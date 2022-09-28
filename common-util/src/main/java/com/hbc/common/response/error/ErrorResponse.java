package com.hbc.common.response.error;

import com.hbc.common.context.CurrentThreadContext;
import com.hbc.common.response.BaseResponse;
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
    this.setRequestId(CurrentThreadContext.getLogContext().getCorrelationId());
    this.setTimestamp(new Date().getTime());
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
      this.errorResponse.setTimestamp(timestamp.getTime());
    }

    public ErrorResponseBuilder exception(Exception exception) {
      if (exception != null)
        errorResponse.getPayload().setException(exception.getClass().toString());
      return this;
    }

    public ErrorResponseBuilder requestId(String requestId) {
      errorResponse.setRequestId(requestId);
      return this;
    }

    public ErrorResponseBuilder timestamp(Date timestamp) {
      errorResponse.setTimestamp(timestamp.getTime());
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
