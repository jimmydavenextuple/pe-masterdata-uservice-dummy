package com.nextuple.transit.cache.domain;

import com.nextuple.core.cache.domain.CacheKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TransitCacheKey implements CacheKey {
  public TransitCacheKey() {
    // default constructor
  }

  private String orgId;

  private String destinationGeozone;
}
