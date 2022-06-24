package com.nextuple.common.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthFilterExceptionTest {

  @Test
  @DisplayName("Testing AuthFilterException")
  void constructTest() {
    AuthFilterException authFilterException = new AuthFilterException("test");
    assertEquals("test", authFilterException.getMessage());
  }
}
