package com.nextuple.pe.masterdata.calendar.exception;

import com.nextuple.pe.masterdata.calendar.error.ErrorResponse;
import com.nextuple.pe.masterdata.calendar.error.ErrorType;
import com.nextuple.pe.masterdata.calendar.error.FieldError;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class CalendarExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    ErrorResponse.ErrorResponseBuilder builder =
        new ErrorResponse.ErrorResponseBuilder(ErrorType.ERROR, 0xfffff0);
    builder.message("Input validation exception");
    e.getBindingResult().getFieldErrors().stream()
        .forEach(
            x ->
                builder.errorField(
                    x.getField(),
                    FieldError.builder()
                        .errorMessage(x.getDefaultMessage())
                        .rejectedValue(x.getRejectedValue() + "")
                        .build()));
    return ResponseEntity.badRequest().body(builder.build());
  }

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

  @ExceptionHandler(CommonServiceException.class)
  public ResponseEntity<ErrorResponse> handleCommonServiceException(CommonServiceException e) {
    return ResponseEntity.status(e.getHttpStatus())
        .body(
            ErrorResponse.builder(ErrorType.ERROR, e.getErrorCode())
                .message(e.getMessage())
                .errorField(e.getFieldInfo())
                .build());
  }
}
