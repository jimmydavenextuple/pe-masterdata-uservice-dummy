package com.hbc.jobs.dashboard.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.response.error.ErrorResponse;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
class JobDashboardExceptionHandlerTest {

  @InjectMocks private JobDashboardExceptionHandler jobDashboardExceptionHandler;

  @Test
  void handleMethodArgumentNotValidException() {
    BindingResult bindingResult = new BeanPropertyBindingResult("", "objectName");
    FieldError error = new FieldError("objectName", "field", "defaultMsg");

    bindingResult.addError(error);

    MethodArgumentNotValidException e = new MethodArgumentNotValidException(null, bindingResult);
    ResponseEntity<ErrorResponse> responseEntity =
        jobDashboardExceptionHandler.handleMethodArgumentNotValidException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(400, responseEntity.getStatusCodeValue());
    assertEquals(
        ErrorResponse.class.toString(),
        Objects.requireNonNull(responseEntity.getBody()).getClass().toString());
  }

  @Test
  void handleJobIdNotFoundException() {
    JobIdNotFoundException e = mock(JobIdNotFoundException.class);

    when(e.getMessage()).thenReturn("testMsg");

    ResponseEntity<ErrorResponse> responseEntity =
        jobDashboardExceptionHandler.handleJobIdNotFoundException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(400, responseEntity.getStatusCodeValue());
    assertEquals(
        ErrorResponse.class.toString(),
        Objects.requireNonNull(responseEntity.getBody()).getClass().toString());
    verify(e, times(1)).getMessage();
  }
}
