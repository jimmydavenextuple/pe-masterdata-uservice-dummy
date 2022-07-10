package com.hbc.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.hbc.common.ExceptionCodeMapping;
import com.hbc.common.response.error.ErrorResponse;
import com.hbc.common.response.error.ErrorType;
import java.io.IOException;
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

class CommonExceptionHandlerTest {

  @InjectMocks private CommonExceptionHandler commonExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void handleMethodArgumentNotValidException() {
    MethodParameter mp = mock(MethodParameter.class);
    BindingResult bp = mock(BindingResult.class);
    MethodArgumentNotValidException e = new MethodArgumentNotValidException(mp, bp);

    ResponseEntity<ErrorResponse> responseEntity =
        commonExceptionHandler.handleMethodArgumentNotValidException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(ErrorType.ERROR, responseEntity.getBody().getPayload().getType());
  }

  @Test
  void handleJsonErrorsTest() {
    HttpMessageNotReadableException e = new HttpMessageNotReadableException("msg");

    ResponseEntity<ErrorResponse> responseEntity = commonExceptionHandler.handleJsonErrors(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(ErrorType.ERROR, responseEntity.getBody().getPayload().getType());
  }

  @Test
  void handleCommonServiceExceptionTest() {
    CommonServiceException commonServiceException =
        new CommonServiceException("error", HttpStatus.BAD_REQUEST, 0x1771, null);

    ResponseEntity<ErrorResponse> responseEntity =
        commonExceptionHandler.handleCommonServiceException(commonServiceException);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Test
  void handleIOExceptionTest() {
    IOException ioException = new IOException();

    ResponseEntity<ErrorResponse> responseEntity =
        commonExceptionHandler.handleIOException(ioException);

    assertEquals(ErrorType.ERROR, responseEntity.getBody().getPayload().getType());
  }

  @Test
  void handlePromiseEngineExceptionTest() {
    PromiseEngineException e = new PromiseEngineException(null, ExceptionCodeMapping.ACCEPT, "msg");

    ResponseEntity<ErrorResponse> responseEntity =
            commonExceptionHandler.handlePromiseEngineException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(ErrorType.ERROR, responseEntity.getBody().getPayload().getType());
  }
}
