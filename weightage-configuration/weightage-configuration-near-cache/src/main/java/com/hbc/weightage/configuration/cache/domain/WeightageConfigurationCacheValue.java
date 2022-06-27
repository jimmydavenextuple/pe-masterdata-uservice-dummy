package com.hbc.weightage.configuration.cache.domain;

import com.hbc.core.cache.domain.CacheValue;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WeightageConfigurationCacheValue implements CacheValue {
  Map<String, Float> weightageConfigurationResponse;
}
