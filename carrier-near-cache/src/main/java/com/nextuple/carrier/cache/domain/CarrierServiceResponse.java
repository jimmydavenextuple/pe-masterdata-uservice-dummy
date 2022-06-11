package com.nextuple.carrier.cache.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CarrierServiceResponse implements Serializable {

  private static final long serialVersionUID = 2201582889725931320L;

  private String orgId;

  private String carrierId;

  private String serviceId;

  private String carrierName;

  private String serviceName;

  private String serviceOptions;
}
