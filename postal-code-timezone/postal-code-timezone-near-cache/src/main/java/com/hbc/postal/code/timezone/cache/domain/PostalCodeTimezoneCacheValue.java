package com.hbc.postal.code.timezone.cache.domain;

import com.hbc.core.cache.domain.CacheValue;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostalCodeTimezoneCacheValue implements CacheValue {
  PostalCodeTimezoneDto postalCodeTimezoneDto;

  @Override
  public boolean isUndefined() {
    return false;
  }
}
