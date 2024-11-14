package com.nextuple.promise.sourcing.rule.api.domain.outbound;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeDetail {
  private static final long serialVersionUID = -6082156034941327319L;

  @NotBlank(message = "Node ID should not be blank")
  @Schema(description = "Specifies the unique identifier of the node.", example = "NG1")
  private String nodeId;

  @NotNull(message = "Sequence cannot be empty")
  @Min(value = 0, message = "Sequence cannot be negative")
  @Schema(description = "Specifies the priority of the node.", example = "9999")
  private Integer sequence;
}
