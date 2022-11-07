package com.hbc.jobs.dashboard.exception;

import com.hbc.common.response.error.ErrorResponse;
import com.hbc.common.response.error.ErrorType;
import com.hbc.common.response.error.FieldError;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class JobDashboardExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    var builder = new ErrorResponse.ErrorResponseBuilder(ErrorType.ERROR, 0xfffff6);
    builder.message("Bad Request");
    e.getBindingResult()
        .getFieldErrors()
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

  @ExceptionHandler(JobIdNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleJobIdNotFoundException(JobIdNotFoundException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff7)
                .message(e.getMessage())
                .errorField(
                    "jobId",
                    FieldError.builder()
                        .errorMessage("job record not " + "found!")
                        .rejectedValue(e.getJobId())
                        .build())
                .build());
  }

  @ExceptionHandler(FileMetaDataException.class)
  public ResponseEntity<ErrorResponse> handleFileMetaDataException(FileMetaDataException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff9)
                .message(e.getMessage())
                .errorField("Id", FieldError.builder().rejectedValue(e.getId()).build())
                .build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleCommonExceptions(Exception e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0xfffff8).message(e.getMessage()).build());
  }
}
