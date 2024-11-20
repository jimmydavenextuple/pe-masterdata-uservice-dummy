package com.nextuple.node.domain.outbound;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextuple.node.domain.NodeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NodeTypesResponse implements Serializable {

  @Schema(description = NodeConstants.NODE_TYPE, example = "[DSV,MFC,FC]")
  List<String> nodeTypes;
}
