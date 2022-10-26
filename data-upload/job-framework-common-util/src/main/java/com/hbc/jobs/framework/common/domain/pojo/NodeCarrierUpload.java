package com.hbc.jobs.framework.common.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeCarrierUpload {

  private String action;
  private String nodeId;
  private String orgId;
  private String carrierServiceId;
  private String serviceOption;
  private String lastPickupTime;
}
