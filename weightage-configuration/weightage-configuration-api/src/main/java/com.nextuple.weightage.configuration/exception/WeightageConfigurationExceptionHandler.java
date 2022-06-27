package com.nextuple.weightage.configuration.exception;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import com.nextuple.weightage.configuration.exception.common.PromiseEngineException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class WeightageConfigurationExceptionHandler {

  @ExceptionHandler(PromiseEngineException.class)
  public ResponseEntity<ErrorResponse> handlePromiseEngineException(PromiseEngineException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0x000001)
                .message(e.getMessage() + "[" + e.getExceptionCode().getErrorCode() + "]")
                .build());
  }
}
