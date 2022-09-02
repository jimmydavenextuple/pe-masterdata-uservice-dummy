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
public class CsvUtilityExceptionHandler {

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

  @ExceptionHandler(CsvFormatValidationFailedException.class)
  public ResponseEntity<ErrorResponse> handleCsvFormatValidationFailedException(
      CsvFormatValidationFailedException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0xfffff2).message(e.getMessage()).build());
  }

  @ExceptionHandler(CsvParsingException.class)
  public ResponseEntity<ErrorResponse> handleCsvParsingException(CsvParsingException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0xfffff2).message(e.getMessage()).build());
  }

  @ExceptionHandler(JobSubmissionException.class)
  public ResponseEntity<ErrorResponse> handleJobSubmissionException(JobSubmissionException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0xfffff3).message(e.getMessage()).build());
  }

  @ExceptionHandler(JsonParsingException.class)
  public ResponseEntity<ErrorResponse> handleJsonParsingException(JsonParsingException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0xfffff4).message(e.getMessage()).build());
  }
}
