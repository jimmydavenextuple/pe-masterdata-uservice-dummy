package com.hbc.core.spring.util;

import static com.hbc.core.spring.config.CacheProperties.CACHE_PROPERTY_VALUE;

import java.util.HashMap;
import java.util.Map;

public class TestUtil {

  public Map<String, String> getCacheMap() {
    Map<String, String> cacheMap = new HashMap<>();
    cacheMap.put("node", CACHE_PROPERTY_VALUE);
    cacheMap.put("carrier", CACHE_PROPERTY_VALUE);
    cacheMap.put("node_carrier", CACHE_PROPERTY_VALUE);
    cacheMap.put("node_calendar", CACHE_PROPERTY_VALUE);
    cacheMap.put("carrier_calendar", CACHE_PROPERTY_VALUE);
    cacheMap.put("node_carrier_calendar", CACHE_PROPERTY_VALUE);
    cacheMap.put("postal code timezone", CACHE_PROPERTY_VALUE);
    cacheMap.put("sourcingRule", CACHE_PROPERTY_VALUE);
    cacheMap.put("weightage configuration", CACHE_PROPERTY_VALUE);
    return cacheMap;
  }
}
