package com.nextuple.carrier.exception;

import com.nextuple.carrier.TestUtil;
import com.nextuple.common.response.error.ErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class CarrierServiceExceptionHandlerTest {

  @InjectMocks TestUtil testUtil;

  @InjectMocks CarrierServiceExceptionHandler carrierServiceExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test for handling carrier service domain exception")
  void handleNodeDomainException() {
    CarrierServiceDomainException exception =
        new CarrierServiceDomainException(
            "Internal Server Error",
            TestUtil.CARRIER_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.ORG_ID);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        carrierServiceExceptionHandler.handleOtherException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }
}
