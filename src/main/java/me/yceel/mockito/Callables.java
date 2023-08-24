package me.yceel.mockito;

import java.util.concurrent.Callable;

public class Callables {

  public static Callable<Object> direct(Object obj) {
    return new DirectCallable(obj);
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
}
