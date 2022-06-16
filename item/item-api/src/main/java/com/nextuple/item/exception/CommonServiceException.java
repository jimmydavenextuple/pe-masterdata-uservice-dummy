package com.nextuple.item.exception;

import com.nextuple.common.response.error.FieldError;
import java.util.Map;
import lombok.Data;
import org.springframework.http.HttpStatus;

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
