package com.nextuple.core.spring.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "nearcache")
public class CacheProperties {

  public static final String CACHE_PROPERTY_VALUE = "100,24,false,sync";

  private Map<String, String> cachemap;

  public Map<String, String> setCacheDefaults() {
    Map<String, String> defaultMap = new HashMap<>();
    defaultMap.put("node", CACHE_PROPERTY_VALUE);
    defaultMap.put("carrier", CACHE_PROPERTY_VALUE);
    defaultMap.put("node_carrier", CACHE_PROPERTY_VALUE);
    defaultMap.put("node_carrier_list", CACHE_PROPERTY_VALUE);
    defaultMap.put("node_calendar", CACHE_PROPERTY_VALUE);
    defaultMap.put("carrier_calendar", CACHE_PROPERTY_VALUE);
    defaultMap.put("node_carrier_calendar", CACHE_PROPERTY_VALUE);
    defaultMap.put("postal_code_timezone", CACHE_PROPERTY_VALUE);
    defaultMap.put("sourcing_rule", CACHE_PROPERTY_VALUE);
    defaultMap.put("weightage_configuration", CACHE_PROPERTY_VALUE);
    defaultMap.put("transit", CACHE_PROPERTY_VALUE);
    defaultMap.put("transit_buffers", CACHE_PROPERTY_VALUE);
    defaultMap.put("common_configuration", CACHE_PROPERTY_VALUE);
    defaultMap.put("postal_code", CACHE_PROPERTY_VALUE);
    defaultMap.put("custom_region", CACHE_PROPERTY_VALUE);
    defaultMap.put("sourcing_constraints", CACHE_PROPERTY_VALUE);
    defaultMap.put("optimization_strategy", CACHE_PROPERTY_VALUE);
    defaultMap.put("sourcing_attribute_definition", CACHE_PROPERTY_VALUE);
    defaultMap.put("sourcing_rule_configuration", CACHE_PROPERTY_VALUE);
    defaultMap.put("sourcing_attribute", CACHE_PROPERTY_VALUE);
    defaultMap.put("group_definition", CACHE_PROPERTY_VALUE);
    defaultMap.put("node_carrier_selection", CACHE_PROPERTY_VALUE);
    defaultMap.put("ue_metadata", CACHE_PROPERTY_VALUE);
    defaultMap.put("ue_configdata", CACHE_PROPERTY_VALUE);
    defaultMap.put("tenant_configdata", CACHE_PROPERTY_VALUE);
    defaultMap.put("zone", CACHE_PROPERTY_VALUE);
    defaultMap.put("tenant_cost_type", CACHE_PROPERTY_VALUE);
    defaultMap.put("selector_and_cost_itinerary_mapping", CACHE_PROPERTY_VALUE);
    defaultMap.put("cost_factor", CACHE_PROPERTY_VALUE);
    defaultMap.put("cost_itinerary_and_factors", CACHE_PROPERTY_VALUE);
    defaultMap.put("preference_selector", CACHE_PROPERTY_VALUE);
    defaultMap.put("cost_attribute_mapping", CACHE_PROPERTY_VALUE);
    defaultMap.put("cost_value", CACHE_PROPERTY_VALUE);
    defaultMap.put("cost_factor_bucket_type", CACHE_PROPERTY_VALUE);
    defaultMap.put("cost_attribute_details", CACHE_PROPERTY_VALUE);
    defaultMap.put("cost_factor_contiguous_bucket", CACHE_PROPERTY_VALUE);
    defaultMap.put("cost_factor_discrete_bucket", CACHE_PROPERTY_VALUE);
    defaultMap.put("node_carriers", CACHE_PROPERTY_VALUE);
    defaultMap.put("node_service_option", CACHE_PROPERTY_VALUE);
    defaultMap.put("node_service_option_buffers", CACHE_PROPERTY_VALUE);
    defaultMap.put("holiday_cutoff", CACHE_PROPERTY_VALUE);
    defaultMap.put("rules_config", CACHE_PROPERTY_VALUE);
    defaultMap.put("optimization_and_cost_types_mapping", CACHE_PROPERTY_VALUE);
    defaultMap.put("transfer_schedules", CACHE_PROPERTY_VALUE);
    defaultMap.put("node_group", CACHE_PROPERTY_VALUE);
    return defaultMap;
  }
}
