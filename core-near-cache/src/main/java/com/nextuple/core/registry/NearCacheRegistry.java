package com.nextuple.core.registry;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class NearCacheRegistry {

  private Map<String, String> registry = new HashMap<>();

  public void registerNearCacheEntity(String entityName, String className) {
    registry.put(entityName, className);
  }

  public String getRegistry(String entityName) {
    return registry.get(entityName);
  }
}
