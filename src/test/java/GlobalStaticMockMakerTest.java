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

  }


}
