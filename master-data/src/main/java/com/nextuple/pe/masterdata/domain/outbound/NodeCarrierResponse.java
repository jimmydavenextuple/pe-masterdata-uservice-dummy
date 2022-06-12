package com.nextuple.pe.masterdata.domain.outbound;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeCarrierResponse implements Serializable {

  private static final long serialVersionUID = -1379998917280769528L;

  private String nodeId;

  private String orgId;

  private String carrierServiceId;

  private String serviceOption;

  private Integer processingTime;

  private String lastPickupTime;
}
