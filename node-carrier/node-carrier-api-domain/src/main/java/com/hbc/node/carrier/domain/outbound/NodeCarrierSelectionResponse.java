package com.hbc.node.carrier.domain.outbound;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeCarrierSelectionResponse implements Serializable {

  private static final long serialVersionUID = -8403080247082341161L;

  private String orgId;
  private String serviceOption;
  private String sourceGeozone;
  private String destinationGeozone;
  private String nodeCarrierSelectionPriority;
}
