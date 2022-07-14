package com.hbc.postal.code.timezone.cache.domain;

import com.hbc.core.cache.domain.CacheKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostalCodeTimezoneCacheKey implements CacheKey {
  String orgId;
  String postalCodePrefix;

  public PostalCodeTimezoneCacheKey() {
    // constructor
  }
}
