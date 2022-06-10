package com.nextuple.pe.masterdata.exception.common;

import com.nextuple.pe.masterdata.error.FieldError;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
public class CommonServiceException extends Exception {
  private static final long serialVersionUID = 943000511917553590L;
  private final HttpStatus httpStatus;
  private final Integer errorCode;
  private final Map<String, FieldError> fieldInfo;

  public CommonServiceException(
      HttpStatus httpStatus, Integer errorCode, Map<String, FieldError> fieldInfo) {
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
    this.fieldInfo = fieldInfo;
  }

  public CommonServiceException(
      String message, HttpStatus httpStatus, Integer errorCode, Map<String, FieldError> fieldInfo) {
    super(message);
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
    this.fieldInfo = fieldInfo;
  }

  public CommonServiceException(
      String message,
      Throwable cause,
      HttpStatus httpStatus,
      Integer errorCode,
      Map<String, FieldError> fieldInfo) {
    super(message, cause);
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
    this.fieldInfo = fieldInfo;
  }
}
