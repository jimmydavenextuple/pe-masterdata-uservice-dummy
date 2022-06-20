package com.nextuple.promise.sourcing.rule.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import com.nextuple.promise.sourcing.rule.exception.common.ExceptionCodeMapping;
import com.nextuple.promise.sourcing.rule.exception.common.PromiseEngineException;
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

class PromiseSourcingRuleExceptionHandlerTest {

  @InjectMocks private PromiseSourcingRuleExceptionHandler promiseSourcingRuleExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void handlePromiseEngineExceptionTest() {
    PromiseEngineException e = new PromiseEngineException(null, ExceptionCodeMapping.ACCEPT, "msg");

    ResponseEntity<ErrorResponse> responseEntity =
        promiseSourcingRuleExceptionHandler.handlePromiseEngineException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(ErrorType.ERROR, responseEntity.getBody().getPayload().getType());
  }

  @Test
  void handleMethodArgumentNotValidException() {
    MethodParameter mp = mock(MethodParameter.class);
    BindingResult bp = mock(BindingResult.class);
    MethodArgumentNotValidException e = new MethodArgumentNotValidException(mp, bp);

    ResponseEntity<ErrorResponse> responseEntity =
        promiseSourcingRuleExceptionHandler.handleMethodArgumentNotValidException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(ErrorType.ERROR, responseEntity.getBody().getPayload().getType());
  }

  @Test
  void handleJsonErrorsTest() {
    HttpMessageNotReadableException e = new HttpMessageNotReadableException("msg");

    ResponseEntity<ErrorResponse> responseEntity =
        promiseSourcingRuleExceptionHandler.handleJsonErrors(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(ErrorType.ERROR, responseEntity.getBody().getPayload().getType());
  }
}
