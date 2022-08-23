package com.hbc.csvdownload.exception;

import com.hbc.common.response.error.ErrorResponse;
import com.hbc.common.response.error.ErrorType;
import com.hbc.common.response.error.FieldError;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class InvalidTemplateTypeExceptionHandler {

  @ExceptionHandler(InvalidTemplateTypeException.class)
  public ResponseEntity<ErrorResponse> handleInvalidTemplateTypeException(
      InvalidTemplateTypeException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff1)
                .message(e.getMessage())
                .errorField(
                    "templateType", FieldError.builder().rejectedValue(e.getTemplateType()).build())
                .build());
  }
}
