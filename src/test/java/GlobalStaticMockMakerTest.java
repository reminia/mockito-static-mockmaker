import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class GlobalStaticMockMakerTest {

  static class Foo {

    static int foo() {
      return 1;
    }

    static int bar() {
      return 2;
    }

    int baz() {
      return 3;
    }

    int quz() {
      return 4;
    }
  }

  @Test
  @DisplayName("Test mockStatic basics")
  public void testMockStatic() {
    try (MockedStatic<Foo> mockedFoo = Mockito.mockStatic(Foo.class)) {
      mockedFoo.when(Foo::foo).thenReturn(3);
      assertEquals(3, Foo.foo());
      assertEquals(0, Foo.bar());
    }
  }

  @Test
  @DisplayName("Test mockStatic works globally in multiple threads")
  public void testMockStaticGlobally() throws Exception {
    try (MockedStatic<Foo> mockedFoo = Mockito.mockStatic(Foo.class)) {
      mockedFoo.when(Foo::foo).thenReturn(3);
      assertEquals(3, Foo.foo());

      ExecutorService es = Executors.newFixedThreadPool(1);
      Future<Integer> res = es.submit(Foo::foo);
      assertEquals(3, res.get());
    }
  }

  @Test
  @DisplayName("Test mockStatic with real method enabled")
  public void testMockStaticRealMethod() {
    try (MockedStatic<Foo> mockedFoo = Mockito.mockStatic(Foo.class, Mockito.CALLS_REAL_METHODS)) {
      mockedFoo.when(Foo::foo).thenReturn(3);
      assertEquals(3, Foo.foo());
      assertEquals(2, Foo.bar());
    }
  }

  @Test
  @DisplayName("Test instance mock is not broken")
  public void testInstanceMock() {
    Foo foo = Mockito.mock(Foo.class);
    Mockito.when(foo.baz()).thenReturn(4);
    assertEquals(4, foo.baz());
  }

  @Test
  @DisplayName("Test instance spy is not broken")
  public void testInstanceSpy() {
    Foo spied = Mockito.spy(new Foo());
    Mockito.when(spied.baz()).thenReturn(4);
    assertEquals(4, spied.baz());
    assertEquals(4, spied.quz());
  }

  @Test
  @DisplayName("Test mixed mockStatic and mock instance")
  public void testMixedStaticAndInstanceMethod() {
    Foo foo = Mockito.mock(Foo.class);
    Mockito.when(foo.baz()).thenReturn(4);
    try (MockedStatic<Foo> mockedFoo = Mockito.mockStatic(Foo.class, Mockito.CALLS_REAL_METHODS)) {
      mockedFoo.when(Foo::foo).thenReturn(3);
      assertEquals(3, Foo.foo());
      assertEquals(4, foo.baz());
    }
  }


}
