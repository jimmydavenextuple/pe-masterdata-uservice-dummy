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
public class AttributeNameRequest implements Serializable {
  private static final long serialVersionUID = 1566204630573496324L;

  @NotBlank(message = "Attribute name cannot be empty")
  @Schema(description = "Name of the attribute", example = "serviceOption")
  private String attributeName;
}
