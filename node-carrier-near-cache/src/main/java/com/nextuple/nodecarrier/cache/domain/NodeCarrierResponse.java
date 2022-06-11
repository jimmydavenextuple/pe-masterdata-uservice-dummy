package com.nextuple.nodecarrier.cache.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NodeCarrierResponse implements Serializable {

  private static final long serialVersionUID = -5620527780915402045L;

  private String nodeId;

  private String orgId;

  private String carrierServiceId;

  private String serviceOption;

  private Integer processingTime;

  private String lastPickupTime;
}
