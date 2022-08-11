package com.hbc.jobs.consumers.exception;

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
@SuppressWarnings("squid:S1192")
public class JobConsumerExceptionHandler {

  @ExceptionHandler(DuplicateJobException.class)
  public ResponseEntity<Object> handleDuplicateJobException(DuplicateJobException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff1)
                .message(e.getMessage())
                .errorField("jobId", FieldError.builder().rejectedValue(e.getJobId()).build())
                .build());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    ErrorResponse.ErrorResponseBuilder builder =
        new ErrorResponse.ErrorResponseBuilder(ErrorType.ERROR, 0xfffff3);
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

  @ExceptionHandler(InvalidJobTypeException.class)
  public ResponseEntity<Object> handleJobIdNotFoundException(InvalidJobTypeException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff4)
                .message(e.getMessage())
                .errorField("jobType", FieldError.builder().rejectedValue(e.getJobType()).build())
                .build());
  }

  @ExceptionHandler(JobRecordDomainException.class)
  public ResponseEntity<Object> handleJobRecordDomainException(JobRecordDomainException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff6)
                .message(e.getMessage())
                .errorField(
                    "jobRecordId", FieldError.builder().rejectedValue(e.getJobRecordId()).build())
                .build());
  }

  @ExceptionHandler(JobDashboardException.class)
  public ResponseEntity<Object> handleJobDashboardException(JobDashboardException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff7)
                .message(e.getMessage())
                .errorField("jobId", FieldError.builder().rejectedValue(e.getJobId()).build())
                .build());
  }

  @ExceptionHandler(JobDomainException.class)
  public ResponseEntity<Object> handleJobDomainException(JobDomainException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xffffd1)
                .message(e.getMessage())
                .errorField("jobId", FieldError.builder().rejectedValue(e.getJobId()).build())
                .build());
  }

  @ExceptionHandler(JobException.class)
  public ResponseEntity<Object> handleJobException(JobException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xffffd2)
                .message(e.getMessage())
                .errorField("jobType", FieldError.builder().rejectedValue(e.getJobId()).build())
                .errorField("pageNo", FieldError.builder().rejectedValue(e.getPageNo()).build())
                .build());
  }

  @ExceptionHandler(TransitMapperException.class)
  public ResponseEntity<Object> handleCapacityMapperException(TransitMapperException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0xffffd3).message(e.getMessage()).build());
  }

  @ExceptionHandler(NodeCarrierMapperException.class)
  public ResponseEntity<Object> handleCapacityMapperException(NodeCarrierMapperException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0xffffd4).message(e.getMessage()).build());
  }
}
