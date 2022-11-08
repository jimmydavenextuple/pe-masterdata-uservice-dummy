package com.nextuple.node.carrier.domain.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeCarrierListCacheKeyDto implements Serializable {
  private static final long serialVersionUID = -1635114888523463461L;

  private String nodeId;

  private String orgId;

  private String serviceOption;
}
