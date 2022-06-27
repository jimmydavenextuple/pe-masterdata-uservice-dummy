package com.hbc.core.exception;

import com.hbc.common.response.error.ErrorResponse;
import com.hbc.common.response.error.ErrorType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class ProducerExceptionHandler {

  @ExceptionHandler(LocalCacheUpdateEventException.class)
  public ResponseEntity<ErrorResponse> handleLocalCacheUpdateEventException(
      LocalCacheUpdateEventException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0x1772).message(e.getMessage()).build());
  }
}
