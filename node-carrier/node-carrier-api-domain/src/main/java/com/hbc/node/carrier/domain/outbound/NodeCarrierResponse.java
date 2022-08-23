package com.hbc.node.carrier.domain.outbound;

import java.io.Serializable;
import java.util.Date;
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

  private Double processingTime;

  private String lastPickupTime;

  private Double bufferHours;

  private Date bufferEndDate;

  private Date bufferStartDate;
}
