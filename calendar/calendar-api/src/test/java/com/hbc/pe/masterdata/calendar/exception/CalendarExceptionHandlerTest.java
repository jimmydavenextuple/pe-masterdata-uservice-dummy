package com.hbc.pe.masterdata.calendar.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hbc.pe.masterdata.calendar.error.ErrorResponse;
import com.hbc.pe.masterdata.calendar.error.ErrorType;
import com.hbc.pe.masterdata.calendar.util.TestUtil;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

class CalendarExceptionHandlerTest {

  @InjectMocks private CalendarExceptionHandler calendarExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void handleMethodArgumentNotValidExceptionTest() {
    MethodParameter parameter = Mockito.mock(MethodParameter.class);
    BindingResult result = Mockito.mock(BindingResult.class);
    MethodArgumentNotValidException e = new MethodArgumentNotValidException(parameter, result);

    ResponseEntity<ErrorResponse> responseEntity =
        calendarExceptionHandler.handleMethodArgumentNotValidException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(
        ErrorType.ERROR, Objects.requireNonNull(responseEntity.getBody()).getPayload().getType());
  }

  @Test
  void handleCalendarDomainExceptionException() {
    CalendarDomainException e =
        new CalendarDomainException("error", null, TestUtil.CALENDAR_ID, null, null, null);

    ResponseEntity<ErrorResponse> responseEntity =
        calendarExceptionHandler.handleCalendarDomainException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(
        ErrorType.ERROR, Objects.requireNonNull(responseEntity.getBody()).getPayload().getType());
  }
}
