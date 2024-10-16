package com.nextuple.promise.sourcing.rule.api.domain.inbound;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourcingRuleIdRequest implements Serializable {
  private static final long serialVersionUID = -7080208237465570365L;

  @Schema(description = "List of sourcing rule ids", example = "[1,2]")
  @NotEmpty(message = "Sourcing rule ids cannot be empty")
  private List<Long> sourcingRuleIds;
}
