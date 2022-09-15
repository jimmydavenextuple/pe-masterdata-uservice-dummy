package com.hbc.node.carrier.exception;

import com.hbc.common.response.error.ErrorResponse;
import com.hbc.common.response.error.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class NodeCarrierExceptionHandler {

  @ExceptionHandler({NodeCarrierDomainException.class, NodeCarrierSelectionDomainException.class})
  public ResponseEntity<ErrorResponse> handleOtherException(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0x1770)
                .message("Internal Server Error")
                .build());
  }

  @ExceptionHandler(InvalidDataException.class)
  public ResponseEntity<ErrorResponse> handleInvalidDataException(Exception e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.builder(ErrorType.ERROR, 0x1771).message(e.getMessage()).build());
  }
}
