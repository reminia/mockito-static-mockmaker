package me.yceel.mockito;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.mockito.internal.creation.bytebuddy.MockMethodInterceptor;
import org.mockito.internal.debugging.LocationImpl;
import org.mockito.internal.invocation.RealMethod;
import org.mockito.invocation.MockHandler;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static org.mockito.internal.invocation.DefaultInvocationFactory.createInvocation;

public class CustomCodeGen implements ClassFileTransformer {
    private final Instrumentation instrument;
    private final ByteBuddy byteBuddy;
    private final Set<Class<?>> mocked = new HashSet<>();

    public CustomCodeGen(Instrumentation instrument) {
        this.instrument = instrument;
        this.byteBuddy = new ByteBuddy().with(TypeValidation.DISABLED).with(Implementation.Context.Disabled.Factory.INSTANCE).with(MethodGraph.Compiler.ForDeclaredMethods.INSTANCE).ignore(isSynthetic().and(not(isConstructor())).or(isDefaultFinalizer()));
        instrument.addTransformer(this, true);
    }

    public void mockStaticClass(Class<?> type) {
        if (!mocked.contains(type)) {
            mocked.add(type);
            try {
                instrument.retransformClasses(type);
            } catch (UnmodifiableClassException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (classBeingRedefined == null) {
            return null;
        }
        return byteBuddy.redefine(classBeingRedefined, ClassFileLocator.Simple.of(classBeingRedefined.getName(), classfileBuffer)).visit(new AsmVisitorWrapper.ForDeclaredMethods().method(isStatic(), Advice.to(StaticMethodAdvice.class))).make().getBytes();
    }

    private static class StaticMethodAdvice {
        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static Callable<?> enter(@Advice.Origin Class<?> type, @Advice.Origin Method method, @Advice.AllArguments Object[] arguments) throws Throwable {
            Map<Class<?>, MockMethodInterceptor> interceptors = GlobalStaticMockMaker.getInterceptors();
            MockMethodInterceptor interceptor = interceptors.get(type);
            MockHandler mockHandler = interceptor.getMockHandler();
            Object ret = mockHandler.handle(createInvocation(type, method, arguments,
                    //todo: fix realmethod
                    RealMethod.IsIllegal.INSTANCE, mockHandler.getMockSettings(), new LocationImpl()));
            return new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return ret;
                }
            };
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object returned, @Advice.Enter Callable<?> mocked) throws Throwable {
            if (mocked != null) {
                returned = mocked.call();
            }
        }
    }
}
