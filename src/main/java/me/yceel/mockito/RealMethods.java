package me.yceel.mockito;

import java.lang.reflect.Method;
import org.mockito.internal.configuration.plugins.Plugins;
import org.mockito.internal.invocation.RealMethod;
import org.mockito.plugins.MemberAccessor;

public class RealMethods {

  public static RealMethod fromMethod(Object target, Method method, Object[] args) {
    return new RealMethodFromMethod(target, method, args);
  }

  private static class RealMethodFromMethod implements RealMethod {

    private final Object target;
    private final Method method;
    private final Object[] args;

    public RealMethodFromMethod(Object target, Method method, Object[] args) {
      this.target = target;
      this.method = method;
      this.args = args;
    }

    @Override
    public boolean isInvokable() {
      return true;
    }

    @Override
    public Object invoke() throws Throwable {
      // todo: rewrite mock static check, not call static methods
      GlobalStaticMockMaker.setMockStatic(method.getDeclaringClass());
      MemberAccessor accessor = Plugins.getMemberAccessor();
      return accessor.invoke(method, target, args);
    }
  }

}
