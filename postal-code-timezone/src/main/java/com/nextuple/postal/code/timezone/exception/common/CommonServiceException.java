package com.nextuple.postal.code.timezone.exception.common;

import com.nextuple.postal.code.timezone.error.FieldError;
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

  public CommonServiceException(
      Throwable cause,
      HttpStatus httpStatus,
      Integer errorCode,
      Map<String, FieldError> fieldInfo) {
    super(cause);
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
    this.fieldInfo = fieldInfo;
  }

  public CommonServiceException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      HttpStatus httpStatus,
      Integer errorCode,
      Map<String, FieldError> fieldInfo) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
    this.fieldInfo = fieldInfo;
  }
}
