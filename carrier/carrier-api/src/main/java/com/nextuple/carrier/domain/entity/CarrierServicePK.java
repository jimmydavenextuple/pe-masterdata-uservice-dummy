package com.nextuple.carrier.domain.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CarrierServicePK implements Serializable {
  private String orgId;
  private String carrierId;
  private String carrierServiceId;
}
