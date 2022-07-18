package com.hbc.core.spring.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "near-cache")
public class CacheProperties {

  public static final String CACHE_PROPERTY_VALUE = "100,24";

  private Map<String, String> cacheMap;

  public Map<String, String> setCacheDefaults() {
    Map<String, String> defaultMap = new HashMap<>();
    defaultMap.put("node", CACHE_PROPERTY_VALUE);
    defaultMap.put("carrier", CACHE_PROPERTY_VALUE);
    defaultMap.put("node_carrier", CACHE_PROPERTY_VALUE);
    defaultMap.put("node_calendar", CACHE_PROPERTY_VALUE);
    defaultMap.put("carrier_calendar", CACHE_PROPERTY_VALUE);
    defaultMap.put("node_carrier_calendar", CACHE_PROPERTY_VALUE);
    defaultMap.put("postal code timezone", CACHE_PROPERTY_VALUE);
    defaultMap.put("sourcingRule", CACHE_PROPERTY_VALUE);
    defaultMap.put("weightage configuration", CACHE_PROPERTY_VALUE);
    return defaultMap;
  }
}
