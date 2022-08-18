package java.com.hbc.global.configuration.cache.domain;

import com.hbc.core.cache.domain.CacheValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class GlobalConfigurationCacheValue implements CacheValue {
  private Map<String, Float> globalConfigurationResponse;
}
