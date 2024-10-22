package com.nextuple.common.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.common.response.error.ErrorPayload;
import com.nextuple.common.response.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ErrorPayloadTest {
  @DisplayName("Should set everything to null with the no parameter constructor")
  @Test
  void noParameterConstructorTest() {
    ErrorPayload errorPayload = new ErrorPayload();

    assertNull(errorPayload.getCode(), "Code should be null");
    assertNull(errorPayload.getErrors(), "Errors should be null");
    assertNull(errorPayload.getException(), "Exceptions should be null");
    assertNull(errorPayload.getFields(), "Fields should be null");
    assertNull(errorPayload.getType(), "Type should be null");
  }

  @DisplayName(
      "Should set the `errorType` and `code` to match arguments from the constructor. Other variables should be null")
  @Test
  void errorTypeAndCodeConstructorTest() {
    ErrorType errorType = ErrorType.ERROR;
    Integer code = 1;
    ErrorPayload errorPayload = new ErrorPayload(errorType, code);

    assertEquals(code, errorPayload.getCode(), "Code");
    assertNull(errorPayload.getErrors(), "Errors should be null");
    assertNull(errorPayload.getException(), "Exceptions should be null");
    assertNull(errorPayload.getFields(), "Fields should be null");
    assertEquals(errorType, errorPayload.getType(), "Error Type");
  }
}
