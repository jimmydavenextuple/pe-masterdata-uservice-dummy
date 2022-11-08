package com.nextuple.jobs.framework.common.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarrierServiceUpload {

  private String action;
  private String orgId;
  private String carrierId;
  private String carrierServiceId;
  private String carrierName;
  private String serviceName;
  private String serviceOptions;
}
