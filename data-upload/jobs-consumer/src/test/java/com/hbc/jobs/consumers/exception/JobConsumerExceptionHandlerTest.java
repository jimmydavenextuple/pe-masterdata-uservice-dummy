package com.hbc.jobs.consumers.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.response.error.ErrorResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
class JobConsumerExceptionHandlerTest {
  private final String msg = "msg";

  @InjectMocks private JobConsumerExceptionHandler handler;

  @Test
  void handleDuplicateJobException() {
    DuplicateJobException e = mock(DuplicateJobException.class);
    when(e.getMessage()).thenReturn(msg);

    ResponseEntity<Object> responseEntity = handler.handleDuplicateJobException(e);
    verifyResponse(e, responseEntity);
  }

  @Test
  void handleJobRecordDomainException() {
    JobRecordDomainException e = mock(JobRecordDomainException.class);
    when(e.getMessage()).thenReturn(msg);

    ResponseEntity<Object> responseEntity = handler.handleJobRecordDomainException(e);
    verifyResponse(e, responseEntity);
  }

  @Test
  void handleJobDashboardException() {
    PublishJobEventException e = mock(PublishJobEventException.class);
    when(e.getMessage()).thenReturn(msg);

    ResponseEntity<Object> responseEntity = handler.handleJobDashboardException(e);
    verifyResponse(e, responseEntity);
  }

  @Test
  void handleJobDomainException() {
    JobDomainException e = mock(JobDomainException.class);
    when(e.getMessage()).thenReturn(msg);

    ResponseEntity<Object> responseEntity = handler.handleJobDomainException(e);
    verifyResponse(e, responseEntity);
  }

  @Test
  void handleJobException() {
    JobException e = mock(JobException.class);
    when(e.getMessage()).thenReturn(msg);

    ResponseEntity<Object> responseEntity = handler.handleJobException(e);
    verifyResponse(e, responseEntity);
  }

  @Test
  void handleTransitMapperException() {
    TransitMapperException e = mock(TransitMapperException.class);
    when(e.getMessage()).thenReturn(msg);

    ResponseEntity<ErrorResponse> responseEntity = handler.handleTransitMapperException(e);
    verifyErrorResponse(e, responseEntity);
  }

  @Test
  void handleFeignClientMapperException() {
    FeignClientMapperException e = mock(FeignClientMapperException.class);
    when(e.getMessage()).thenReturn(msg);

    ResponseEntity<ErrorResponse> responseEntity = handler.handleFeignClientMapperException(e);
    verifyErrorResponse(e, responseEntity);
  }

  @Test
  void handleNodeCarrierMapperException() {
    NodeCarrierMapperException e = mock(NodeCarrierMapperException.class);
    when(e.getMessage()).thenReturn(msg);

    ResponseEntity<ErrorResponse> responseEntity = handler.handleNodeCarrierMapperException(e);
    verifyErrorResponse(e, responseEntity);
  }

  private void verifyErrorResponse(Exception e, ResponseEntity<ErrorResponse> responseEntity) {
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(400, responseEntity.getStatusCodeValue());
    assertEquals(
        ErrorResponse.class.toString(),
        Objects.requireNonNull(responseEntity.getBody()).getClass().toString());
    verify(e, times(1)).getMessage();
  }

  @Test
  void handleInvalidJobTypeException() {
    InvalidJobTypeException e = mock(InvalidJobTypeException.class);
    when(e.getMessage()).thenReturn(msg);
    ResponseEntity<Object> responseEntity = handler.handleJobIdNotFoundException(e);
    verifyResponse(e, responseEntity);
  }

  private void verifyResponse(Exception e, ResponseEntity<Object> responseEntity) {
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(400, responseEntity.getStatusCodeValue());
    assertEquals(
        ErrorResponse.class.toString(),
        Objects.requireNonNull(responseEntity.getBody()).getClass().toString());
    verify(e, times(1)).getMessage();
  }

  @Test
  void handleMethodArgumentNotValidException() {
    MethodArgumentNotValidException e = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = mock(FieldError.class);
    when(fieldError.getField()).thenReturn("testField");
    when(fieldError.getDefaultMessage()).thenReturn("testDefault");
    when(fieldError.getRejectedValue()).thenReturn("testReject");
    List<FieldError> fieldErrors = new ArrayList<>(Collections.singletonList(fieldError));
    when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
    when(e.getBindingResult()).thenReturn(bindingResult);

    ResponseEntity<Object> responseEntity = handler.handleMethodArgumentNotValidException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(400, responseEntity.getStatusCodeValue());
    assertEquals(
        ErrorResponse.class.toString(),
        Objects.requireNonNull(responseEntity.getBody()).getClass().toString());
  }
}
