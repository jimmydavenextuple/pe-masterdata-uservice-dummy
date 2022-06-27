package com.nextuple.weightage.configuration.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import com.nextuple.weightage.configuration.exception.common.ExceptionCodeMapping;
import com.nextuple.weightage.configuration.exception.common.PromiseEngineException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class WeightageConfigurationExceptionHandlerTest {

  @InjectMocks
  private WeightageConfigurationExceptionHandler weightageConfigurationExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void handlePromiseEngineExceptionTest() {
    PromiseEngineException e = new PromiseEngineException(null, ExceptionCodeMapping.ACCEPT, "msg");

    ResponseEntity<ErrorResponse> responseEntity =
        weightageConfigurationExceptionHandler.handlePromiseEngineException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(ErrorType.ERROR, responseEntity.getBody().getPayload().getType());
  }
}
