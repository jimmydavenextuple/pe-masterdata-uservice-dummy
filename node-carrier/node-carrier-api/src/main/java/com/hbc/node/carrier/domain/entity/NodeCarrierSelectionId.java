package com.hbc.node.carrier.domain.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NodeCarrierSelectionId implements Serializable {

  private String orgId;
  private String serviceOption;
  private String sourceGeozone;
  private String destinationGeozone;
}
