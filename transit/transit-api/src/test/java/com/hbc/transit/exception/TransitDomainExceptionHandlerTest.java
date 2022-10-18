package com.hbc.transit.exception;

import com.hbc.common.response.error.ErrorResponse;
import com.hbc.transit.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class TransitDomainExceptionHandlerTest {

  @InjectMocks TestUtil testUtil;

  @InjectMocks TransitExceptionHandler transitExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test for handling transit domain exception")
  void handleTransitDomainException() {
    TransitDomainException exception =
        new TransitDomainException(
            "Internal Server Error",
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        transitExceptionHandler.handleOtherException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }

  @Test
  @DisplayName("Test for handling transit buffer req job ref domain exception")
  void handleTransitBufferReqJobRefDomainException() {
    TransitBufferReqJobRefDomainException exception =
        new TransitBufferReqJobRefDomainException(
            "Internal Server Error",
            TestUtil.TRANS_BUFFER_REQ_JOB_REF_ID,
            TestUtil.TRANS_BUFFER_REQ_JOB_REF_EXT_REF_ID);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        transitExceptionHandler.handleTransitBufferReqJobRefDomainException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }
}
