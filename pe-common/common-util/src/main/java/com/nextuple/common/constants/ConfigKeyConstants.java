package com.nextuple.common.constants;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigKeyConstants {
  public static final String SERVICE_OPTIONS_CONFIG_KEY = "service-options";
  public static final String SERVICE_OPTIONS_INVENTORY_TYPE_MAPPING_CONFIG_KEY =
      "service-option-inventory-type-mapping";
  public static final String PUBLISH_EDD_RESPONSE_ON_PAGE_CONFIG_KEY =
      "publish-edd-response-on-page";
  public static final String DEFAULT_CARRIER_PRIORITY_VALUE_CONFIG_KEY =
      "default-carrier-priority-value";
  public static final String LOG_SUPPRESSION_SERVICE_OPTIONS_CONFIG_KEY =
      "log-suppression-service-options";
  public static final String SOURCING_NO_OF_SOLUTION_CONFIG_KEY = "sourcing-no-of-solution";
  public static final String SOURCING_NO_OF_NODES_CONFIG_KEY = "sourcing-no-of-nodes";
  public static final String NODE_TYPES_CONFIG_KEY = "node-types";
  public static final String EVENT_ALLOWED_PAGES_CONFIG_KEY = "event-allowed-pages";
  public static final String EVENT_SORT_BY_CONFIG_KEY = "event-sort-by";
  public static final String EVENT_PUBLISH_ENABLED_CONFIG_KEY = "event-publish-enabled";
  public static final String EVENT_LOG_LEVEL_CONFIG_KEY = "event-log-level";
  public static final String EVENT_CONSOLE_LOG_LISTEN_CONFIG_KEY = "event-console-log-listen";
  public static final String BUFFER_HORIZON_DAYS = "buffer-horizon-days";
  public static final String PROCESSING_TIME_COMPUTATION_CONFIG_KEY = "processing-time-computation";
  public static final String NODE_WORKING_HOURS_CONFIG_KEY = "node-working-hours";
  public static final String CUSTOM_ATTRIBUTES_CONFIG_KEY = "custom-attributes";
  public static final String LINES_CUSTOM_ATTRIBUTES_CONFIG_KEY = "lines-custom-attributes";
  public static final String PROCESSING_TIME_ML_OVERRIDE_CLASS_CONFIG_KEY =
      "processing-time-ml-override-class";
  public static final String PROCESSING_TIME_ML_MAPPER_CLASS_CONFIG_KEY =
      "processing-time-ml-mapper-class";
  public static final String ITEM_BUFFER_ENABLED_CONFIG_KEY = "item-buffer-enabled";
  public static final String CAPACITY_ENABLED_FLAG = "capacity-enabled";
  public static final String CAPACITY_HORIZON = "capacity-horizon";
  public static final String TRANSFERS_ENABLED = "transfers-enabled";
  public static final String PROMISING_INTERMEDIATE_EVENT_ENABLED =
      "promising-intermediate-event-enabled";
  public static final String RECOMMENDATION_ENABLED_CONFIG_KEY = "recommendation-enabled";
  public static final String ALLOWED_ATTRIBUTES_FOR_TARGET_MARGINS_CONFIG_KEY =
      "target-gross-profit-margins-attributes-list";
  public static final String SELECTED_ATTRIBUTE_FOR_TARGET_MARGINS_CONFIG_KEY =
      "target-gross-profit-margins-selected-attribute";
  public static final String ALLOWED_ATTRIBUTE_VALUES_FOR_TARGET_MARGINS_CONFIG_KEY =
      "target-gross-profit-margins-allowed-values-";
  public static final String TARGET_GROSS_PROFIT_MARGINS_CONFIG_KEY =
      "target-gross-profit-margins-";
  public static final String CAPPING_LOGIC_ENABLED_CONFIG_KEY = "ship-charge-capping-logic-enabled";
  public static final String SHIP_CHARGE_CAPPING_CONSTANTS_CONFIG_KEY =
      "ship-charge-capping-constants";
  public static final String CAPPING_AND_COST_TYPES_MAPPING_CONFIG_KEY =
      "ship-charge-capping-and-cost-types-mapping";
  public static final String SERVICE_OPTION_HIERARCHY_CONFIG_KEY =
      "service-option-hierarchy-for-recommendation";
  public static final String RECOMMENDATION_IMPL_CLASS_NAME_CONFIG_KEY =
      "recommendation-engine-impl-class-name";
  public static final String TRANSFER_HORIZON_DAYS_CONFIG_KEY = "transfer-schedule-horizon-days";
  public static final String TRANSFER_PAST_DAYS_CONFIG_KEY = "transfer-schedule-past-days";
  public static final String SHIP_TOGETHER_ENABLED_FLAG = "ship-together-enabled";
  public static final String ENABLE_FUTURE_AVAILABILITY_CONFIG_KEY = "enable-future-availability";
  public static final String ENABLE_AVAILABILITY_SORTING_CONFIG_KEY = "enable-availability-sorting";
  public static final String ORDER_OPERATIONS_CONFIG_KEY = "order-operations";
  public static final String TEMPLATES = "templates";
  public static final String OPERATION_TEMPLATE_MAPPING = "operation-template-mapping";
  public static final String INVENTORY_MISSING_LINES_ACTION_CONFIG_KEY =
      "inventory-missing-lines-action";
  public static final String TRANSIT_HORIZON_CONFIG_KEY = "transit-horizon-days";
  public static final String CARRIER_CALENDAR_PAST_LOOKUP_DAYS_CONFIG_KEY =
      "carrier-calender-past-lookup-days";
  public static final String NODE_CALENDAR_PAST_LOOKUP_DAYS_CONFIG_KEY =
      "node-calender-past-lookup-days";
  public static final String INBOUND_PROCESSING_TIME_ENABLED_KEY =
      "inbound-processing-time-enabled";
  public static final String RULE_CRAFT_ENGINE_CONFIG_MAP = "rule-craft-engine-config";

  public static final String CAPACITY_AWARE_CONFIG_KEY = "capacity-aware";

  public static final String CAPACITY_FUTURE_LOOKUP_DAYS_CONFIG_KEY = "capacity-future-lookup-days";
  public static final String CAPACITY_PAST_LOOKBACK_DAYS_CONFIG_KEY = "capacity-past-lookback-days";
  public static final String CAPACITY_MODEL_NAME_CONFIG_KEY = "capacity-model-name";

  public static final List<String> CONFIG_KEYS_MULTIPLE_VALUES_NOT_ALLOWED =
      List.of(SELECTED_ATTRIBUTE_FOR_TARGET_MARGINS_CONFIG_KEY);

  public static final Integer CAPACITY_SOLUTION_COUNT = 1;

  public static final String DEFAULT_CAPACITY_PAST_LOOKBACK_DAYS =
      "{\"outbound\": 0, \"transport\": 0, \"receiving\": 0}";
  public static final String DEFAULT_CAPACITY_FUTURE_LOOKUP_DAYS =
      "{\"outbound\": 20, \"transport\": 20, \"receiving\": 20}";
  public static final String DEFAULT_EMPTY_CAPACITY_MODEL_JSON = "{}";
}
