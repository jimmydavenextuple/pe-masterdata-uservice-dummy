package com.hbc.pe.masterdata.calendar.exception;

import com.hbc.common.response.error.ErrorResponse;
import com.hbc.common.response.error.ErrorType;
import com.hbc.common.response.error.FieldError;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class CalendarExceptionHandler {

  public static final String CALENDAR_ID = "calendarId";

  @ExceptionHandler(CalendarDomainException.class)
  public ResponseEntity<ErrorResponse> handleCalendarDomainException(CalendarDomainException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff1)
                .message(e.getMessage())
                .errorField(
                    CALENDAR_ID, FieldError.builder().rejectedValue(e.getCalendarId()).build())
                .errorField("nodeId", FieldError.builder().rejectedValue(e.getNodeId()).build())
                .errorField(
                    "carrierServiceId",
                    FieldError.builder().rejectedValue(e.getCarrierServiceId()).build())
                .build());
  }

  @ExceptionHandler(CalenderServiceException.class)
  public ResponseEntity<ErrorResponse> handleCalenderServiceException(CalenderServiceException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff1)
                .message(e.getMessage())
                .errorField("orgId", FieldError.builder().rejectedValue(e.getOrgId()).build())
                .errorField(
                    "carrierServiceId",
                    FieldError.builder().rejectedValue(e.getCarrierServiceId()).build())
                .build());
  }

  @ExceptionHandler(DateException.class)
  public ResponseEntity<ErrorResponse> handleDateException(DateException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff1)
                .message(e.getMessage())
                .errorField(
                    CALENDAR_ID, FieldError.builder().rejectedValue(e.getCalendarId()).build())
                .errorField("orgId", FieldError.builder().rejectedValue(e.getOrgId()).build())
                .build());
  }
}
