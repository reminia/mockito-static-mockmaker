package me.yceel.mockito;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.creation.bytebuddy.InlineByteBuddyMockMaker;
import org.mockito.internal.creation.bytebuddy.MockMethodInterceptor;
import org.mockito.invocation.MockHandler;
import org.mockito.mock.MockCreationSettings;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.internal.util.StringUtil.join;

public class GlobalStaticMockMaker extends InlineByteBuddyMockMaker {

  private static final Map<Class<?>, MockMethodInterceptor> interceptors = new HashMap<>();
  //todo: access needs to be synchronized
  private static final Map<Class<?>, Boolean> mockStatic = new HashMap<>();
  private final CustomCodeGen codeGen;

  public GlobalStaticMockMaker() {
    this.codeGen = new CustomCodeGen(ByteBuddyAgent.getInstrumentation());
  }

  public static boolean isMockStatic(Class<?> type) {
    if (!mockStatic.containsKey(type)) {
      return true;
    } else {
      boolean ret = mockStatic.get(type);
      mockStatic.remove(type);
      return ret;
    }
  }

  public static void setMockStatic(Class<?> type) {
    mockStatic.put(type, false);
  }


  public static Map<Class<?>, MockMethodInterceptor> getInterceptors() {
    return interceptors;
  }

  @Override
  public <T> StaticMockControl<T> createStaticMock(Class<T> type, MockCreationSettings<T> settings,
      MockHandler handler) {
    codeGen.mockStaticClass(type);
    return new CustomInlineController<>(type, interceptors, settings, handler);
  }

  @Override
  public MockHandler getHandler(Object mock) {
    if (mock instanceof Class<?>) {
      return interceptors.get(mock).getMockHandler();
    } else {
      return super.getHandler(mock);
    }
  }

  private static class CustomInlineController<T> implements StaticMockControl<T> {

    private final Class<T> type;

    private final Map<Class<?>, MockMethodInterceptor> interceptors;

    private final MockCreationSettings<T> settings;

    private final MockHandler handler;

    private CustomInlineController(Class<T> type, Map<Class<?>, MockMethodInterceptor> interceptors,
        MockCreationSettings<T> settings, MockHandler handler) {
      this.type = type;
      this.interceptors = interceptors;
      this.settings = settings;
      this.handler = handler;
    }

    @Override
    public Class<T> getType() {
      return type;
    }

    @Override
    public void enable() {
      if (interceptors.putIfAbsent(type, new MockMethodInterceptor(handler, settings)) != null) {
        throw new MockitoException(join("For " + type.getName()
                + ", static mocking is already registered in the current thread", "",
            "To create a new mock, the existing static mock registration must be deregistered"));
      }
    }

    @Override
    public void disable() {
      if (interceptors.remove(type) == null) {
        throw new MockitoException(join("Could not deregister " + type.getName()
                + " as a static mock since it is not currently registered", "",
            "To register a static mock, use Mockito.mockStatic(" + type.getSimpleName()
                + ".class)"));
      }
    }
  }

}
