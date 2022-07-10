package com.hbc.weightage.configuration.cache.domain;

import com.hbc.core.cache.domain.CacheKey;
import com.hbc.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WeightageConfigurationCacheKey implements CacheKey {
  FetchWeightageRequest fetchWeightageRequest;

  public WeightageConfigurationCacheKey() {
    // default constructor
  }
}
