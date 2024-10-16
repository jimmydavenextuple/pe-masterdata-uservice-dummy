package com.nextuple.promise.sourcing.rule.api.domain.outbound;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeGroupWithNodesResponse {
  private static final long serialVersionUID = 5617198359480628403L;

  @NotBlank(message = "Node Group name cannot be blank")
  @Schema(description = "Specifies the name of the Node group.", example = "NG1")
  private String nodeGroupName;

  @Valid
  @NotEmpty(message = "Nodes list cannot be empty")
  @Schema(description = "Specifies the list of nodes.", example = "NG1")
  private List<NodeDetail> nodes;
}
