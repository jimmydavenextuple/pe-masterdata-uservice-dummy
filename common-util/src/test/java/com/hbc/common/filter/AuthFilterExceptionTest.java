package com.hbc.common.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AuthFilterExceptionTest {

  @Test
  void constructTest() {
    Throwable throwable = new Throwable();
    AuthFilterException authFilterException =
        new AuthFilterException("test", throwable, true, false);
    assertEquals("test", authFilterException.getMessage());
    assertEquals(throwable, authFilterException.getCause());
  }
}
