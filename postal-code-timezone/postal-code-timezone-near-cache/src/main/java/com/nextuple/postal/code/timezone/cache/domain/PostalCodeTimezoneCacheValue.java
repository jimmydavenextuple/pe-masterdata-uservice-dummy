package com.nextuple.postal.code.timezone.cache.domain;

import com.nextuple.core.cache.domain.CacheValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostalCodeTimezoneCacheValue implements CacheValue {
  PostalCodeTimezoneDto postalCodeTimezoneDto;
}
