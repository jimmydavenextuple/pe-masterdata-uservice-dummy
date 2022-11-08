package com.nextuple.carrier.calendar.cache.domain;

import com.nextuple.core.cache.domain.CacheKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CarrierServiceCalendarCacheKey implements CacheKey {
  private static final long serialVersionUID = 4024017030652933137L;
  private String carrierServiceId;
  private String orgId;
  private String serviceOption;

  public CarrierServiceCalendarCacheKey() {
    // constructor
  }
}
