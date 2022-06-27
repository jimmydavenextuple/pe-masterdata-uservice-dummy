package com.nextuple.pe.masterdata.calendar.exception;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import com.nextuple.common.response.error.FieldError;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class CalendarExceptionHandler {

  @ExceptionHandler(CalendarDomainException.class)
  public ResponseEntity<ErrorResponse> handleCalendarDomainException(CalendarDomainException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff1)
                .message(e.getMessage())
                .errorField(
                    "calendarId", FieldError.builder().rejectedValue(e.getCalendarId()).build())
                .errorField("nodeId", FieldError.builder().rejectedValue(e.getNodeId()).build())
                .errorField(
                    "carrierServiceId",
                    FieldError.builder().rejectedValue(e.getCarrierServiceId()).build())
                .build());
  }
}
