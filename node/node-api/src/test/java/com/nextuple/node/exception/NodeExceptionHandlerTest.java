package com.nextuple.node.exception;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.node.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class NodeExceptionHandlerTest {

  @InjectMocks TestUtil testUtil;

  @InjectMocks NodeExceptionHandler nodeExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test for handling node domain exception")
  void handleNodeDomainException() {
    NodeDomainException exception =
        new NodeDomainException("Internal Server Error", TestUtil.NODE_ID, TestUtil.ORG_ID);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        nodeExceptionHandler.handleOtherException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }
}
