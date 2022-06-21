package com.nextuple.weightage.configuration.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import com.nextuple.weightage.configuration.exception.common.ExceptionCodeMapping;
import com.nextuple.weightage.configuration.exception.common.PromiseEngineException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

  @Test
  void handleMethodArgumentNotValidException() {
    MethodParameter mp = mock(MethodParameter.class);
    BindingResult bp = mock(BindingResult.class);
    MethodArgumentNotValidException e = new MethodArgumentNotValidException(mp, bp);

    ResponseEntity<ErrorResponse> responseEntity =
        weightageConfigurationExceptionHandler.handleMethodArgumentNotValidException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(ErrorType.ERROR, responseEntity.getBody().getPayload().getType());
  }

  @Test
  void handleJsonErrorsTest() {
    HttpMessageNotReadableException e = new HttpMessageNotReadableException("msg");

    ResponseEntity<ErrorResponse> responseEntity =
        weightageConfigurationExceptionHandler.handleJsonErrors(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(ErrorType.ERROR, responseEntity.getBody().getPayload().getType());
  }
}
