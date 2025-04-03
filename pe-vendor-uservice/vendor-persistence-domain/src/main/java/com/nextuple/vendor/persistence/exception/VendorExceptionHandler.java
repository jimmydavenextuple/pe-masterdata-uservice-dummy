package com.nextuple.vendor.persistence.exception;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class VendorExceptionHandler {

  @ExceptionHandler(VendorDomainException.class)
  public ResponseEntity<ErrorResponse> handleOtherException(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0x1770)
                .message("Internal Server Error")
                .build());
  }
}
