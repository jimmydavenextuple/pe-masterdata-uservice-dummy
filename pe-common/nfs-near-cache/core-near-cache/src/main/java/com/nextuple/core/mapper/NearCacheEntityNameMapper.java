package com.nextuple.core.mapper;

import static com.nextuple.core.constants.NearCacheConstants.CARRIER_CALENDAR_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.CARRIER_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.COMMON_CONFIG_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.COST_ATTRIBUTE_DETAILS_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.COST_ATTRIBUTE_MAPPING_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.COST_FACTOR_BUCKET_TYPE_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.COST_FACTOR_CONTIGUOUS_BUCKET_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.COST_FACTOR_DISCRETE_BUCKET_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.COST_FACTOR_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.COST_ITINERARY_AND_FACTORS_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.COST_VALUE_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.GROUP_DEFINITION_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.HOLIDAY_CUTOFF_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.NODE_CALENDAR_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.NODE_CARRIERS_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.NODE_CARRIER_CALENDAR_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.NODE_CARRIER_LIST_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.NODE_CARRIER_SELECTION_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.NODE_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.NODE_GROUP_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.NODE_SERVICE_OPTION_BUFFER_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.NODE_SERVICE_OPTION_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.OPTIMIZATION_STRATEGY_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.OPT_COST_TYPES_MAPPING_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.POSTAL_CODE_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.POSTAL_CODE_TIME_ZONE_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.PREFERENCE_SELECTOR_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.PROMISE_SOURCING_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.RULES_CONFIGURATION_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.SELECTOR_AND_COST_ITINERARY_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.SOURCING_ATTRIBUTES_DEFINITION_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.SOURCING_ATTRIBUTE_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.SOURCING_CONSTRAINT_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.SOURCING_CUSTOM_REGION_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.SOURCING_RULE_DETAILS_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.TENANT_CONFIG_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.TENANT_COST_TYPE_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.TRANSFER_SCHEDULE_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.TRANSIT_BUFFER_V2_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.TRANSIT_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.UE_CONFIG_DATA_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.UE_META_DATA_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.WEIGHTAGE_CONFIGURATION_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.ZONE_ENTITY_NAME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NearCacheEntityNameMapper {

  private static final Map<String, List<String>> registry = new HashMap<>();

  private NearCacheEntityNameMapper() {}

  public static Map<String, List<String>> getEntityMapping() {
    if (registry.isEmpty()) {
      registry.put("NodeEntity", List.of(NODE_ENTITY_NAME));
      registry.put("NodeCarrierEntity", List.of(NODE_CARRIER_LIST_ENTITY_NAME));
      registry.put("CarrierServiceEntity", List.of(CARRIER_ENTITY_NAME));
      registry.put("NodeCalendarEntity", List.of(NODE_CALENDAR_ENTITY_NAME));
      registry.put("NodeCarrierServiceCalendarEntity", List.of(NODE_CARRIER_CALENDAR_ENTITY_NAME));
      registry.put("CarrierServiceCalendarEntity", List.of(CARRIER_CALENDAR_ENTITY_NAME));
      registry.put("PromiseSourcingRule", List.of(PROMISE_SOURCING_ENTITY_NAME));
      registry.put("WeightageConfigurationEntity", List.of(WEIGHTAGE_CONFIGURATION_ENTITY_NAME));
      registry.put("PostalCodeTimezoneEntity", List.of(POSTAL_CODE_TIME_ZONE_ENTITY_NAME));
      registry.put("TransitEntity", List.of(TRANSIT_ENTITY_NAME));
      registry.put("TransitBufferEntity", List.of(TRANSIT_ENTITY_NAME));
      registry.put("TransitBufferV2Entity", List.of(TRANSIT_BUFFER_V2_ENTITY_NAME));
      registry.put("CommonConfiguration", List.of(COMMON_CONFIG_ENTITY_NAME));
      registry.put("PostalCodeEntity", List.of(POSTAL_CODE_ENTITY_NAME));
      registry.put(
          "CustomRegionEntity",
          List.of(SOURCING_CUSTOM_REGION_ENTITY_NAME, POSTAL_CODE_ENTITY_NAME));
      registry.put("SourcingConstraintEntity", List.of(SOURCING_CONSTRAINT_ENTITY_NAME));
      registry.put("NamedOptimizationStrategyEntity", List.of(OPTIMIZATION_STRATEGY_ENTITY_NAME));
      registry.put(
          "SourcingAttributesDefinitionEntity",
          List.of(SOURCING_ATTRIBUTES_DEFINITION_ENTITY_NAME));
      registry.put("SourcingRuleDetailsEntity", List.of(SOURCING_RULE_DETAILS_ENTITY_NAME));
      registry.put("SourcingAttributeEntity", List.of(SOURCING_ATTRIBUTE_ENTITY_NAME));
      registry.put("GroupDefinitionEntity", List.of(GROUP_DEFINITION_ENTITY_NAME));
      registry.put("NodeCarrierSelectionEntity", List.of(NODE_CARRIER_SELECTION_ENTITY_NAME));
      registry.put(
          "NodePriorityEntity", List.of(SOURCING_RULE_DETAILS_ENTITY_NAME, NODE_GROUP_ENTITY_NAME));
      registry.put("UserExitMetaData", List.of(UE_META_DATA_ENTITY_NAME));
      registry.put("UserExitConfigData", List.of(UE_CONFIG_DATA_ENTITY_NAME));
      registry.put("TenantConfigdataEntity", List.of(TENANT_CONFIG_ENTITY_NAME));
      registry.put("ConfigMetadataEntity", List.of(TENANT_CONFIG_ENTITY_NAME));
      registry.put("ZoneEntity", List.of(ZONE_ENTITY_NAME));
      registry.put("TenantCostTypeEntity", List.of(TENANT_COST_TYPE_ENTITY_NAME));
      registry.put(
          "SelectorAndCostItineraryMappingEntity",
          List.of(SELECTOR_AND_COST_ITINERARY_ENTITY_NAME));
      registry.put("CostFactorEntity", List.of(COST_FACTOR_ENTITY_NAME));
      registry.put(
          "CostItineraryAndFactorsEntity", List.of(COST_ITINERARY_AND_FACTORS_ENTITY_NAME));
      registry.put("PreferenceSelectorEntity", List.of(PREFERENCE_SELECTOR_ENTITY_NAME));
      registry.put("CostAttributeMappingEntity", List.of(COST_ATTRIBUTE_MAPPING_ENTITY_NAME));
      registry.put("CostValueEntity", List.of(COST_VALUE_ENTITY_NAME));
      registry.put("CostFactorBucketTypeEntity", List.of(COST_FACTOR_BUCKET_TYPE_ENTITY_NAME));
      registry.put("CostAttributeDetailsEntity", List.of(COST_ATTRIBUTE_DETAILS_ENTITY_NAME));
      registry.put(
          "CostFactorContiguousBucketEntity", List.of(COST_FACTOR_CONTIGUOUS_BUCKET_ENTITY_NAME));
      registry.put(
          "CostFactorDiscreteBucketEntity", List.of(COST_FACTOR_DISCRETE_BUCKET_ENTITY_NAME));
      registry.put("NodeCarriersEntity", List.of(NODE_CARRIERS_ENTITY_NAME));
      registry.put("NodeServiceOptionEntity", List.of(NODE_SERVICE_OPTION_ENTITY_NAME));
      registry.put(
          "NodeServiceOptionBufferEntity", List.of(NODE_SERVICE_OPTION_BUFFER_ENTITY_NAME));
      registry.put("HolidayCutoffEntity", List.of(HOLIDAY_CUTOFF_ENTITY_NAME));
      registry.put("RulesConfigurationEntity", List.of(RULES_CONFIGURATION_ENTITY_NAME));
      registry.put(
          "OptimizationAndCostTypesMappingEntity", List.of(OPT_COST_TYPES_MAPPING_ENTITY_NAME));
      registry.put("TransferScheduleEntity", List.of(TRANSFER_SCHEDULE_ENTITY_NAME));
    }
    log.debug("Registry value : {}", registry);
    return registry;
  }
}
