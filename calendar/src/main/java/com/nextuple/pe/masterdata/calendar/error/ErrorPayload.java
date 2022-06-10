package com.nextuple.pe.masterdata.calendar.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorPayload implements Serializable {
  private static final long serialVersionUID = 6279566259082112377L;
  private ErrorType type;
  private Integer code;
  private Map<String, FieldError> fields; // NOSONAR
  private Map<String, ErrorData> errors; // NOSONAR
  private String exception;

  public ErrorPayload() {}

  public ErrorPayload(ErrorType type, Integer code) {
    this.type = type;
    this.code = code;
  }
}
