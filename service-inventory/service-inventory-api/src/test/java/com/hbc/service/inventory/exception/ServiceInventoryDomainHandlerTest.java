package com.hbc.service.inventory.exception;

import static org.junit.jupiter.api.Assertions.*;

import com.hbc.common.response.error.ErrorResponse;
import com.hbc.service.inventory.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ServiceInventoryDomainHandlerTest {

  @InjectMocks ServiceInventoryDomainHandler serviceInventoryDomainHandler;

  @Test
  void handleOtherException() {
    ServiceInventoryDomainException exception =
        new ServiceInventoryDomainException("error", TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);

    ResponseEntity<ErrorResponse> errorResponse =
        serviceInventoryDomainHandler.handleOtherException(exception);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatusCode());
    assertEquals("Internal Server Error", errorResponse.getBody().getMessage());
  }
}
