package me.yceel.mockito;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import org.mockito.internal.configuration.plugins.Plugins;
import org.mockito.plugins.MemberAccessor;

public class Callables {

  public static Callable<Object> direct(Object value) {
    return new DirectCallable(value);
  }

  public static Callable<Object> fromMethod(Object target, Method method, Object[] args) {
    return new MethodInvokeCallable(target, method, args);
  }

  private static class DirectCallable implements Callable<Object> {

    private final Object ret;

    public DirectCallable(Object ret) {
      this.ret = ret;
    }

    @Override
    public Object call() throws Exception {
      return ret;
    }
  }

  private static class MethodInvokeCallable implements Callable<Object> {

    private final Object target;
    private final Method method;
    private final Object[] args;

    public MethodInvokeCallable(Object target, Method method, Object[] args) {
      this.target = target;
      this.method = method;
      this.args = args;
    }

    @Override
    public Object call() throws Exception {
      // todo: rewrite mock static check, not call static methods
      GlobalStaticMockMaker.setMockStatic(method.getDeclaringClass());
      MemberAccessor accessor = Plugins.getMemberAccessor();
      return accessor.invoke(method, target, args);
    }
  }
}
