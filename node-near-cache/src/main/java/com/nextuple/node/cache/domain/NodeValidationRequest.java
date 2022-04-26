package com.nextuple.node.cache.domain;

import com.nextuple.core.node.NodeValidationDetails;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NodeValidationRequest {
  @Valid
  @Size(min = 1, message = "at least one nodeNo and tenantId is required")
  @NotNull(message = "nodes cannot be null")
  private List<NodeValidationDetails> nodes;
}
