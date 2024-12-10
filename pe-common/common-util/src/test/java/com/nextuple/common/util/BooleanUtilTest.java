package com.nextuple.common.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nextuple.common.TestUtil;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.HardExecutionFailureException;
import com.nextuple.common.exception.ServiceUnavailableException;
import feign.FeignException;
import java.io.IOException;
import java.net.SocketException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BooleanUtilTest {
  @Nested
  @DisplayName("checkValidBoolean Tests")
  class CheckValidBooleanTests {
    @DisplayName("when the value is null, it throws CommonServiceException")
    @Test
    void checkValidBooleanWhenValueIsNull() {
      String field = "field";

      Exception exception =
          assertThrows(
              CommonServiceException.class, () -> BooleanUtil.checkValidBooleanValue(null, field));

      assertEquals(
          field + ": Boolean value is empty/null", exception.getMessage(), "Exception message");
    }

    @DisplayName("when the value is invalid, it throws CommonServiceException")
    @Test
    void checkValidBooleanWhenValueIsInvalid() {
      String val = "INVALID";
      String field = "field";

      Exception exception =
          assertThrows(
              CommonServiceException.class, () -> BooleanUtil.checkValidBooleanValue(val, field));

      assertEquals(field + ": Invalid boolean value", exception.getMessage(), "Exception message");
    }

    @DisplayName("when the value is empty string, it throws CommonServiceException")
    @Test
    void checkValidBooleanWhenValueIsEmpty() {
      String val = "";
      String field = "field";

      Exception exception =
          assertThrows(
              CommonServiceException.class, () -> BooleanUtil.checkValidBooleanValue(val, field));

      assertEquals(
          field + ": Boolean value is empty/null", exception.getMessage(), "Exception message");
    }

    @DisplayName("when the value is a boolean, it does not throw exception")
    @Test
    void checkValidBooleanWhenValueIsBoolean() {
      String val = "true";
      String field = "field";

      assertDoesNotThrow(() -> BooleanUtil.checkValidBooleanValue(val, field));
    }
  }

  @Test
  @DisplayName("Is Feign Connection Exception Test with parent class IOException")
  void isFeignConnectionExceptionTest() {
    Assertions.assertFalse(
        BooleanUtil.isFeignConnectionException(
            (FeignException) TestUtil.getConnectionRefusedFeignException(new IOException())));
  }

  @Test
  @DisplayName("Is Feign Connection Exception Test with subclass class SocketException")
  void isFeignConnectionExceptionTestWithSocketException() {
    Assertions.assertTrue(
        BooleanUtil.isFeignConnectionException(
            (FeignException) TestUtil.getConnectionRefusedFeignException(new SocketException())));
  }

  @Test
  @DisplayName("Is Feign Connection Exception Test with subclass class ConnectException")
  void isFeignConnectionExceptionTestWithConnectException() {
    Assertions.assertTrue(
        BooleanUtil.isFeignConnectionException(
            (FeignException)
                TestUtil.getConnectionRefusedFeignException(new java.net.ConnectException())));
  }

  @Test
  @DisplayName("Is Feign Connection Exception Test with not a subclass of IOException")
  void isFeignConnectionExceptionTestWithNotASubclassOfIOException() {
    Assertions.assertFalse(
        BooleanUtil.isFeignConnectionException(
            (FeignException)
                TestUtil.getConnectionRefusedFeignException(new ServiceUnavailableException())));
  }

  @Test
  @DisplayName("Is Service Unavailable Exception Test with ServiceUnavailableException")
  void isServiceUnavailableExceptionTest() {
    Assertions.assertTrue(
        BooleanUtil.isServiceUnavailableException(
            new Exception(new ServiceUnavailableException())));
  }

  @Test
  @DisplayName("Is Service Unavailable Exception Test with IOException")
  void isServiceUnavailableExceptionTestWithIOException() {
    Assertions.assertFalse(
        BooleanUtil.isServiceUnavailableException(new Exception(new IOException())));
  }

  @Test
  @DisplayName("Is Hard Execution Failure Exception Test with HardExecutionFailureException")
  void isHardExecutionFailureExceptionTest() {
    Assertions.assertTrue(
        BooleanUtil.isHardExecutionFailureException(
            new Exception(new HardExecutionFailureException("msg"))));
  }

  @Test
  @DisplayName("Is Hard Execution Failure Exception Test with IOException")
  void isHardExecutionFailureExceptionTestWithIOException() {
    Assertions.assertFalse(
        BooleanUtil.isHardExecutionFailureException(new Exception(new IOException())));
  }
}
