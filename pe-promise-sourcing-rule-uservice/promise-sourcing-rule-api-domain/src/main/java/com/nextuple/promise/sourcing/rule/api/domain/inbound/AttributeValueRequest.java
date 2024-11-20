package com.nextuple.promise.sourcing.rule.api.domain.inbound;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttributeValueRequest implements Serializable {
  public static final long serialVersionUID = -2874415901844391304L;

  @NotBlank(message = "Value cannot be null")
  @Schema(description = "Value of the attribute", example = "SDND")
  private String value;
}
