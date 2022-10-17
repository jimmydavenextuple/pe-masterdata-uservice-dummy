package com.hbc.transit.exception;

import com.hbc.common.response.error.ErrorResponse;
import com.hbc.common.response.error.ErrorType;
import com.hbc.common.response.error.FieldError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class TransitExceptionHandler {

  @ExceptionHandler(TransitDomainException.class)
  public ResponseEntity<ErrorResponse> handleOtherException(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0x1770)
                .message("Internal Server Error")
                .build());
  }

  @ExceptionHandler(TransitBufferReqJobRefDomainException.class)
  public ResponseEntity<ErrorResponse> handleTransitBufferReqJobRefDomainException(
      TransitBufferReqJobRefDomainException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff9)
                .message(e.getMessage())
                .errorField("id", FieldError.builder().rejectedValue(e.getId()).build())
                .errorField(
                    "extReferenceId",
                    FieldError.builder().rejectedValue(e.getExtReferenceId()).build())
                .build());
  }
}
