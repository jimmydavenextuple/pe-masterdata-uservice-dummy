package com.hbc.pe.masterdata.calendar.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hbc.common.response.error.ErrorResponse;
import com.hbc.common.response.error.ErrorType;
import com.hbc.pe.masterdata.calendar.util.TestUtil;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CalendarExceptionHandlerTest {

  @InjectMocks private CalendarExceptionHandler calendarExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
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

  @Test
  void handleDateExceptionException() {
    DateException e = new DateException("error", TestUtil.CALENDAR_ID, TestUtil.ORG_ID);

    ResponseEntity<ErrorResponse> responseEntity = calendarExceptionHandler.handleDateException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(
        ErrorType.ERROR, Objects.requireNonNull(responseEntity.getBody()).getPayload().getType());
  }
}
