package com.nextuple.weightage.configuration.cache.domain;

import com.nextuple.core.cache.domain.CacheKey;
import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WeightageConfigurationCacheKey implements CacheKey {
  FetchWeightageRequest fetchWeightageRequest;

  public WeightageConfigurationCacheKey() {}
}
