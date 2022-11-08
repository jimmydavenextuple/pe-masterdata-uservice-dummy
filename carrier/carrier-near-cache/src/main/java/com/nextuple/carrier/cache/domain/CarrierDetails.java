package com.nextuple.carrier.cache.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CarrierDetails implements Serializable {

  private static final long serialVersionUID = 4816560413275581767L;

  private String orgId;

  private String carrierId;

  private String carrierServiceId;

  private String carrierName;

  private String serviceName;

  private String serviceOptions;
}
