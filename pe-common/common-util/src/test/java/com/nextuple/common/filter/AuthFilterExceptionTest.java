package com.nextuple.common.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AuthFilterExceptionTest {

  @Test
  void constructTest() {
    AuthFilterException authFilterException = new AuthFilterException("test");
    assertEquals("test", authFilterException.getMessage());
  }
}
