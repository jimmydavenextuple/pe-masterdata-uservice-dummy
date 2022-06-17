package com.nextuple.carrier.domain.outbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarrierServiceResponse {

  private String orgId;
  private String carrierId;
  private String serviceId;
  private String carrierName;
  private String serviceName;
  private String serviceOptions;
}
