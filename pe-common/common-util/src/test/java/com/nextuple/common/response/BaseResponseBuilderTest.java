package com.nextuple.common.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nextuple.common.context.CurrentThreadContext;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BaseResponseBuilderTest {

  @DisplayName("Should return false if not successful")
  @Test
  void notSuccessTest() {
    BaseResponseBuilder baseResponseBuilder = new BaseResponseBuilder();
    baseResponseBuilder.success(false);
    BaseResponse baseResponse = baseResponseBuilder.build();
    assertFalse(baseResponse.isSuccess(), "Not successful");
  }

  @DisplayName("Should return false if not successful")
  @Test
  void successTest() {
    BaseResponseBuilder baseResponseBuilder = new BaseResponseBuilder();
    baseResponseBuilder.success(true);
    BaseResponse baseResponse = baseResponseBuilder.build();
    assertTrue(baseResponse.isSuccess(), "Successful");
  }

  @DisplayName("Should return the request ID that has been previously set")
  @Test
  void requestIdTest() {
    BaseResponseBuilder baseResponseBuilder = new BaseResponseBuilder();
    String requestId = "requestId";
    baseResponseBuilder.requestId(requestId);
    BaseResponse baseResponse = baseResponseBuilder.build();
    assertEquals(requestId, baseResponse.getRequestId(), "Request ID");
  }

  @DisplayName("Should return null since request ID has not been set")
  @Test
  void requestIdNullTest() {
    CurrentThreadContext.getLogContext().setCorrelationId(null);
    BaseResponseBuilder baseResponseBuilder = new BaseResponseBuilder();
    BaseResponse baseResponse = baseResponseBuilder.build();
    assertNull(baseResponse.getRequestId(), "Request ID");
  }

  @DisplayName("Should return the timestamp that has been previously set")
  @Test
  void timestampTest() {
    BaseResponseBuilder baseResponseBuilder = new BaseResponseBuilder();
    Date timestamp = new Date();
    baseResponseBuilder.timestamp(timestamp);
    BaseResponse baseResponse = baseResponseBuilder.build();
    assertEquals(timestamp.getTime(), baseResponse.getTimestamp(), "Timestamp");
  }

  @DisplayName("Should return the message that has been previously set")
  @Test
  void messageTest() {
    BaseResponseBuilder baseResponseBuilder = new BaseResponseBuilder();
    String message = "message";
    baseResponseBuilder.message(message);
    BaseResponse baseResponse = baseResponseBuilder.build();
    assertEquals(message, baseResponse.getMessage(), "Message");
  }

  @DisplayName("Should return null since message has not been set")
  @Test
  void messageNullTest() {
    BaseResponseBuilder baseResponseBuilder = new BaseResponseBuilder();
    BaseResponse baseResponse = baseResponseBuilder.build();
    assertNull(baseResponse.getMessage(), "Message");
  }

  @DisplayName("Should return the payload that has been previously set")
  @Test
  void payloadTest() {
    BaseResponseBuilder baseResponseBuilder = new BaseResponseBuilder();
    String payload = "payload";
    baseResponseBuilder.payload(payload);
    BaseResponse baseResponse = baseResponseBuilder.build();
    assertEquals(payload, baseResponse.getPayload(), "Payload");
  }

  @DisplayName("Should return null since payload has not been set")
  @Test
  void payloadNullTest() {
    BaseResponseBuilder baseResponseBuilder = new BaseResponseBuilder();
    BaseResponse baseResponse = baseResponseBuilder.build();
    assertNull(baseResponse.getPayload(), "Payload");
  }
}
