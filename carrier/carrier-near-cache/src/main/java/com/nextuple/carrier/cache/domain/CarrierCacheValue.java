package com.nextuple.carrier.cache.domain;

import com.nextuple.core.cache.domain.CacheValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CarrierCacheValue implements CacheValue {

  private static final long serialVersionUID = -9036961122388888732L;

  private CarrierDetails carrierDetails;
}
