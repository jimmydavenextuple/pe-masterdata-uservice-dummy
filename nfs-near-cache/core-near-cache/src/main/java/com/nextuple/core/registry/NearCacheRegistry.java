package com.nextuple.core.registry;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class NearCacheRegistry {

  private Map<String, Map<String, String>> registry = new HashMap<>();

  public void registerNearCacheEntity(String entityName, String className, String dropType) {
    Map<String, String> cacheKeyDetails = new HashMap<>();
    cacheKeyDetails.put(className, dropType);
    registry.put(entityName, cacheKeyDetails);
  }

  public Map<String, String> getRegistry(String entityName) {
    return registry.get(entityName);
  }
}
