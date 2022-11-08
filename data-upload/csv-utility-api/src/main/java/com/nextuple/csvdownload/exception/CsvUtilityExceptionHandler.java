package com.nextuple.csvdownload.exception;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.jobs.framework.common.utils.ExceptionUtils;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class CsvUtilityExceptionHandler {

  private final Logger logger = LoggerFactory.getLogger(CsvUtilityExceptionHandler.class);

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

  @ExceptionHandler(CsvDownloadUtilityServiceException.class)
  public ResponseEntity<ErrorResponse> handleCsvDownloadUtilityServiceException(
      CsvDownloadUtilityServiceException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0xfffff6).message(e.getMessage()).build());
  }

  @ExceptionHandler(PostalCodeTimezoneServiceException.class)
  public ResponseEntity<ErrorResponse> handlePostalCodeTimezoneServiceException(
      PostalCodeTimezoneServiceException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff7)
                .message(e.getMessage())
                .errorField("orgId", FieldError.builder().rejectedValue(e.getOrgId()).build())
                .errorField("state", FieldError.builder().rejectedValue(e.getState()).build())
                .build());
  }

  @ExceptionHandler(InvalidActionType.class)
  public ResponseEntity<ErrorResponse> handleInvalidActionType(InvalidActionType e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff5)
                .message(e.getMessage())
                .errorField("rowIndex", FieldError.builder().rejectedValue(e.getRowIndex()).build())
                .errorField("action", FieldError.builder().rejectedValue(e.getAction()).build())
                .build());
  }

  @ExceptionHandler(TransitServiceException.class)
  public ResponseEntity<ErrorResponse> handleTransitServiceException(TransitServiceException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff8)
                .message(e.getMessage())
                .errorField("orgId", FieldError.builder().rejectedValue(e.getOrgId()).build())
                .errorField(
                    "carrierServiceId",
                    FieldError.builder().rejectedValue(e.getCarrierServiceId()).build())
                .build());
  }

  @ExceptionHandler(FeignException.class)
  public ResponseEntity<ErrorResponse> handleFeignException(FeignException e) {
    var errorResponse = ExceptionUtils.parseFeignException(e);
    logger.error("Feign exception. message: {}", errorResponse.getMessage());
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xffff1)
                .message(errorResponse.getMessage())
                .build());
  }

  @ExceptionHandler(CsvDataValidationException.class)
  public ResponseEntity<ErrorResponse> handleCsvDataValidationException(
      CsvDataValidationException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0xffff2).message(e.getMessage()).build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
    return ResponseEntity.internalServerError()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0xffffff).message(e.getMessage()).build());
  }
}
