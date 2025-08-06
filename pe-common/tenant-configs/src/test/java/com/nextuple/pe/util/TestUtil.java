/*
 * Copyright (c) 2023., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.util;

import static com.nextuple.common.constants.ConfigKeyConstants.PROCESSING_TIME_ML_OVERRIDE_CLASS_CONFIG_KEY;

import com.nextuple.configuration.cache.domain.TenantConfigdataCacheValue;
import java.util.Map;

public class TestUtil {
  public static final String GEO_ZONE = "KOA";
  public static final String DEFAULT_CAPACITY_ENABLED_FLAG = "false";
  public static final String DEFAULT_TRANSFER_ENABLED = "false";
  private static final String CM_UOM = "CM";
  public static final String LBS_UOM = "LBS";
  private static final String IS_HAZMAT = "isHazmat";
  public static final String ORG_ID = "NEXTUPLE_GR";
  public static final String CARRIER_SERVICE_ID = "PURO-POST";
  public static final String DEFAULT_PROCESSING_TIME_COMPUTATION = "HIERARCHICAL";
  public static final String DEFAULT_NODE_WORKING_HOURS = "00:00-23:59";
  public static final String DEFAULT_ITEM_BUFFER_ENABLED = "false";
  public static final String DEFAULT_PROMISE_INTERMEDIATE_EVENT_ENABLED_FLAG = "true";
  public static final String DEFAULT_SHIP_TOGETHER_ENABLED_FLAG = "true";
  public static final String DEFAULT_INVENTORY_MISSING_ACTION = "CANCEL_DEMAND";

  public Map<String, Object> getSourcingConfigValueForOrg() {
    return Map.of("no-of-solution", 3, "no-of-nodes", 8);
  }

  public Map<String, Object> getDefaultSourcingConfigValue() {
    return Map.of(
        "no-of-solution",
        2,
        "no-of-nodes",
        9,
        "processing-time-computation",
        TestUtil.DEFAULT_PROCESSING_TIME_COMPUTATION,
        PROCESSING_TIME_ML_OVERRIDE_CLASS_CONFIG_KEY,
        "com.nextuple.module.TestClass");
  }

  public Map<String, Object> getEventConfigValueForOrg() {
    return Map.of(
        "allowedPages",
        "checkout",
        "publishEnabled",
        "{}",
        "logLevel",
        "{GenericExceptionEvent: \"ERROR\"}",
        "consoleLogListenEnabled",
        "{}",
        "sortBy",
        "@timestamp");
  }

  public Map<String, Object> getDefaultEventConfigValue() {
    return Map.of(
        "allowedPages",
        "itemDetail",
        "publishEnabled",
        "{}",
        "logLevel",
        "{}",
        "consoleLogListenEnabled",
        "{}",
        "sortBy",
        "@timestamp");
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForServiceOptions() {
    return TenantConfigdataCacheValue.builder()
        .configKey("service-options")
        .configValue("SDND, EXPRESS, STANDARD")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForInventoryMissingLinesAction() {
    return TenantConfigdataCacheValue.builder()
        .configKey("inventory-missing-lines-action")
        .configValue("CANCEL_DEMAND")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForOrderOperations() {
    return TenantConfigdataCacheValue.builder()
        .configKey("order-operations")
        .configValue("CREATE, CANCEL, EDD_ENQUIRY, EDD_OVERRIDE")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForTemplates() {
    return TenantConfigdataCacheValue.builder()
        .configKey("templates")
        .configValue("processCreateOrder,processCancelOrder,processCancelLineOrder")
        .build();
  }

  public TenantConfigdataCacheValue
      getTenantConfigCacheValueForServiceOptionsInventoryTypeMapping() {
    return TenantConfigdataCacheValue.builder()
        .configKey("service-option-inventory-type-mapping")
        .configValue("SDND:PICK,EXPRESS:SHIP")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForPublishEddResponse() {
    return TenantConfigdataCacheValue.builder()
        .configKey("publish-edd-response-on-page")
        .configValue("checkout")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForDefaultCarrierPriority() {
    return TenantConfigdataCacheValue.builder()
        .configKey("default-carrier-priority-value")
        .configValue("0")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForLogSuppression() {
    return TenantConfigdataCacheValue.builder()
        .configKey("log-suppression-service-options")
        .configValue("SDND,NEXTDAY")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueGetAllowedPages() {
    return TenantConfigdataCacheValue.builder()
        .configKey("event-allowed-pages")
        .configValue("checkout")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForNoOfSolution() {
    return TenantConfigdataCacheValue.builder()
        .configKey("sourcing-no-of-solution")
        .configValue("3")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForNoOfNodes() {
    return TenantConfigdataCacheValue.builder()
        .configKey("sourcing-no-of-nodes")
        .configValue("8")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueGetPublishEnabled() {
    return TenantConfigdataCacheValue.builder()
        .configKey("event-publish-enabled")
        .configValue(
            "{ItemDetailsEvent: false,OrderLinesWithShipNodeIdEvent: false,OrderLineEligibilityEvent: false,EligibleItemNodeTypesEvent: false,InventoryDetailsEvent: false,WeightageDetailsEvent: false,SourcingSolutionEvent: false,SuggestedPromiseEvent: false,TransitDetailsEvent: false, ReservationRequestEvent: false,ReservationResponseEvent: false}")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueGetLogLevel() {
    return TenantConfigdataCacheValue.builder()
        .configKey("event-log-level")
        .configValue("{ExceptionEvent: ERROR}")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueGetRuleCraftConfig() {
    return TenantConfigdataCacheValue.builder()
        .configKey("rule-craft-engine-config")
        .configValue(
            "{\"inboundProcessingTime\": {\"ruleFilterStrategy\": \"ruleFilterStrategy1\",\"ruleGroup\":\"ruleGroup\"}}")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueOperationTemplateMapping() {
    return TenantConfigdataCacheValue.builder()
        .configKey("operation-template-mapping")
        .configValue(
            "{\"CREATE\":\"processCreateOrder\",\"CANCEL\":\"processCancelOrder\",\"CANCEL_LINE\":\"processCancelLineOrder\"}")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueConsoleLogListenEnabledMap() {
    return TenantConfigdataCacheValue.builder()
        .configKey("event-console-log-listen")
        .configValue("{}")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueGetSortBy() {
    return TenantConfigdataCacheValue.builder()
        .configKey("event-sort-by")
        .configValue("eventTimeEpochMillis")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForProcessingTimeComputation() {
    return TenantConfigdataCacheValue.builder()
        .configKey("processing-time-computation")
        .configValue("ADDITION")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForNodeWorkingHours() {
    return TenantConfigdataCacheValue.builder()
        .configKey("node-working-hours")
        .configValue("08:00-19:00")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForItemBufferEnabled() {
    return TenantConfigdataCacheValue.builder()
        .configKey("item-buffer-enabled")
        .configValue("true")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForMLProcessingTimeOverride() {
    return TenantConfigdataCacheValue.builder()
        .configKey("processing-time-ml-override-class")
        .configValue("com.nextuple.promise.sourcing.impl.DefaultMLOverridingImpl")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForShipChargeCappingLogic() {
    return TenantConfigdataCacheValue.builder()
        .configKey("ship-charge-capping-logic-enabled")
        .configValue("true")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForShipChargeCappingConstants() {
    return TenantConfigdataCacheValue.builder()
        .configKey("ship-charge-capping-constants")
        .configValue("10,20")
        .build();
  }

  public TenantConfigdataCacheValue
      getTenantConfigCacheValueForSelectedAttributeForTargetProfitMargins() {
    return TenantConfigdataCacheValue.builder()
        .configKey("target-gross-profit-margins-selected-attribute")
        .configValue("itemCategory")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForServiceOptionHierarchy() {
    return TenantConfigdataCacheValue.builder()
        .configKey("service-option-hierarchy-for-recommendation")
        .configValue("STANDARD,EXPRESS")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValueForTargetProfitMargins(
      String attributeName) {
    return TenantConfigdataCacheValue.builder()
        .configKey("target-gross-profit-margins-" + attributeName)
        .configValue("KITCHEN:20,ELECTRONICS:30")
        .build();
  }
}
