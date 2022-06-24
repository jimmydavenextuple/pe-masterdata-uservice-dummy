package com.nextuple.core.mapper;

import static com.nextuple.core.constants.NearCacheConstants.CARRIER_CALENDAR_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.CARRIER_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.NODE_CALENDAR_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.NODE_CARRIER_CALENDAR_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.NODE_CARRIER_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.NODE_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.POSTAL_CODE_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.PROMISE_SOURCING_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.WEIGHTAGE_CONFIGURATION_ENTITY_NAME;

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
      registry.put("NodeEntity", NODE_ENTITY_NAME);
      registry.put("NodeCarrierEntity", NODE_CARRIER_ENTITY_NAME);
      registry.put("CarrierServiceEntity", CARRIER_ENTITY_NAME);
      registry.put("NodeCalendarEntity", NODE_CALENDAR_ENTITY_NAME);
      registry.put("NodeCarrierServiceCalendarEntity", NODE_CARRIER_CALENDAR_ENTITY_NAME);
      registry.put("CarrierServiceCalendarEntity", CARRIER_CALENDAR_ENTITY_NAME);
      registry.put("PromiseSourcingRule", PROMISE_SOURCING_ENTITY_NAME);
      registry.put("WeightageConfiguration", WEIGHTAGE_CONFIGURATION_ENTITY_NAME);
      registry.put("PostalCodeTimezoneEntity", POSTAL_CODE_ENTITY_NAME);
    }
    log.info("registry value after: {}", registry);
    return registry;
  }
}
