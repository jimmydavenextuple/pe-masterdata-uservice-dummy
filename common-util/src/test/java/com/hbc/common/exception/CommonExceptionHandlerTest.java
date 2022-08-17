package com.hbc.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.hbc.common.context.CurrentThreadContext;
import com.hbc.common.context.LogContext;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.enums.ExceptionCodeMapping;
import com.hbc.common.response.error.ErrorResponse;
import com.hbc.common.response.error.ErrorType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

class CommonExceptionHandlerTest {

  @InjectMocks private CommonExceptionHandler commonExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        commonExceptionHandler,
        "slf4jLogger",
        LoggerFactory.getLogger(CommonExceptionHandler.class));
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
  void handleJsonErrorsInvalidFormatExceptionTest() {
    InvalidFormatException cause = mock(InvalidFormatException.class);
    Reference reference = mock(Reference.class);

    List<Reference> referenceList = new ArrayList<>();
    referenceList.add(reference);
    when(cause.getPath()).thenReturn(referenceList);
    when(cause.getMessage()).thenReturn("Cannot coerce empty String");
    when(reference.getFieldName()).thenReturn("ExampleField");
    HttpMessageNotReadableException e = new HttpMessageNotReadableException("msg", cause);

    ResponseEntity<ErrorResponse> responseEntity = commonExceptionHandler.handleJsonErrors(e);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(ErrorType.ERROR, responseEntity.getBody().getPayload().getType());

    when(cause.getMessage()).thenReturn("values accepted for Enum class:");
    responseEntity = commonExceptionHandler.handleJsonErrors(e);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(ErrorType.ERROR, responseEntity.getBody().getPayload().getType());

    when(cause.getMessage())
        .thenReturn("not compatible with any of standard forms 2022-01-01'T'12:12:12");
    responseEntity = commonExceptionHandler.handleJsonErrors(e);
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

  @Test
  void handleRuntimeExceptionTest() {
    IndexOutOfBoundsException e = new IndexOutOfBoundsException();

    ResponseEntity<ErrorResponse> responseEntity = commonExceptionHandler.handleRuntimeException(e);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    assertEquals(ErrorType.ERROR, responseEntity.getBody().getPayload().getType());
  }

}
