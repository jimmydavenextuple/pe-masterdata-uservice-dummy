package com.hbc.common.response;

import com.hbc.common.context.CurrentThreadContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseResponseTest {

  @DisplayName(
      "Should set the timestamp to be current Date, success to true, and requestId to be the same as the constructor argument, message and payload to be null")
  @Test
  void builderTest() {
    String correlationId = "correlationId";
    CurrentThreadContext.getLogContext().setCorrelationId(correlationId);
    BaseResponseBuilder baseResponseBuilder = BaseResponse.builder();
    BaseResponse baseResponse = baseResponseBuilder.build();

    assertNotNull(baseResponse.getTimestamp());
    assertTrue(baseResponse.isSuccess());
    assertEquals(correlationId, baseResponse.getRequestId());
    assertNull(baseResponse.getMessage());
    assertNull(baseResponse.getPayload());
  }
}
