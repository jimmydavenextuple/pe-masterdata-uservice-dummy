package com.nextuple.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> implements Serializable {
  private static final long serialVersionUID = -6697518836477283253L;

  @Schema(description = "Flag indicating if the request is successful.", example = "true")
  private boolean success;

  @Schema(
      description = "Unique identifier of the request.",
      example = "aaaa-bbbb-cccc-dddd-eeee-ffff")
  private String requestId;

  @Schema(description = "Response timestamp.", example = "2023-01-01T00:00:00.000+00:00")
  private Long timestamp;

  @Schema(
      description =
          "Message conveying success, error, warnings, or any other relevant information.",
      example = "Request completed successfully")
  private String message;

  private T payload; // NOSONAR

  public static BaseResponseBuilder builder() {
    return new BaseResponseBuilder();
  }
}
