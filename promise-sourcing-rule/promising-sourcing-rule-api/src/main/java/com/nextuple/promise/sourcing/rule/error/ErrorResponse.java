package com.nextuple.promise.sourcing.rule.error;

import com.nextuple.common.response.BaseResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ErrorResponse extends BaseResponse<ErrorPayload> {

  public ErrorResponse() {
    this.setSuccess(false);
    this.setTimestamp(new Date());
  }

  public static ErrorResponseBuilder builder(ErrorType type, Integer code) {
    return new ErrorResponseBuilder(type, code);
  }

  public static class ErrorResponseBuilder {
    private final ErrorResponse errorResponse;

    public ErrorResponseBuilder(ErrorType type, Integer code) {
      this.errorResponse = new ErrorResponse();
      this.errorResponse.setPayload(new ErrorPayload(type, code));
    }

    public ErrorResponseBuilder(ErrorType type, Date timestamp, Integer code, String message) {
      this.errorResponse = new ErrorResponse();
      this.errorResponse.setPayload(new ErrorPayload(type, code));
      this.errorResponse.setMessage(message);
      this.errorResponse.setTimestamp(timestamp);
    }

    public ErrorResponseBuilder exception(Exception exception) {
      if (exception != null)
        errorResponse.getPayload().setException(exception.getClass().toString());
      return this;
    }

    public ErrorResponseBuilder timestamp(Date timestamp) {
      errorResponse.setTimestamp(timestamp);
      return this;
    }

    public ErrorResponseBuilder message(String message) {
      errorResponse.setMessage(message);
      return this;
    }

    public ErrorResponseBuilder code(Integer code) {
      this.errorResponse.getPayload().setCode(code);
      return this;
    }

    public ErrorResponseBuilder type(ErrorType type) {
      this.errorResponse.getPayload().setType(type);
      return this;
    }

    public ErrorResponseBuilder errorField(String field, FieldError fieldError) {
      if (this.errorResponse.getPayload().getFields() == null) {
        this.errorResponse.getPayload().setFields(new HashMap<>());
      }
      this.errorResponse.getPayload().getFields().put(field, fieldError);
      return this;
    }

    public ErrorResponseBuilder errorField(Map<String, FieldError> fieldError) {
      this.errorResponse.getPayload().setFields(fieldError);
      return this;
    }

    public ErrorResponseBuilder errorData(String reference, ErrorData errorData) {
      if (this.errorResponse.getPayload().getErrors() == null) {
        this.errorResponse.getPayload().setErrors(new HashMap<>());
      }
      this.errorResponse.getPayload().getErrors().put(reference, errorData);
      return this;
    }

    public ErrorResponse build() {
      return this.errorResponse;
    }
  }
}
