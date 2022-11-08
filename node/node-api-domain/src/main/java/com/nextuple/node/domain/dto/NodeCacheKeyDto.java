package com.nextuple.node.domain.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeCacheKeyDto implements Serializable {
  private static final long serialVersionUID = 5584595073543984767L;

  private String nodeId;
  private String orgId;
}
