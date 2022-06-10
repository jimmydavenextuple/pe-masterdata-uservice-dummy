package com.nextuple.pe.masterdata.domain.inbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarrierServiceUpdateRequest implements Serializable {
  private String carrierName;
  private String serviceName;
  private String serviceOptions;
}
