package com.hbc.carrier.domain.inbound;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarrierServiceUpdateRequest implements Serializable {
  private String carrierName;
  private String serviceName;
  private String serviceOptions;
}
