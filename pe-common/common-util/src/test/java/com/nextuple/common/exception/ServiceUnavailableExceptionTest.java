package com.nextuple.common.exception;

import static com.nextuple.common.constants.CommonConstants.SERVER_UNAVAILABLE_ERROR_MESSAGE;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ServiceUnavailableExceptionTest {

  @Test
  @DisplayName("when the exception is created with message, it returns the message")
  void serviceUnavailableExceptionWithMessage() {
    String message = "message";
    ServiceUnavailableException serviceUnavailableException =
        new ServiceUnavailableException(message);
    Assertions.assertEquals(message, serviceUnavailableException.getMessage());
  }

  @Test
  @DisplayName("when the exception is created without message, it returns the default message")
  void serviceUnavailableExceptionWithoutMessage() {
    ServiceUnavailableException serviceUnavailableException = new ServiceUnavailableException();
    Assertions.assertEquals(
        SERVER_UNAVAILABLE_ERROR_MESSAGE, serviceUnavailableException.getMessage());
  }

  @Test
  @DisplayName("when the exception is created with cause, it returns the cause")
  void serviceUnavailableExceptionWithCause() {
    Throwable cause = new Throwable();
    ServiceUnavailableException serviceUnavailableException =
        new ServiceUnavailableException(cause);
    Assertions.assertEquals(cause, serviceUnavailableException.getCause());
  }
}
