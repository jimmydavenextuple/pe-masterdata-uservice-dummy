package com.nextuple.common.response.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorPayload implements Serializable {
  private static final long serialVersionUID = 6279566259082112377L;

  @Schema(
      description = "Type of Error",
      allowableValues = {"INFO", "WARNING", "ERROR"})
  private ErrorType type;

  @Schema(description = "Error code", example = "0xffba")
  private Integer code;

  private Map<String, FieldError> fields; // NOSONAR
  private Map<String, ErrorData> errors; // NOSONAR

  @Schema(description = "Exception detail")
  private String exception;

  public ErrorPayload() {}

  public ErrorPayload(ErrorType type, Integer code) {
    this.type = type;
    this.code = code;
  }
}
