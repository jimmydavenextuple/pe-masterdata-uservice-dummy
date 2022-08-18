package java.com.hbc.global.configuration.cache.domain;

import com.hbc.core.cache.domain.CacheKey;
import com.hbc.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GlobalConfigurationCacheKey implements CacheKey {
  FetchWeightageRequest fetchWeightageRequest;

  public GlobalConfigurationCacheKey() {
    // default constructor
  }
}
