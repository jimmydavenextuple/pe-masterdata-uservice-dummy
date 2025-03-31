/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.configs.impl;

import static com.nextuple.common.constants.CommonConstants.CONFIG_KEY;
import static com.nextuple.common.constants.CommonConstants.ORG_ID;
import static com.nextuple.common.constants.ConfigKeyConstants.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.PromisingEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.configuration.cache.domain.TenantConfigdataCacheKey;
import com.nextuple.configuration.cache.service.TenantConfigdataNearCacheService;
import com.nextuple.pe.configs.ITenantConfig;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConditionalOnProperty(
    name = "tenant-configuration",
    havingValue = "configDatabase",
    matchIfMissing = true)
public class TenantDBConfigImpl implements ITenantConfig {

  private static final Logger logger = LoggerFactory.getLogger(TenantDBConfigImpl.class);
  @Autowired TenantConfigdataNearCacheService tenantConfigdataNearCacheService;

  @Value("${sourcing.DEFAULT.processing-time-computation:HIERARCHICAL}")
  public String defaultProcessingTimeComputation;

  @Value("${sourcing.DEFAULT.node-working-hours:00:00-23:59}")
  public String defaultNodeWorkingHours;

  @Value("${sourcing.DEFAULT.item-buffer-enabled:false}")
  public String defaultItemBufferEnabled;

  @Value("${sourcing.DEFAULT.promising-intermediate-event-enabled:true}")
  public String defaultPromisingIntermediateEventEnabled;

  @Value("${sourcing.DEFAULT.capacity-enabled:false}")
  public String defaultCapacityFlag;

  @Value("${sourcing.DEFAULT.ship-charge-capping-logic-enabled:false}")
  public String defaultShipChargeCappingLogicFlag;

  @Value("${sourcing.DEFAULT.transfer-schedule-horizon-days:1}")
  public String defaultTransferScheduleHorizonDays;

  @Value("${sourcing.DEFAULT.transfer-schedule-past-days:1}")
  public String defaultTransferSchedulePastDays;

  @Value("${sourcing.DEFAULT.transfers-enabled:false}")
  public String defaultTransfersEnabled;

  @Value("${sourcing.DEFAULT.recommendation-enabled:false}")
  public String defaultRecommendationEngineFlag;

  public static final Gson gson = new Gson();

  public static Gson getGsonObject() {
    return gson;
  }

  @Override
  public Integer getNoOfLineSolutionsRequired() {
    return Integer.parseInt(getTenantConfigdataCacheValue("line-solutions-required"));
  }

  @Override
  public Boolean getRecommendationEngineEnabledFlag() {
    return Boolean.valueOf(
        getTenantConfiguration(RECOMMENDATION_ENABLED_CONFIG_KEY, defaultRecommendationEngineFlag));
  }

  @Override
  public Integer getLineThreshold() {
    return Integer.parseInt(getTenantConfigdataCacheValue("line-threshold"));
  }

  @Override
  public Integer getFinalSolutionCap() {
    return Integer.parseInt(getTenantConfigdataCacheValue("final-solutions-cap"));
  }

  @Override
  public Integer getTopSolutionsCount() {
    return Integer.parseInt(getTenantConfigdataCacheValue("top-solutions-count"));
  }

  @Override
  public Integer getMaxSolutions() {
    return Integer.parseInt(getTenantConfigdataCacheValue("max-solutions"));
  }

  @Override
  public String getProcessingTimeMLOverrideClass() {
    return getTenantConfigdataCacheValue(PROCESSING_TIME_ML_OVERRIDE_CLASS_CONFIG_KEY);
  }

  @Override
  public String getServiceOptions() {
    return getTenantConfigdataCacheValue(SERVICE_OPTIONS_CONFIG_KEY);
  }

  @Override
  public Set<String> getServiceOptionsList() {
    return new HashSet<>(Arrays.asList(getServiceOptions().split("\\s*,+\\s*")));
  }

  @Override
  public String getServiceOptionInventoryTypeMapping() {
    return getTenantConfigdataCacheValue(SERVICE_OPTIONS_INVENTORY_TYPE_MAPPING_CONFIG_KEY);
  }

  @Override
  public Integer getCapacityHorizon() {
    return Integer.valueOf(getTenantConfigdataCacheValue(CAPACITY_HORIZON));
  }

  @Override
  public Boolean getTransfersEnabled() {
    return Boolean.valueOf(getTenantConfiguration(TRANSFERS_ENABLED, defaultTransfersEnabled));
  }

  public Integer getNumberOfSolutions(Boolean isCapacityEnabled) {
    return Boolean.TRUE.equals(isCapacityEnabled)
        ? CAPACITY_SOLUTION_COUNT
        : Integer.valueOf(getTenantConfigdataCacheValue(SOURCING_NO_OF_SOLUTION_CONFIG_KEY));
  }

  @Override
  public String getPublishEddResponseOnPage() {
    return getTenantConfigdataCacheValue(PUBLISH_EDD_RESPONSE_ON_PAGE_CONFIG_KEY);
  }

  @Override
  public Set<String> getPublishEddResponseOnPageList() {
    return new HashSet<>(Arrays.asList(getPublishEddResponseOnPage().split(",")));
  }

  @Override
  public String getDefaultCarrierPriority() {
    return getTenantConfigdataCacheValue(DEFAULT_CARRIER_PRIORITY_VALUE_CONFIG_KEY);
  }

  @Override
  public Boolean getShipChargeCappingLogicEnabledFlag() {
    return Boolean.valueOf(
        getTenantConfiguration(
            CAPPING_LOGIC_ENABLED_CONFIG_KEY, defaultShipChargeCappingLogicFlag));
  }

  @Override
  public String getShipChargeCappingConstants() {
    return getTenantConfigurationWithoutThrowingException(SHIP_CHARGE_CAPPING_CONSTANTS_CONFIG_KEY);
  }

  @Override
  public String getShipChargeConstantsAndCostTypesMapping() {
    return getTenantConfigurationWithoutThrowingException(
        CAPPING_AND_COST_TYPES_MAPPING_CONFIG_KEY);
  }

  @Override
  public String getAttributeForTargetProfitMargins() {
    return getTenantConfigurationWithoutThrowingException(
        SELECTED_ATTRIBUTE_FOR_TARGET_MARGINS_CONFIG_KEY);
  }

  @Override
  public String getServiceOptionHierarchy() {
    return getTenantConfigurationWithoutThrowingException(SERVICE_OPTION_HIERARCHY_CONFIG_KEY);
  }

  @Override
  public String getRecommendationEngineImplClass() {
    return getTenantConfigdataCacheValue(RECOMMENDATION_IMPL_CLASS_NAME_CONFIG_KEY);
  }

  @Override
  public Integer getTransferScheduleHorizonDays() {
    return Integer.valueOf(
        getTenantConfiguration(
            TRANSFER_HORIZON_DAYS_CONFIG_KEY, defaultTransferScheduleHorizonDays));
  }

  @Override
  public Integer getTransferSchedulePastDays() {
    return Integer.valueOf(
        getTenantConfiguration(TRANSFER_PAST_DAYS_CONFIG_KEY, defaultTransferSchedulePastDays));
  }

  @Override
  public String getTargetProfitMargins(String attributeName) {
    return getTenantConfigurationWithoutThrowingException(
        TARGET_GROSS_PROFIT_MARGINS_CONFIG_KEY + attributeName);
  }

  private String getTenantConfigurationWithoutThrowingException(String configKey) {
    String orgId = getOrgId();
    var cacheKey = TenantConfigdataCacheKey.builder().orgId(orgId).configKey(configKey).build();
    var cacheValue = tenantConfigdataNearCacheService.get(cacheKey);

    if (Objects.isNull(cacheValue) || Objects.isNull(cacheValue.getConfigValue())) {
      logger.debug(
          "Tenant Configuration not found for given orgId: {} and configKey: {}", orgId, configKey);
      return null;
    }
    return cacheValue.getConfigValue();
  }

  @Override
  public String getLogSuppressionServiceOptions() {
    return getTenantConfigdataCacheValue(LOG_SUPPRESSION_SERVICE_OPTIONS_CONFIG_KEY);
  }

  @Override
  public Set<String> getLogSuppressionServiceOptionsList() {
    return new HashSet<>(Arrays.asList(getLogSuppressionServiceOptions().split(",")));
  }

  @Override
  public Integer getBufferHorizonDays() {
    return Integer.parseInt(getTenantConfigdataCacheValue(BUFFER_HORIZON_DAYS));
  }

  @Override
  public String getProcessingTimeComputation() {
    return getTenantConfiguration(
        PROCESSING_TIME_COMPUTATION_CONFIG_KEY, defaultProcessingTimeComputation);
  }

  @Override
  public String getNodeWorkingHours() {
    return getTenantConfiguration(NODE_WORKING_HOURS_CONFIG_KEY, defaultNodeWorkingHours);
  }

  public Integer getNumberOfSolutions() {
    return Integer.valueOf(getTenantConfigdataCacheValue(SOURCING_NO_OF_SOLUTION_CONFIG_KEY));
  }

  public Integer getNumberOfNodes() {
    return Integer.valueOf(getTenantConfigdataCacheValue(SOURCING_NO_OF_NODES_CONFIG_KEY));
  }

  public Set<String> getAllowedPagesListForPublishingEvent() {
    String allowedPages = getTenantConfigdataCacheValue(EVENT_ALLOWED_PAGES_CONFIG_KEY);
    return new HashSet<>(Arrays.asList(allowedPages.split(",")));
  }

  public Map<String, Boolean> getPublishEnabledMap() {
    Type type = new TypeToken<Map<String, Boolean>>() {}.getType();
    String publishEnabledString = getTenantConfigdataCacheValue(EVENT_PUBLISH_ENABLED_CONFIG_KEY);
    return getGsonObject().fromJson(publishEnabledString, type);
  }

  public Map<String, String> getLogLevelMap() {
    Type type = new TypeToken<Map<String, String>>() {}.getType();
    String logLevelString = getTenantConfigdataCacheValue(EVENT_LOG_LEVEL_CONFIG_KEY);
    return getGsonObject().fromJson(logLevelString, type);
  }

  public Map<String, Boolean> getConsoleLogListenEnabledMap() {
    Type type = new TypeToken<Map<String, String>>() {}.getType();
    String consoleLogListenEnabledString =
        getTenantConfigdataCacheValue(EVENT_CONSOLE_LOG_LISTEN_CONFIG_KEY);
    return getGsonObject().fromJson(consoleLogListenEnabledString, type);
  }

  public String getSortBy() {
    return getTenantConfigdataCacheValue(EVENT_SORT_BY_CONFIG_KEY);
  }

  public String getProcessingTimeMLMapper() {
    return getTenantConfigdataCacheValue(PROCESSING_TIME_ML_MAPPER_CLASS_CONFIG_KEY);
  }

  @Override
  public Boolean getIsCutoffApplied() {
    return Boolean.parseBoolean(
        getTenantConfiguration("is-ml-processing-time-cutoff-applied", "true"));
  }

  @Override
  public Boolean getCapacityEnabledFlag() {
    return Boolean.valueOf(getTenantConfiguration(CAPACITY_ENABLED_FLAG, defaultCapacityFlag));
  }

  @Override
  public Boolean getItemBufferEnabled() {
    return Boolean.valueOf(
        getTenantConfiguration(ITEM_BUFFER_ENABLED_CONFIG_KEY, defaultItemBufferEnabled));
  }

  @Override
  public Boolean getPromisingIntermediateEventsEnabled() {
    return Boolean.valueOf(
        getTenantConfiguration(
            "promising-intermediate-event-enabled", defaultPromisingIntermediateEventEnabled));
  }

  private String getTenantConfigdataCacheValue(String configKey) {
    return getTenantConfiguration(configKey, null);
  }

  private String getTenantConfiguration(String configKey, String defaultValue) {
    String orgId = getOrgId();
    var cacheKey = TenantConfigdataCacheKey.builder().orgId(orgId).configKey(configKey).build();
    var cacheValue = tenantConfigdataNearCacheService.get(cacheKey);

    if (Objects.isNull(cacheValue) || Objects.isNull(cacheValue.getConfigValue())) {
      if (Objects.nonNull(defaultValue)) return defaultValue;
      logger.debug(
          "Tenant Configuration not found for given orgId: {} and configKey: {}", orgId, configKey);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CONFIG_KEY, FieldError.builder().rejectedValue(configKey).build());
      String errorMessage =
          "Tenant Configuration not found for given orgId and configKey %s".formatted(configKey);
      throw new PromisingEngineException(errorMessage);
    }
    return cacheValue.getConfigValue();
  }

  public String getOrgId() {
    return CurrentThreadContext.getLogContext().getTenantId();
  }
}
