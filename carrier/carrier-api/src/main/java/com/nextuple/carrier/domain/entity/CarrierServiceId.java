package com.nextuple.carrier.domain.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CarrierServiceId implements Serializable {
  private String orgId;
  private String carrierId;
  private String carrierServiceId;
}
