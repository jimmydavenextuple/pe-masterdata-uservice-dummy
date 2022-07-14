package com.hbc.node.carrier.domain.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NodeCarrierId implements Serializable {

  private String nodeId;
  private String orgId;
  private String carrierServiceId;
  private String serviceOption;
}
