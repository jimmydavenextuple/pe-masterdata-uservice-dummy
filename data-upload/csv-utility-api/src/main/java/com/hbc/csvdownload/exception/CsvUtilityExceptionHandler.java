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
}
