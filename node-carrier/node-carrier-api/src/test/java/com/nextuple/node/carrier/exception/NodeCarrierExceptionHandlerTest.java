package com.nextuple.node.carrier.exception;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.node.carrier.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class NodeCarrierExceptionHandlerTest {

  @InjectMocks TestUtil testUtil;

  @InjectMocks NodeCarrierExceptionHandler nodeCarrierExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test for handling node carrier domain exception")
  void handleNodeCarrierDomainException() {
    NodeCarrierDomainException exception =
        new NodeCarrierDomainException(
            "Internal Server Error",
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        nodeCarrierExceptionHandler.handleOtherException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }
}
