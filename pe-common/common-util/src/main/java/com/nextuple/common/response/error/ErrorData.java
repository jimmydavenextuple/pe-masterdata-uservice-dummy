package com.nextuple.common.response.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorData implements Serializable {
  private static final long serialVersionUID = 6006518972445723246L;

  @Schema(description = "Error code", example = "0xffba")
  private Integer code;

  @Schema(description = "Error Message ", example = "Invalid Parameters")
  private String message;
}
