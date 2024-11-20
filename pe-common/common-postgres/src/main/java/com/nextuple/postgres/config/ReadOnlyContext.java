package com.nextuple.postgres.config;

import java.util.concurrent.atomic.AtomicInteger;

public class ReadOnlyContext {
  private static final ThreadLocal<AtomicInteger> READ_ONLY_LEVEL =
      ThreadLocal.withInitial(() -> new AtomicInteger(0));

  // default constructor

  public static boolean isReadOnly() {
    return READ_ONLY_LEVEL.get().get() > 0;
  }

  public static void enter() {
    READ_ONLY_LEVEL.get().incrementAndGet();
  }

  public static void exit() {
    READ_ONLY_LEVEL.get().decrementAndGet();
  }

  public void unload() {
    READ_ONLY_LEVEL.remove(); // Compliant
  }
}
