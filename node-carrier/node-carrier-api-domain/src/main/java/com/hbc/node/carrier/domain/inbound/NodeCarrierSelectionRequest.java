package com.hbc.node.carrier.domain.inbound;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeCarrierSelectionRequest implements Serializable {

  private static final long serialVersionUID = 2092009966201271251L;

  @NotBlank(message = "orgId cannot be empty")
  private String orgId;

  @NotBlank(message = "serviceOption cannot be empty")
  private String serviceOption;

  @NotBlank(message = "sourceGeozone can't be blank")
  private String sourceGeozone;

  @NotBlank(message = "destinationGeozone can't be blank")
  private String destinationGeozone;

  private String nodeCarrierSelectionPriority;
}
