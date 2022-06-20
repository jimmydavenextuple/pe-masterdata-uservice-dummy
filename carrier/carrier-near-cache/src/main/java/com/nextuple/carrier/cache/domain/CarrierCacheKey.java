package com.nextuple.carrier.cache.domain;

import com.nextuple.core.cache.domain.CacheKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CarrierCacheKey implements CacheKey {

  private static final long serialVersionUID = -3644427339281848288L;

  private String carrierId;

  private String carrierServiceId;

  private String orgId;
}
