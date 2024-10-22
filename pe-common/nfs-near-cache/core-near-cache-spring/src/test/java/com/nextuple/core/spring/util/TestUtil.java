package com.nextuple.core.spring.util;

import static com.nextuple.core.spring.config.CacheProperties.CACHE_PROPERTY_VALUE;

import java.util.HashMap;
import java.util.Map;

public class TestUtil {

  public Map<String, String> getCacheMap() {
    Map<String, String> cacheMap = new HashMap<>();
    cacheMap.put("node", CACHE_PROPERTY_VALUE);
    cacheMap.put("carrier", CACHE_PROPERTY_VALUE);
    cacheMap.put("node_carrier", CACHE_PROPERTY_VALUE);
    cacheMap.put("node_carrier_list", CACHE_PROPERTY_VALUE);
    cacheMap.put("node_calendar", CACHE_PROPERTY_VALUE);
    cacheMap.put("carrier_calendar", CACHE_PROPERTY_VALUE);
    cacheMap.put("node_carrier_calendar", CACHE_PROPERTY_VALUE);
    cacheMap.put("postal_code_timezone", CACHE_PROPERTY_VALUE);
    cacheMap.put("sourcing_rule", CACHE_PROPERTY_VALUE);
    cacheMap.put("weightage_configuration", CACHE_PROPERTY_VALUE);
    cacheMap.put("transit", CACHE_PROPERTY_VALUE);
    cacheMap.put("common_configuration", CACHE_PROPERTY_VALUE);
    cacheMap.put("postal_code", CACHE_PROPERTY_VALUE);
    cacheMap.put("custom_region", CACHE_PROPERTY_VALUE);
    cacheMap.put("sourcing_constraints", CACHE_PROPERTY_VALUE);
    cacheMap.put("optimization_strategy", CACHE_PROPERTY_VALUE);
    cacheMap.put("sourcing_attribute_definition", CACHE_PROPERTY_VALUE);
    cacheMap.put("sourcing_rule_configuration", CACHE_PROPERTY_VALUE);
    cacheMap.put("sourcing_attribute", CACHE_PROPERTY_VALUE);
    cacheMap.put("group_definition", CACHE_PROPERTY_VALUE);
    cacheMap.put("node_carrier_selection", CACHE_PROPERTY_VALUE);
    cacheMap.put("ue_metadata", CACHE_PROPERTY_VALUE);
    cacheMap.put("ue_configdata", CACHE_PROPERTY_VALUE);
    cacheMap.put("tenant_configdata", CACHE_PROPERTY_VALUE);
    cacheMap.put("zone", CACHE_PROPERTY_VALUE);
    cacheMap.put("tenant_cost_type", CACHE_PROPERTY_VALUE);
    cacheMap.put("selector_and_cost_itinerary_mapping", CACHE_PROPERTY_VALUE);
    cacheMap.put("cost_factor", CACHE_PROPERTY_VALUE);
    cacheMap.put("cost_itinerary_and_factors", CACHE_PROPERTY_VALUE);
    cacheMap.put("preference_selector", CACHE_PROPERTY_VALUE);
    cacheMap.put("cost_attribute_mapping", CACHE_PROPERTY_VALUE);
    cacheMap.put("cost_value", CACHE_PROPERTY_VALUE);
    cacheMap.put("cost_factor_bucket_type", CACHE_PROPERTY_VALUE);
    cacheMap.put("cost_attribute_details", CACHE_PROPERTY_VALUE);
    cacheMap.put("cost_factor_contiguous_bucket", CACHE_PROPERTY_VALUE);
    cacheMap.put("cost_factor_discrete_bucket", CACHE_PROPERTY_VALUE);
    cacheMap.put("transit_buffers", CACHE_PROPERTY_VALUE);
    cacheMap.put("node_carriers", CACHE_PROPERTY_VALUE);
    cacheMap.put("node_service_option", CACHE_PROPERTY_VALUE);
    cacheMap.put("node_service_option_buffers", CACHE_PROPERTY_VALUE);
    cacheMap.put("holiday_cutoff", CACHE_PROPERTY_VALUE);
    cacheMap.put("rules_config", CACHE_PROPERTY_VALUE);
    cacheMap.put("optimization_and_cost_types_mapping", CACHE_PROPERTY_VALUE);
    cacheMap.put("transfer_schedules", CACHE_PROPERTY_VALUE);
    cacheMap.put("node_group", CACHE_PROPERTY_VALUE);
    return cacheMap;
  }
}
