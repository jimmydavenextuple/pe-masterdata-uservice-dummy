package com.nextuple.common.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.response.error.ErrorData;
import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import com.nextuple.common.response.error.FieldError;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ErrorResponseTest {

  @DisplayName(
      "Should initialize success to be false, error response to be null, and timestamp to be current Date based on empty constructor")
  @Test
  void noParameterConstructorTest() {
    CurrentThreadContext.getLogContext().setCorrelationId(null);
    ErrorResponse errorResponse = new ErrorResponse();

    assertFalse(errorResponse.isSuccess(), "Success should be false");
    assertNull(errorResponse.getRequestId(), "RequestId should be null");
    Assertions.assertNotNull(errorResponse.getTimestamp(), "Timestamp should be current time");
  }

  @DisplayName(
      "Should initialize `errorType` and `code` from the arguments in the errorResponseBuilder function")
  @Test
  void errorResponseBuilderErrorTypeCodeTest() {
    ErrorResponse errorResponse = new ErrorResponse();

    ErrorType errorType = ErrorType.ERROR;
    Integer code = 1;

    ErrorResponse.ErrorResponseBuilder errorResponseBuilder =
        errorResponse.builder(errorType, code);

    ErrorResponse actual = errorResponseBuilder.build();

    assertEquals(errorType, actual.getPayload().getType(), "Error type");
    assertEquals(code, actual.getPayload().getCode(), "Code");
  }

  @DisplayName(
      "Should initialize `errorType`,`code`,`timestamp`, and `message` from the arguments in the errorResponseBuilder function")
  @Test
  void errorResponseBuilderTest() {
    ErrorType errorType = ErrorType.ERROR;
    Integer code = 1;
    Date timestamp = new Date();
    String message = "message";

    ErrorResponse.ErrorResponseBuilder errorResponseBuilder =
        new ErrorResponse.ErrorResponseBuilder(errorType, timestamp, code, message);

    ErrorResponse actual = errorResponseBuilder.build();

    assertEquals(errorType, actual.getPayload().getType(), "Error type");
    assertEquals(code, actual.getPayload().getCode(), "Code");
    assertEquals(timestamp.getTime(), actual.getTimestamp(), "Timestamp");
    assertEquals(message, actual.getMessage(), "Message");
  }

  @DisplayName("Should set the `payload` to be null if exception is null")
  @Test
  void exceptionNullTest() {
    ErrorType errorType = ErrorType.ERROR;
    Integer code = 1;

    ErrorResponse.ErrorResponseBuilder expected =
        new ErrorResponse.ErrorResponseBuilder(errorType, code);

    ErrorResponse.ErrorResponseBuilder builder = expected.exception(null);

    ErrorResponse actual = builder.build();

    assertEquals(errorType, actual.getPayload().getType(), "Error Type");
    assertEquals(code, actual.getPayload().getCode(), "Code");
    assertNull(actual.getPayload().getException(), "Exception");
  }

  @DisplayName("Should set the `requestId` from argument passed in the requestId function")
  @Test
  void requestIdTest() {
    ErrorType errorType = ErrorType.ERROR;
    Integer code = 1;
    String requestId = "requestId";

    ErrorResponse.ErrorResponseBuilder expected =
        new ErrorResponse.ErrorResponseBuilder(errorType, code);

    ErrorResponse.ErrorResponseBuilder builder = expected.requestId(requestId);

    ErrorResponse actual = builder.build();
    assertEquals(errorType, actual.getPayload().getType(), "Error Type");
    assertEquals(code, actual.getPayload().getCode(), "Code");
    assertEquals(requestId, actual.getRequestId(), "Request Id");
  }

  @DisplayName("Should set the `timestamp` from argument passed in the timestamp function")
  @Test
  void timestampTest() {
    ErrorType errorType = ErrorType.ERROR;
    Integer code = 1;
    Date timestamp = new Date();

    ErrorResponse.ErrorResponseBuilder expected =
        new ErrorResponse.ErrorResponseBuilder(errorType, code);

    ErrorResponse.ErrorResponseBuilder builder = expected.timestamp(timestamp);

    ErrorResponse actual = builder.build();

    assertEquals(errorType, actual.getPayload().getType(), "Error Type");
    assertEquals(code, actual.getPayload().getCode(), "Code");
    assertEquals(timestamp.getTime(), actual.getTimestamp(), "Timestamp");
  }

  @DisplayName("Test the ErrorResponseBuilder message method")
  @Test
  void messageTest() {
    ErrorType errorType = ErrorType.ERROR;
    Integer code = 1;
    String message = "message";

    ErrorResponse.ErrorResponseBuilder expected =
        new ErrorResponse.ErrorResponseBuilder(errorType, code);

    ErrorResponse.ErrorResponseBuilder builder = expected.message(message);

    ErrorResponse actual = builder.build();
    assertEquals(errorType, actual.getPayload().getType(), "Error Type");
    assertEquals(code, actual.getPayload().getCode(), "Code");
    assertEquals(message, actual.getMessage(), "Message");
  }

  @DisplayName(
      "Should build an Error Response that initializes the `errorType` and `code` from the arguments in the constructor")
  @Test
  void codeTest() {
    ErrorType errorType = ErrorType.ERROR;
    Integer code = 1;
    Integer newCode = 2;

    ErrorResponse.ErrorResponseBuilder expected =
        new ErrorResponse.ErrorResponseBuilder(errorType, code);

    ErrorResponse.ErrorResponseBuilder builder = expected.code(newCode);

    ErrorResponse actual = builder.build();
    assertEquals(errorType, actual.getPayload().getType(), "Error Type");
    assertNotEquals(code, actual.getPayload().getCode(), "Old Code");
    assertEquals(newCode, actual.getPayload().getCode(), "New Code");
  }

  @DisplayName(
      "Should build an Error Response that initializes the `errorType` and `code` from the arguments in the constructor")
  @Test
  void errorTypeTest() {
    ErrorType errorType = ErrorType.ERROR;
    Integer code = 1;
    ErrorType newErrorType = ErrorType.WARNING;

    ErrorResponse.ErrorResponseBuilder expected =
        new ErrorResponse.ErrorResponseBuilder(errorType, code);

    ErrorResponse.ErrorResponseBuilder builder = expected.type(newErrorType);

    ErrorResponse actual = builder.build();
    assertEquals(code, actual.getPayload().getCode(), "Code");
    assertEquals(newErrorType, actual.getPayload().getType(), "New Error Type");
    assertNotEquals(errorType, actual.getPayload().getType(), "Old Error TYpe");
  }

  @DisplayName(
      "Should initialize or add into a map that contains the key `field` and value `fieldError` which all come from the function arguments")
  @Test
  void errorFieldTest() {
    ErrorType errorType = ErrorType.ERROR;
    Integer code = 1;
    String field = "field";
    FieldError fieldError = new FieldError();

    ErrorResponse.ErrorResponseBuilder expected =
        new ErrorResponse.ErrorResponseBuilder(errorType, code);

    ErrorResponse.ErrorResponseBuilder builder = expected.errorField(field, fieldError);
    ErrorResponse actual = builder.build();
    assertEquals(fieldError, actual.getPayload().getFields().get(field), "Field Error");
  }

  @DisplayName(
      "Should initialize a map or add into a  map that contains the key `reference` and value `errorData` which all come from the function arguments")
  @Test
  void errorDataTest() {
    ErrorType errorType = ErrorType.ERROR;
    Integer code = 1;
    String reference = "reference";
    ErrorData errorData = new ErrorData();

    ErrorResponse.ErrorResponseBuilder expected =
        new ErrorResponse.ErrorResponseBuilder(errorType, code);

    ErrorResponse.ErrorResponseBuilder builder = expected.errorData(reference, errorData);
    ErrorResponse actual = builder.build();
    assertEquals(errorData, actual.getPayload().getErrors().get(reference));
  }
}
