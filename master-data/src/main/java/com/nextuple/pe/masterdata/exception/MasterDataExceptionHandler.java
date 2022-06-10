package com.nextuple.pe.masterdata.exception;

import com.nextuple.pe.masterdata.error.ErrorResponse;
import com.nextuple.pe.masterdata.error.ErrorType;
import com.nextuple.pe.masterdata.exception.common.CommonServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class MasterDataExceptionHandler {

  @ExceptionHandler({
    NodeDomainException.class,
    ItemDomainException.class,
    TransitDomainException.class,
    CarrierServiceDomainException.class,
    ServiceInventoryDomainException.class
  })
  public ResponseEntity<ErrorResponse> handleOtherException(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0x1770)
                .message("Internal Server Error")
                .build());
  }

  @ExceptionHandler(CommonServiceException.class)
  public ResponseEntity<ErrorResponse> handleCommonServiceException(CommonServiceException e) {
    return ResponseEntity.status(e.getHttpStatus())
        .body(
            ErrorResponse.builder(ErrorType.ERROR, e.getErrorCode())
                .message(e.getMessage())
                .errorField(e.getFieldInfo())
                .build());
  }
}
