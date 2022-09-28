package com.hbc.carrier.domain.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarrierCacheKeyDto implements Serializable {
  private static final long serialVersionUID = -4452676731179942410L;

  private String carrierId;

  private String carrierServiceId;

  private String orgId;
}
