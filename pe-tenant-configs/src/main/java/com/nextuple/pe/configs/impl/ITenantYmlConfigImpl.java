/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.configs.impl;

import static com.nextuple.common.constants.ConfigKeyConstants.ITEM_BUFFER_ENABLED_CONFIG_KEY;
import static com.nextuple.common.constants.ConfigKeyConstants.PROCESSING_TIME_ML_MAPPER_CLASS_CONFIG_KEY;
import static com.nextuple.common.constants.ConfigKeyConstants.PROCESSING_TIME_ML_OVERRIDE_CLASS_CONFIG_KEY;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.pe.configs.CostConfig;
import com.nextuple.pe.configs.DefaultCarrierPriorityConfig;
import com.nextuple.pe.configs.EventConfig;
import com.nextuple.pe.configs.ITenantConfig;
import com.nextuple.pe.configs.LogSuppressionServiceOptionsConfig;
import com.nextuple.pe.configs.PublishEddOnPageConfig;
import com.nextuple.pe.configs.ServiceOptionConfig;
import com.nextuple.pe.configs.ServiceOptionIVTypeMappingConfig;
import com.nextuple.pe.configs.SourcingConfig;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConditionalOnProperty(name = "tenant-configuration", havingValue = "applicationYml")
public class ITenantYmlConfigImpl implements ITenantConfig {
  @Autowired ServiceOptionConfig serviceOptionConfig;
  @Autowired ServiceOptionIVTypeMappingConfig serviceOptionIVTypeMappingConfig;
  @Autowired PublishEddOnPageConfig publishEddOnPageConfig;
  @Autowired DefaultCarrierPriorityConfig defaultCarrierPriorityConfig;
  @Autowired LogSuppressionServiceOptionsConfig logSuppressionServiceOptionsConfig;
  @Autowired SourcingConfig sourcingConfig;
  @Autowired EventConfig eventConfig;
  @Autowired CostConfig costConfig;

  @Value("${promise.service.options.DEFAULT}")
  public String defaultServiceOptions;

  @Value("${service-option.inventory-type.mapping.DEFAULT}")
  public String defaultServiceOptionInventoryTypeMapping;

  @Value("${publish.edd-response-on-page.DEFAULT}")
  public String defaultPublishEddResponseOnPage;

  @Value("${default-carrier-priority.value.DEFAULT}")
  public String defaultValueOfCarrierPriority;

  @Value("${log-suppression.service-options.DEFAULT}")
  public String defaultLogSuppressionServiceOptions;

  @Value("${cost.line-solutions-required.DEFAULT}")
  public Integer defaultNoOfLineSolutionsRequired;

  @Value("${cost.line-threshold.DEFAULT}")
  public Integer defaultLineThreshold;

  @Value("${cost.final-solution-cap.DEFAULT}")
  public Integer defaultFinalSolutionCap;

  @Value("${cost.top-solutions-count.DEFAULT}")
  public Integer defaultTopSolutionsCount;

  @Value("${cost.max-solutions.DEFAULT}")
  public Integer defaultMaxSolutions;

  @Value("${buffer.horizon-days}")
  public Integer horizonDays;

  private static final String DEFAULT = "DEFAULT";
  private static final String NO_OF_LINE_SOLUTIONS_REQUIRED = "line-solutions-required";
  private static final String LINE_THRESHOLD = "line-threshold";
  private static final String FINAL_SOLUTIONS_CAP = "final-solutions-cap";
  private static final String TOP_SOLUTIONS_COUNT = "top-solutions-count";
  private static final String MAX_SOLUTIONS = "max-solutions";
  public static final Gson gson = new Gson();
  Map<String, Boolean> publishEnabledMap = new HashMap<>();
  Map<String, String> logLevelMap = new HashMap<>();
  Map<String, Boolean> consoleLogListenEnabledMap = new HashMap<>();

  @Override
  public Integer getNoOfLineSolutionsRequired() {
    return costConfig
        .getCostConfigs()
        .get(NO_OF_LINE_SOLUTIONS_REQUIRED)
        .getOrDefault(getOrgId(), defaultNoOfLineSolutionsRequired);
  }

  @Override
  public Integer getLineThreshold() {
    return costConfig
        .getCostConfigs()
        .get(LINE_THRESHOLD)
        .getOrDefault(getOrgId(), defaultLineThreshold);
  }

  @Override
  public Integer getFinalSolutionCap() {
    return costConfig
        .getCostConfigs()
        .get(FINAL_SOLUTIONS_CAP)
        .getOrDefault(getOrgId(), defaultFinalSolutionCap);
  }

  @Override
  public Integer getTopSolutionsCount() {
    return costConfig
        .getCostConfigs()
        .get(TOP_SOLUTIONS_COUNT)
        .getOrDefault(getOrgId(), defaultTopSolutionsCount);
  }

  @Override
  public Integer getMaxSolutions() {
    return costConfig
        .getCostConfigs()
        .get(MAX_SOLUTIONS)
        .getOrDefault(getOrgId(), defaultMaxSolutions);
  }

  public static Gson getGsonObject() {
    return gson;
  }

  public String getOrgId() {
    return CurrentThreadContext.getLogContext().getTenantId();
  }

  public String getServiceOptions() {
    return serviceOptionConfig.getOptionsMap().getOrDefault(getOrgId(), defaultServiceOptions);
  }

  public Set<String> getServiceOptionsList() {
    return new HashSet<>(Arrays.asList(getServiceOptions().split(",")));
  }

  public String getServiceOptionInventoryTypeMapping() {
    return serviceOptionIVTypeMappingConfig
        .getMapping()
        .getOrDefault(getOrgId(), defaultServiceOptionInventoryTypeMapping);
  }

  public String getPublishEddResponseOnPage() {
    return publishEddOnPageConfig
        .getEddResponseOnPage()
        .getOrDefault(getOrgId(), defaultPublishEddResponseOnPage);
  }

  public Set<String> getPublishEddResponseOnPageList() {
    return new HashSet<>(Arrays.asList(getPublishEddResponseOnPage().split(",")));
  }

  public String getDefaultCarrierPriority() {
    return defaultCarrierPriorityConfig
        .getValue()
        .getOrDefault(getOrgId(), defaultValueOfCarrierPriority);
  }

  public String getLogSuppressionServiceOptions() {
    return logSuppressionServiceOptionsConfig
        .getServiceOptions()
        .getOrDefault(getOrgId(), defaultLogSuppressionServiceOptions);
  }

  public Set<String> getLogSuppressionServiceOptionsList() {
    return new HashSet<>(Arrays.asList(getLogSuppressionServiceOptions().split(",")));
  }

  public Integer getNumberOfSolutions() {
    return (Integer) getSourcingConfigValue("no-of-solution");
  }

  public Integer getNumberOfNodes() {
    return (Integer) getSourcingConfigValue("no-of-nodes");
  }

  public Map<String, Object> getDefaultEventProperties() {
    return eventConfig.getEvent().get(DEFAULT);
  }

  public Set<String> getAllowedPagesListForPublishingEvent() {
    String allowedPages =
        (String)
            eventConfig
                .getEvent()
                .getOrDefault(getOrgId(), getDefaultEventProperties())
                .get("allowedPages");
    return new HashSet<>(Arrays.asList(allowedPages.split(",")));
  }

  public Map<String, Boolean> getPublishEnabledMap() {
    if (ObjectUtils.isEmpty(publishEnabledMap)) {
      Type type = new TypeToken<Map<String, Boolean>>() {}.getType();
      String publishEnabledString =
          (String)
              eventConfig
                  .getEvent()
                  .getOrDefault(getOrgId(), getDefaultEventProperties())
                  .get("publishEnabled");
      publishEnabledMap = getGsonObject().fromJson(publishEnabledString, type);
    }
    return publishEnabledMap;
  }

  public Map<String, String> getLogLevelMap() {
    if (ObjectUtils.isEmpty(logLevelMap)) {
      Type type = new TypeToken<Map<String, String>>() {}.getType();
      String logLevelString =
          (String)
              eventConfig
                  .getEvent()
                  .getOrDefault(getOrgId(), getDefaultEventProperties())
                  .get("logLevel");
      logLevelMap = getGsonObject().fromJson(logLevelString, type);
    }
    return logLevelMap;
  }

  public Map<String, Boolean> getConsoleLogListenEnabledMap() {
    if (ObjectUtils.isEmpty(consoleLogListenEnabledMap)) {
      Type type = new TypeToken<Map<String, String>>() {}.getType();
      String consoleLogListenEnabledString =
          (String)
              eventConfig
                  .getEvent()
                  .getOrDefault(getOrgId(), getDefaultEventProperties())
                  .get("consoleLogListenEnabled");
      consoleLogListenEnabledMap = getGsonObject().fromJson(consoleLogListenEnabledString, type);
    }
    return consoleLogListenEnabledMap;
  }

  public String getSortBy() {
    return (String)
        eventConfig.getEvent().getOrDefault(getOrgId(), getDefaultEventProperties()).get("sortBy");
  }

  @Override
  public Integer getBufferHorizonDays() {
    return horizonDays;
  }

  @Override
  public String getProcessingTimeComputation() {
    return (String) getSourcingConfigValue("processing-time-computation");
  }

  @Override
  public String getNodeWorkingHours() {
    return (String) getSourcingConfigValue("node-working-hours");
  }

  @Override
  public String getProcessingTimeMLOverrideClass() {
    return (String) getSourcingConfigValue(PROCESSING_TIME_ML_OVERRIDE_CLASS_CONFIG_KEY);
  }

  @Override
  public Boolean getPromisingIntermediateEventsEnabled() {
    return (Boolean) getSourcingConfigValue("promising-intermediate-event-enabled");
  }

  private Object getSourcingConfigValue(String configKey) {
    Map<String, Object> defaultSourcingProperties = sourcingConfig.getSourcing().get(DEFAULT);
    return sourcingConfig
        .getSourcing()
        .getOrDefault(getOrgId(), defaultSourcingProperties)
        .get(configKey);
  }

  @Override
  public String getProcessingTimeMLMapper() {
    return (String) getSourcingConfigValue(PROCESSING_TIME_ML_MAPPER_CLASS_CONFIG_KEY);
  }

  @Override
  public Boolean getIsCutoffApplied() {
    return Boolean.getBoolean(
        (String) getSourcingConfigValue("is-ml-processing-time-cutoff-applied"));
  }

  @Override
  public Boolean getItemBufferEnabled() {
    return (Boolean) getSourcingConfigValue(ITEM_BUFFER_ENABLED_CONFIG_KEY);
  }
}
