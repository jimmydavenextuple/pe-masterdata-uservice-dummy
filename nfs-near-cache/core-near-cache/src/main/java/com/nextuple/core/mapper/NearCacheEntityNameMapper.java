package com.nextuple.core.mapper;

import static com.nextuple.core.constants.NearCacheConstants.NODE_ENTITY_NAME;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NearCacheEntityNameMapper {

  private static final Map<String, String> registry = new HashMap<>();

  private NearCacheEntityNameMapper() {}

  public static Map<String, String> getEntityMapping() {
    log.info("registry value before: {}", registry);
    if (registry.isEmpty()) {
      registry.put("Node", NODE_ENTITY_NAME);
    }
    log.info("registry value after: {}", registry);
    return registry;
  }
}
