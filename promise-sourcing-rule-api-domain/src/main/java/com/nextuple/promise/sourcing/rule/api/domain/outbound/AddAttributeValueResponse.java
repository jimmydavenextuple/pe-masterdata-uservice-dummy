package com.nextuple.promise.sourcing.rule.api.domain.outbound;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AddAttributeValueResponse implements Serializable {
  public static final long serialVersionUID = -3871524881833719312L;

  @Schema(description = "Name of the sourcing attribute.", example = "productClass")
  private String attributeName;

  @Schema(description = "Value of the sourcing attribute.", example = "Electronics")
  private String value;
}
