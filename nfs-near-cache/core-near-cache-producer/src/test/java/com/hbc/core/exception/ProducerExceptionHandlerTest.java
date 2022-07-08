package com.hbc.core.exception;

import static org.junit.jupiter.api.Assertions.*;

import com.hbc.common.response.error.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ProducerExceptionHandlerTest {

  @InjectMocks ProducerExceptionHandler producerExceptionHandler;

  @Test
  void handleLocalCacheUpdateEventException() {
    LocalCacheUpdateEventException exception =
        new LocalCacheUpdateEventException(
            "Error occurred while publishing LocalCacheUpdate Event", "entityName");

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        producerExceptionHandler.handleLocalCacheUpdateEventException(exception);

    assertEquals(HttpStatus.BAD_REQUEST, errorResponseResponseEntity.getStatusCode());
    assertEquals(
        "Error occurred while publishing LocalCacheUpdate Event",
        errorResponseResponseEntity.getBody().getMessage());
  }
}
