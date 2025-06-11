package com.nextuple.pe.configs.impl;

import static com.nextuple.common.constants.ConfigKeyConstants.ENABLE_AVAILABILITY_SORTING_CONFIG_KEY;
import static com.nextuple.common.constants.ConfigKeyConstants.ENABLE_FUTURE_AVAILABILITY_CONFIG_KEY;
import static com.nextuple.common.constants.ConfigKeyConstants.TRANSFERS_ENABLED;
import static com.nextuple.common.constants.ConfigKeyConstants.TRANSFER_HORIZON_DAYS_CONFIG_KEY;
import static com.nextuple.common.constants.ConfigKeyConstants.TRANSFER_PAST_DAYS_CONFIG_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nextuple.common.constants.ConfigKeyConstants;
import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.pe.configs.CapacityConfig;
import com.nextuple.pe.configs.DefaultCarrierPriorityConfig;
import com.nextuple.pe.configs.EventConfig;
import com.nextuple.pe.configs.LogSuppressionServiceOptionsConfig;
import com.nextuple.pe.configs.PromiseCoordinationConfig;
import com.nextuple.pe.configs.PublishEddOnPageConfig;
import com.nextuple.pe.configs.ServiceOptionConfig;
import com.nextuple.pe.configs.ServiceOptionIVTypeMappingConfig;
import com.nextuple.pe.configs.SourcingConfig;
import com.nextuple.pe.util.TestUtil;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class ITenantYmlConfigImplTest {
  public static final String DEFAULT = "DEFAULT";
  private final String defaultServiceOptions = "SDND,STANDARD";
  private final String defaultServiceOptionInventoryTypeMapping = "SDND:PICK,EXPRESS:SHIP";
  private final String defaultPublishEddResponseOnPage = "checkout";
  private final String defaultValueOfCarrierPriority = "0";
  private final String defaultLogSuppressionServiceOptions = "SDND,NEXTDAY";
  private final String invalidOrgId = "xyz";
  @InjectMocks ITenantYmlConfigImpl iTenantYmlConfigImpl;

  @InjectMocks private TestUtil testUtil;
  @Mock ServiceOptionConfig serviceOptionConfig;
  @Mock ServiceOptionIVTypeMappingConfig serviceOptionIVTypeMappingConfig;
  @Mock PublishEddOnPageConfig publishEddOnPageConfig;
  @Mock DefaultCarrierPriorityConfig defaultCarrierPriorityConfig;
  @Mock LogSuppressionServiceOptionsConfig logSuppressionServiceOptionsConfig;
  @Mock SourcingConfig sourcingConfig;
  @Mock EventConfig eventConfig;
  @Mock CapacityConfig capacityConfig;
  @Mock PromiseCoordinationConfig promiseCoordinationConfig;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        iTenantYmlConfigImpl, "defaultServiceOptions", defaultServiceOptions);
    ReflectionTestUtils.setField(
        iTenantYmlConfigImpl,
        "defaultServiceOptionInventoryTypeMapping",
        defaultServiceOptionInventoryTypeMapping);
    ReflectionTestUtils.setField(
        iTenantYmlConfigImpl, "defaultPublishEddResponseOnPage", defaultPublishEddResponseOnPage);
    ReflectionTestUtils.setField(
        iTenantYmlConfigImpl, "defaultValueOfCarrierPriority", defaultValueOfCarrierPriority);
    ReflectionTestUtils.setField(
        iTenantYmlConfigImpl,
        "defaultLogSuppressionServiceOptions",
        defaultLogSuppressionServiceOptions);
    CurrentThreadContext.getLogContext().setTenantId(TestUtil.ORG_ID);
  }

  @Test
  void getOrgIdTest() {
    String orgId = iTenantYmlConfigImpl.getOrgId();
    Assertions.assertEquals(TestUtil.ORG_ID, orgId);
  }

  @DisplayName("Returns service options for the org, when we have data for orgId in yml")
  @Test
  void getServiceOptionsTest1() {
    String serviceOption = "SDND";
    Mockito.when(serviceOptionConfig.getOptionsMap())
        .thenReturn(Map.of(TestUtil.ORG_ID, serviceOption, "DEFAULT", defaultServiceOptions));

    String serviceOptionsResponse = iTenantYmlConfigImpl.getServiceOptions();
    Assertions.assertEquals(serviceOption, serviceOptionsResponse);
  }

  @DisplayName("Returns default service options, when we don't have data for orgId in yml")
  @Test
  void getServiceOptionsTest2() {
    String serviceOption = "SDND";
    CurrentThreadContext.getLogContext().setTenantId(invalidOrgId);
    Mockito.when(serviceOptionConfig.getOptionsMap())
        .thenReturn(Map.of(TestUtil.ORG_ID, serviceOption, "DEFAULT", defaultServiceOptions));

    String serviceOptionsResponse = iTenantYmlConfigImpl.getServiceOptions();
    Assertions.assertEquals(defaultServiceOptions, serviceOptionsResponse);
  }

  @DisplayName("Returns order operations for the org, when we have data for orgId in yml")
  @Test
  void getOrderOperationsTest() {
    String orderOperation = "CANCEL_LINE";
    Mockito.when(promiseCoordinationConfig.getOrderOperations())
        .thenReturn(Map.of(TestUtil.ORG_ID, orderOperation, "DEFAULT", "CREATE"));

    Set<String> orderOperationResponse = iTenantYmlConfigImpl.getOrderOperations();
    Assertions.assertEquals(Set.of(orderOperation), orderOperationResponse);
  }

  @DisplayName("Returns order operations for the org, when we do not have data for orgId in yml")
  @Test
  void getOrderOperationsTestDefault() {
    String defaultOrderOperation = "CREATE";
    Mockito.when(promiseCoordinationConfig.getOrderOperations())
        .thenReturn(Map.of("DEFAULT", defaultOrderOperation));

    Set<String> orderOperationResponse = iTenantYmlConfigImpl.getOrderOperations();
    Assertions.assertEquals(Set.of(defaultOrderOperation), orderOperationResponse);
  }

  @DisplayName("Returns order operations for the org, when we have data for orgId in yml")
  @Test
  void getTemplatesTest() {
    String templates = "processCreateOrder";
    Mockito.when(promiseCoordinationConfig.getTemplates())
        .thenReturn(Map.of(TestUtil.ORG_ID, templates, "DEFAULT", "processCancelOrder"));

    Set<String> templatesResponse = iTenantYmlConfigImpl.getTemplates();
    Assertions.assertEquals(Set.of(templates), templatesResponse);
  }

  @DisplayName("Returns order operations for the org, when we do not have data for orgId in yml")
  @Test
  void getTemplatesTestDefault() {
    String defaultTemplates = "processCancelOrder";
    Mockito.when(promiseCoordinationConfig.getTemplates())
        .thenReturn(Map.of("DEFAULT", defaultTemplates));

    Set<String> orderOperationResponse = iTenantYmlConfigImpl.getTemplates();
    Assertions.assertEquals(Set.of(defaultTemplates), orderOperationResponse);
  }

  @DisplayName("Returns operation template mapping for the org, when we have data for orgId in yml")
  @Test
  void getOrderOperationMappingTest() {
    Map<String, String> operationTemplateMapping = Map.of("CREATE", "processCreateOrder");
    Mockito.when(promiseCoordinationConfig.getOperationTemplateMapping())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                operationTemplateMapping,
                "DEFAULT",
                Map.of("CANCEL", "processCancelOrder")));

    Map<String, String> operationTemplateMappingResponse =
        iTenantYmlConfigImpl.getOperationTemplateMapping();
    Assertions.assertEquals(operationTemplateMapping, operationTemplateMappingResponse);
  }

  @DisplayName(
      "Returns operation template mapping for the org, when we do not have data for orgId in yml")
  @Test
  void getOrderOperationMappingTestDefault() {
    Map<String, String> defaultOperationTemplateMapping = Map.of("CANCEL", "processCancelOrder");
    Mockito.when(promiseCoordinationConfig.getOperationTemplateMapping())
        .thenReturn(Map.of("DEFAULT", defaultOperationTemplateMapping));

    Map<String, String> operationTemplateMappingResponse =
        iTenantYmlConfigImpl.getOperationTemplateMapping();
    Assertions.assertEquals(defaultOperationTemplateMapping, operationTemplateMappingResponse);
  }

  @DisplayName("Returns service options list")
  @Test
  void getServiceOptionsListTest() {
    String serviceOption = "SDND";
    Mockito.when(serviceOptionConfig.getOptionsMap())
        .thenReturn(Map.of(TestUtil.ORG_ID, serviceOption, "DEFAULT", defaultServiceOptions));

    Set<String> serviceOptionListResponse = iTenantYmlConfigImpl.getServiceOptionsList();
    Assertions.assertEquals(Set.of(serviceOption), serviceOptionListResponse);
  }

  @DisplayName(
      "Returns service options to inventory type mapping for the org, when we have data for orgId in yml")
  @Test
  void getServiceOptionInventoryTypeMappingTest1() {
    String serviceOptionInventoryTypeMapping = "SDND:PICK";
    Mockito.when(serviceOptionIVTypeMappingConfig.getMapping())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                serviceOptionInventoryTypeMapping,
                "DEFAULT",
                defaultServiceOptionInventoryTypeMapping));

    String serviceOptionInventoryTypeMappingResponse =
        iTenantYmlConfigImpl.getServiceOptionInventoryTypeMapping();
    Assertions.assertEquals(
        serviceOptionInventoryTypeMapping, serviceOptionInventoryTypeMappingResponse);
  }

  @DisplayName(
      "Returns default service options to inventory type mapping, when we don't have data for orgId in yml")
  @Test
  void getServiceOptionInventoryTypeMappingTest2() {
    String serviceOptionInventoryTypeMapping = "SDND:PICK";
    CurrentThreadContext.getLogContext().setTenantId(invalidOrgId);
    Mockito.when(serviceOptionIVTypeMappingConfig.getMapping())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                serviceOptionInventoryTypeMapping,
                "DEFAULT",
                defaultServiceOptionInventoryTypeMapping));

    String serviceOptionInventoryTypeMappingResponse =
        iTenantYmlConfigImpl.getServiceOptionInventoryTypeMapping();
    Assertions.assertEquals(
        defaultServiceOptionInventoryTypeMapping, serviceOptionInventoryTypeMappingResponse);
  }

  @DisplayName(
      "Returns info of publishing edd on specific page for the org, when we have data for orgId in yml")
  @Test
  void getPublishEddResponseOnPageTest1() {
    String publishEddResponseOnPage = "checkout";
    Mockito.when(publishEddOnPageConfig.getEddResponseOnPage())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                publishEddResponseOnPage,
                "DEFAULT",
                defaultPublishEddResponseOnPage));

    String publishEddResponseOnPageResponse = iTenantYmlConfigImpl.getPublishEddResponseOnPage();
    Assertions.assertEquals(publishEddResponseOnPage, publishEddResponseOnPageResponse);
  }

  @DisplayName(
      "Returns default info of publishing edd on specific page, when we don't have data for orgId in yml")
  @Test
  void getPublishEddResponseOnPageTest2() {
    String publishEddResponseOnPage = "checkout";
    CurrentThreadContext.getLogContext().setTenantId(invalidOrgId);
    Mockito.when(publishEddOnPageConfig.getEddResponseOnPage())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                publishEddResponseOnPage,
                "DEFAULT",
                defaultPublishEddResponseOnPage));

    String publishEddResponseOnPageResponse = iTenantYmlConfigImpl.getPublishEddResponseOnPage();
    Assertions.assertEquals(defaultPublishEddResponseOnPage, publishEddResponseOnPageResponse);
  }

  @DisplayName("Returns list of publishing edd on specific page")
  @Test
  void getPublishEddResponseOnPageListTest() {
    String publishEddResponseOnPage = "checkout";
    Mockito.when(publishEddOnPageConfig.getEddResponseOnPage())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                publishEddResponseOnPage,
                "DEFAULT",
                defaultPublishEddResponseOnPage));

    Set<String> publishEddResponseOnPageListResponse =
        iTenantYmlConfigImpl.getPublishEddResponseOnPageList();
    Assertions.assertEquals(Set.of(publishEddResponseOnPage), publishEddResponseOnPageListResponse);
  }

  @DisplayName(
      "Returns info of default carrier property for the org, when we have data for orgId in yml")
  @Test
  void getDefaultCarrierPriorityTest1() {
    String carrierPriority = "1";
    Mockito.when(defaultCarrierPriorityConfig.getValue())
        .thenReturn(
            Map.of(TestUtil.ORG_ID, carrierPriority, "DEFAULT", defaultValueOfCarrierPriority));

    String carrierPriorityResponse = iTenantYmlConfigImpl.getDefaultCarrierPriority();
    Assertions.assertEquals(carrierPriority, carrierPriorityResponse);
  }

  @DisplayName(
      "Returns default info of default carrier property, when we don't have data for orgId in yml")
  @Test
  void getDefaultCarrierPriorityTest2() {
    String carrierPriority = "1";
    CurrentThreadContext.getLogContext().setTenantId(invalidOrgId);
    Mockito.when(defaultCarrierPriorityConfig.getValue())
        .thenReturn(
            Map.of(TestUtil.ORG_ID, carrierPriority, "DEFAULT", defaultValueOfCarrierPriority));

    String carrierPriorityResponse = iTenantYmlConfigImpl.getDefaultCarrierPriority();
    Assertions.assertEquals(defaultValueOfCarrierPriority, carrierPriorityResponse);
  }

  @DisplayName(
      "Returns info of log suppression of service option for the org, when we have data for orgId in yml")
  @Test
  void getLogSuppressionServiceOptionsTest1() {
    String logSuppressionServiceOptions = "SDND";
    Mockito.when(logSuppressionServiceOptionsConfig.getServiceOptions())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                logSuppressionServiceOptions,
                "DEFAULT",
                defaultLogSuppressionServiceOptions));

    String logSuppressionServiceOptionsResponse =
        iTenantYmlConfigImpl.getLogSuppressionServiceOptions();
    Assertions.assertEquals(logSuppressionServiceOptions, logSuppressionServiceOptionsResponse);
  }

  @DisplayName(
      "Returns default info of log suppression of service option, when we don't have data for orgId in yml")
  @Test
  void getLogSuppressionServiceOptionsTest2() {
    String logSuppressionServiceOptions = "SDND";
    CurrentThreadContext.getLogContext().setTenantId(invalidOrgId);
    Mockito.when(logSuppressionServiceOptionsConfig.getServiceOptions())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                logSuppressionServiceOptions,
                "DEFAULT",
                defaultLogSuppressionServiceOptions));

    String logSuppressionServiceOptionsResponse =
        iTenantYmlConfigImpl.getLogSuppressionServiceOptions();
    Assertions.assertEquals(
        defaultLogSuppressionServiceOptions, logSuppressionServiceOptionsResponse);
  }

  @DisplayName("Returns list of log suppression of service option")
  @Test
  void getLogSuppressionServiceOptionsListTest() {
    String logSuppressionServiceOptions = "SDND";
    Mockito.when(logSuppressionServiceOptionsConfig.getServiceOptions())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                logSuppressionServiceOptions,
                "DEFAULT",
                defaultLogSuppressionServiceOptions));

    Set<String> logSuppressionServiceOptionsListResponse =
        iTenantYmlConfigImpl.getLogSuppressionServiceOptionsList();
    Assertions.assertEquals(
        Set.of(logSuppressionServiceOptions), logSuppressionServiceOptionsListResponse);
  }

  @DisplayName(
      "Returns number of solutions in the sourcing response for the org, when we have data for orgId in yml")
  @Test
  void getNumberOfSolutionsTest1() {
    Map<String, Object> valueForOrg = testUtil.getSourcingConfigValueForOrg();
    Map<String, Object> defaultValue = testUtil.getDefaultSourcingConfigValue();
    Map<String, Map<String, Object>> sourcingConfigMap =
        Map.of(TestUtil.ORG_ID, valueForOrg, "DEFAULT", defaultValue);
    Mockito.when(sourcingConfig.getSourcing()).thenReturn(sourcingConfigMap);

    Integer numberOfSolutionsResponse = iTenantYmlConfigImpl.getNumberOfSolutions();
    Assertions.assertEquals(valueForOrg.get("no-of-solution"), numberOfSolutionsResponse);
  }

  @DisplayName(
      "Returns default number of solutions in the sourcing response, when we don't have data for orgId in yml")
  @Test
  void getNumberOfSolutionsTest2() {
    CurrentThreadContext.getLogContext().setTenantId(invalidOrgId);
    Map<String, Object> valueForOrg = testUtil.getSourcingConfigValueForOrg();
    Map<String, Object> defaultValue = testUtil.getDefaultSourcingConfigValue();
    Map<String, Map<String, Object>> sourcingConfigMap =
        Map.of(TestUtil.ORG_ID, valueForOrg, "DEFAULT", defaultValue);
    Mockito.when(sourcingConfig.getSourcing()).thenReturn(sourcingConfigMap);

    Integer numberOfSolutionsResponse = iTenantYmlConfigImpl.getNumberOfSolutions();
    Assertions.assertEquals(defaultValue.get("no-of-solution"), numberOfSolutionsResponse);
  }

  @DisplayName(
      "Returns number of nodes in the sourcing response for the org, when we have data for orgId in yml")
  @Test
  void getNumberOfNodesTest1() {
    Map<String, Object> valueForOrg = testUtil.getSourcingConfigValueForOrg();
    Map<String, Object> defaultValue = testUtil.getDefaultSourcingConfigValue();
    Map<String, Map<String, Object>> sourcingConfigMap =
        Map.of(TestUtil.ORG_ID, valueForOrg, "DEFAULT", defaultValue);
    Mockito.when(sourcingConfig.getSourcing()).thenReturn(sourcingConfigMap);

    Integer numberOfNodesResponse = iTenantYmlConfigImpl.getNumberOfNodes();
    Assertions.assertEquals(valueForOrg.get("no-of-nodes"), numberOfNodesResponse);
  }

  @DisplayName(
      "Returns default number of nodes in the sourcing response, when we don't have data for orgId in yml")
  @Test
  void getNumberOfNodesTest2() {
    CurrentThreadContext.getLogContext().setTenantId(invalidOrgId);
    Map<String, Object> valueForOrg = testUtil.getSourcingConfigValueForOrg();
    Map<String, Object> defaultValue = testUtil.getDefaultSourcingConfigValue();
    Map<String, Map<String, Object>> sourcingConfigMap =
        Map.of(TestUtil.ORG_ID, valueForOrg, "DEFAULT", defaultValue);
    Mockito.when(sourcingConfig.getSourcing()).thenReturn(sourcingConfigMap);

    Integer numberOfNodesResponse = iTenantYmlConfigImpl.getNumberOfNodes();
    Assertions.assertEquals(defaultValue.get("no-of-nodes"), numberOfNodesResponse);
  }

  @DisplayName(
      "Returns list of allowed pages for publishing events for the org, when we have data for orgId in yml")
  @Test
  void getAllowedPagesListForPublishingEventTest1() {
    Map<String, Object> valueForOrg = testUtil.getEventConfigValueForOrg();
    Map<String, Object> defaultValue = testUtil.getDefaultEventConfigValue();
    String allowedPages = (String) valueForOrg.get("allowedPages");
    Map<String, Map<String, Object>> eventConfigMap =
        Map.of(TestUtil.ORG_ID, valueForOrg, "DEFAULT", defaultValue);
    Mockito.when(eventConfig.getEvent()).thenReturn(eventConfigMap);

    Set<String> allowedPagesList = iTenantYmlConfigImpl.getAllowedPagesListForPublishingEvent();
    Assertions.assertEquals(
        new HashSet<>(Arrays.asList(allowedPages.split(","))), allowedPagesList);
  }

  @DisplayName(
      "Returns default list of allowed pages for publishing events, when we don't have data for orgId in yml")
  @Test
  void getAllowedPagesListForPublishingEventTest2() {
    CurrentThreadContext.getLogContext().setTenantId(invalidOrgId);
    Map<String, Object> valueForOrg = testUtil.getEventConfigValueForOrg();
    Map<String, Object> defaultValue = testUtil.getDefaultEventConfigValue();
    String allowedPages = (String) defaultValue.get("allowedPages");
    Map<String, Map<String, Object>> eventConfigMap =
        Map.of(TestUtil.ORG_ID, valueForOrg, "DEFAULT", defaultValue);
    Mockito.when(eventConfig.getEvent()).thenReturn(eventConfigMap);

    Set<String> allowedPagesList = iTenantYmlConfigImpl.getAllowedPagesListForPublishingEvent();
    Assertions.assertEquals(
        new HashSet<>(Arrays.asList(allowedPages.split(","))), allowedPagesList);
  }

  @DisplayName(
      "Returns map of publish enabled events for the org, when we have data for orgId in yml")
  @Test
  void getPublishEnabledMapTest1() {
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, String>>() {}.getType();
    Map<String, Object> valueForOrg = testUtil.getEventConfigValueForOrg();
    Map<String, Object> defaultValue = testUtil.getDefaultEventConfigValue();
    String publishEnabled = (String) valueForOrg.get("publishEnabled");
    Map<String, Map<String, Object>> eventConfigMap =
        Map.of(TestUtil.ORG_ID, valueForOrg, "DEFAULT", defaultValue);
    Mockito.when(eventConfig.getEvent()).thenReturn(eventConfigMap);

    Map<String, Boolean> publishEnabledMap = iTenantYmlConfigImpl.getPublishEnabledMap();
    Assertions.assertEquals(gson.fromJson(publishEnabled, type), publishEnabledMap);
  }

  @DisplayName(
      "Returns default map of publish enabled events, when we don't have data for orgId in yml")
  @Test
  void getPublishEnabledMapTest2() {
    CurrentThreadContext.getLogContext().setTenantId(invalidOrgId);
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, String>>() {}.getType();
    Map<String, Object> valueForOrg = testUtil.getEventConfigValueForOrg();
    Map<String, Object> defaultValue = testUtil.getDefaultEventConfigValue();
    String publishEnabled = (String) defaultValue.get("publishEnabled");
    Map<String, Map<String, Object>> eventConfigMap =
        Map.of(TestUtil.ORG_ID, valueForOrg, "DEFAULT", defaultValue);
    Mockito.when(eventConfig.getEvent()).thenReturn(eventConfigMap);

    Map<String, Boolean> publishEnabledMap = iTenantYmlConfigImpl.getPublishEnabledMap();
    Assertions.assertEquals(gson.fromJson(publishEnabled, type), publishEnabledMap);
  }

  @DisplayName("Returns map of log levels for the org, when we have data for orgId in yml")
  @Test
  void getLogLevelMapTest1() {
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, String>>() {}.getType();
    Map<String, Object> valueForOrg = testUtil.getEventConfigValueForOrg();
    Map<String, Object> defaultValue = testUtil.getDefaultEventConfigValue();
    String logLevel = (String) valueForOrg.get("logLevel");
    Map<String, Map<String, Object>> eventConfigMap =
        Map.of(TestUtil.ORG_ID, valueForOrg, "DEFAULT", defaultValue);
    Mockito.when(eventConfig.getEvent()).thenReturn(eventConfigMap);

    Map<String, String> logLevelMap = iTenantYmlConfigImpl.getLogLevelMap();
    Assertions.assertEquals(gson.fromJson(logLevel, type), logLevelMap);
  }

  @DisplayName("Returns default map of log levels, when we don't have data for orgId in yml")
  @Test
  void getLogLevelMapTest2() {
    CurrentThreadContext.getLogContext().setTenantId(invalidOrgId);
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, String>>() {}.getType();
    Map<String, Object> valueForOrg = testUtil.getEventConfigValueForOrg();
    Map<String, Object> defaultValue = testUtil.getDefaultEventConfigValue();
    String logLevel = (String) defaultValue.get("logLevel");
    Map<String, Map<String, Object>> eventConfigMap =
        Map.of(TestUtil.ORG_ID, valueForOrg, "DEFAULT", defaultValue);
    Mockito.when(eventConfig.getEvent()).thenReturn(eventConfigMap);

    Map<String, String> logLevelMap = iTenantYmlConfigImpl.getLogLevelMap();
    Assertions.assertEquals(gson.fromJson(logLevel, type), logLevelMap);
  }

  @DisplayName(
      "Returns map of events which have listen console log enabled for the org, when we have data for orgId in yml")
  @Test
  void getConsoleLogListenEnabledMapTest1() {
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, String>>() {}.getType();
    Map<String, Object> valueForOrg = testUtil.getEventConfigValueForOrg();
    Map<String, Object> defaultValue = testUtil.getDefaultEventConfigValue();
    String consoleLogListenEnabled = (String) valueForOrg.get("consoleLogListenEnabled");
    Map<String, Map<String, Object>> eventConfigMap =
        Map.of(TestUtil.ORG_ID, valueForOrg, "DEFAULT", defaultValue);
    Mockito.when(eventConfig.getEvent()).thenReturn(eventConfigMap);

    Map<String, Boolean> consoleLogListenEnabledMap =
        iTenantYmlConfigImpl.getConsoleLogListenEnabledMap();
    Assertions.assertEquals(
        gson.fromJson(consoleLogListenEnabled, type), consoleLogListenEnabledMap);
  }

  @DisplayName(
      "Returns default map of events which have listen console log enabled, when we don't have data for orgId in yml")
  @Test
  void getConsoleLogListenEnabledMapTest2() {
    CurrentThreadContext.getLogContext().setTenantId(invalidOrgId);
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, String>>() {}.getType();
    Map<String, Object> valueForOrg = testUtil.getEventConfigValueForOrg();
    Map<String, Object> defaultValue = testUtil.getDefaultEventConfigValue();
    String consoleLogListenEnabled = (String) defaultValue.get("consoleLogListenEnabled");
    Map<String, Map<String, Object>> eventConfigMap =
        Map.of(TestUtil.ORG_ID, valueForOrg, "DEFAULT", defaultValue);
    Mockito.when(eventConfig.getEvent()).thenReturn(eventConfigMap);

    Map<String, Boolean> consoleLogListenEnabledMap =
        iTenantYmlConfigImpl.getConsoleLogListenEnabledMap();
    Assertions.assertEquals(
        gson.fromJson(consoleLogListenEnabled, type), consoleLogListenEnabledMap);
  }

  @DisplayName(
      "Returns sort by field for publishing events for the org, when we have data for orgId in yml")
  @Test
  void getSortByForPublishingEventTest1() {
    Map<String, Object> valueForOrg = testUtil.getEventConfigValueForOrg();
    Map<String, Object> defaultValue = testUtil.getDefaultEventConfigValue();
    String sortByOrgValue = (String) valueForOrg.get("sortBy");
    Map<String, Map<String, Object>> eventConfigMap =
        Map.of(TestUtil.ORG_ID, valueForOrg, "DEFAULT", defaultValue);
    Mockito.when(eventConfig.getEvent()).thenReturn(eventConfigMap);

    String sortBy = iTenantYmlConfigImpl.getSortBy();
    Assertions.assertEquals(sortByOrgValue, sortBy);
  }

  @DisplayName(
      "Returns default sort by field for publishing events, when we don't have data for orgId in yml")
  @Test
  void getSortByForPublishingEventTest2() {
    CurrentThreadContext.getLogContext().setTenantId(invalidOrgId);
    Map<String, Object> valueForOrg = testUtil.getEventConfigValueForOrg();
    Map<String, Object> defaultValue = testUtil.getDefaultEventConfigValue();
    String sortByDefaultValue = (String) defaultValue.get("sortBy");
    Map<String, Map<String, Object>> eventConfigMap =
        Map.of(TestUtil.ORG_ID, valueForOrg, "DEFAULT", defaultValue);
    Mockito.when(eventConfig.getEvent()).thenReturn(eventConfigMap);

    String sortBy = iTenantYmlConfigImpl.getSortBy();
    Assertions.assertEquals(sortByDefaultValue, sortBy);
  }

  @DisplayName("Returns processing time computation for the org, when we have default data in yml")
  @Test
  void getProcessingTimeComputationTest() {
    Map<String, Object> defaultValue = testUtil.getDefaultSourcingConfigValue();
    Map<String, Map<String, Object>> sourcingConfigMap = Map.of("DEFAULT", defaultValue);
    Mockito.when(sourcingConfig.getSourcing()).thenReturn(sourcingConfigMap);

    String processingTimeComputation = iTenantYmlConfigImpl.getProcessingTimeComputation();
    Assertions.assertEquals(
        defaultValue.get("processing-time-computation"), processingTimeComputation);

    Mockito.verify(sourcingConfig, Mockito.times(2)).getSourcing();
  }

  @DisplayName("Fetch node working hours from yml property")
  @Test
  void getNodeWorkingHoursTest() {
    Map<String, Object> defaultValue = testUtil.getDefaultSourcingConfigValue();
    Map<String, Map<String, Object>> sourcingConfigMap = Map.of("DEFAULT", defaultValue);
    Mockito.when(sourcingConfig.getSourcing()).thenReturn(sourcingConfigMap);

    String nodeWorkingHours = iTenantYmlConfigImpl.getNodeWorkingHours();
    Assertions.assertEquals(defaultValue.get("node-working-hours"), nodeWorkingHours);

    Mockito.verify(sourcingConfig, Mockito.times(2)).getSourcing();
  }

  @DisplayName("Fetch processing time ml override class from yml property")
  @Test
  void getProcessingTimeMLOverrideClassTest() {
    Map<String, Object> defaultValue = testUtil.getDefaultSourcingConfigValue();
    Map<String, Map<String, Object>> sourcingConfigMap = Map.of("DEFAULT", defaultValue);
    Mockito.when(sourcingConfig.getSourcing()).thenReturn(sourcingConfigMap);

    String processingTimeMLOverrideClass = iTenantYmlConfigImpl.getProcessingTimeMLOverrideClass();
    Assertions.assertEquals(
        defaultValue.get(ConfigKeyConstants.PROCESSING_TIME_ML_OVERRIDE_CLASS_CONFIG_KEY),
        processingTimeMLOverrideClass);

    Mockito.verify(sourcingConfig, Mockito.times(2)).getSourcing();
  }

  @Test
  @DisplayName("Get capacity enabled flag")
  void getCapacityEnabledFlag() {
    when(capacityConfig.getCapacity())
        .thenReturn(Map.of("NEXTUPLE_GR", Map.of("capacity-enabled", false)));
    CurrentThreadContext.getLogContext().setTenantId("NEXTUPLE_GR");
    assertEquals(false, iTenantYmlConfigImpl.getCapacityEnabledFlag());
  }

  @Test
  @DisplayName("Get capacity horizon")
  void getCapacityHoriozn() {
    when(capacityConfig.getCapacity()).thenReturn(Map.of("DEFAULT", Map.of("capacity-horizon", 7)));
    CurrentThreadContext.getLogContext().setTenantId("NEXTUPLE_GR");
    assertEquals(7, iTenantYmlConfigImpl.getCapacityHorizon());
  }

  @Test
  @DisplayName("Get transfer enabled for NEXTUPLE_GR and DEFAULT")
  void getTransfersEnabled() {
    when(sourcingConfig.getSourcing())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                Map.of(TRANSFERS_ENABLED, true),
                DEFAULT,
                Map.of(TRANSFERS_ENABLED, false)));
    CurrentThreadContext.getLogContext().setTenantId(TestUtil.ORG_ID);
    assertTrue(iTenantYmlConfigImpl.getTransfersEnabled());
    CurrentThreadContext.getLogContext().setTenantId("SIGNET");
    assertFalse(iTenantYmlConfigImpl.getTransfersEnabled());
  }

  @Test
  @DisplayName("Get Number of solutions with capacity enabled flag as true")
  void getNumberOfSolutionsWithCapacityFlagEnabledTest() {
    Integer response = iTenantYmlConfigImpl.getNumberOfSolutions(Boolean.TRUE);
    assertNotNull(response);
    assertEquals(1, response);
  }

  @Test
  @DisplayName("Get Number of solutions with capacity enabled flag as false")
  void getNumberOfSolutionsWithCapacityFlagDisabledTest() {
    Map<String, Object> valueForOrg = testUtil.getSourcingConfigValueForOrg();
    Map<String, Object> defaultValue = testUtil.getDefaultSourcingConfigValue();
    Map<String, Map<String, Object>> sourcingConfigMap =
        Map.of(TestUtil.ORG_ID, valueForOrg, "DEFAULT", defaultValue);
    Mockito.when(sourcingConfig.getSourcing()).thenReturn(sourcingConfigMap);
    Integer response = iTenantYmlConfigImpl.getNumberOfSolutions(Boolean.FALSE);
    assertNotNull(response);
    assertEquals(3, response);
  }

  @Test
  @DisplayName("Get transfer horizon days for NEXTUPLE_GR and DEFAULT")
  void getTransfersHorizonDays() {
    when(sourcingConfig.getSourcing())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                Map.of(TRANSFER_HORIZON_DAYS_CONFIG_KEY, 3),
                DEFAULT,
                Map.of(TRANSFER_HORIZON_DAYS_CONFIG_KEY, 2)));
    CurrentThreadContext.getLogContext().setTenantId(TestUtil.ORG_ID);
    assertEquals(3, iTenantYmlConfigImpl.getTransferScheduleHorizonDays());
    CurrentThreadContext.getLogContext().setTenantId("SIGNET");
    assertEquals(2, iTenantYmlConfigImpl.getTransferScheduleHorizonDays());
  }

  @Test
  @DisplayName("Get transfer past days for NEXTUPLE_GR and DEFAULT")
  void getTransfersPastDays() {
    when(sourcingConfig.getSourcing())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                Map.of(TRANSFER_PAST_DAYS_CONFIG_KEY, 3),
                DEFAULT,
                Map.of(TRANSFER_PAST_DAYS_CONFIG_KEY, 2)));
    CurrentThreadContext.getLogContext().setTenantId(TestUtil.ORG_ID);
    assertEquals(3, iTenantYmlConfigImpl.getTransferSchedulePastDays());
    CurrentThreadContext.getLogContext().setTenantId("SIGNET");
    assertEquals(2, iTenantYmlConfigImpl.getTransferSchedulePastDays());
  }

  @Test
  @DisplayName("Get Ship Charge Capping Logic Enabled Flag")
  void getShipChargeCappingLogicEnabledFlagTest() {
    when(sourcingConfig.getSourcing())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                Map.of("ship-charge-capping-logic-enabled", true),
                DEFAULT,
                Map.of("ship-charge-capping-logic-enabled", false)));
    Boolean resp = iTenantYmlConfigImpl.getShipChargeCappingLogicEnabledFlag();
    assertTrue(resp);
  }

  @Test
  @DisplayName("Get Ship Charge Constants and Cost Types Mapping")
  void getShipChargeConstantsAndCostTypesMappingTest() {
    String resp = iTenantYmlConfigImpl.getShipChargeConstantsAndCostTypesMapping();
    assertNull(resp);
  }

  @Test
  @DisplayName("Get Ship Charge Capping Constants")
  void getShipChargeCappingConstantsTest() {
    String resp = iTenantYmlConfigImpl.getShipChargeCappingConstants();
    assertNull(resp);
  }

  @Test
  @DisplayName("Get Attribute For Target Profit Margins")
  void getAttributeForTargetProfitMarginsTest() {
    String resp = iTenantYmlConfigImpl.getAttributeForTargetProfitMargins();
    assertNull(resp);
  }

  @Test
  @DisplayName("Get Target Profit Margins")
  void getTargetProfitMarginsTest() {
    String resp = iTenantYmlConfigImpl.getTargetProfitMargins("test");
    assertNull(resp);
  }

  @Test
  @DisplayName("Get Recommendation Engine Impl Class Test")
  void getRecommendationEngineImplClassTest() {
    String resp = iTenantYmlConfigImpl.getRecommendationEngineImplClass();
    assertNull(resp);
  }

  @Test
  @DisplayName("Get Service Option Hierarchy Test")
  void getServiceOptionHierarchyTest() {
    String resp = iTenantYmlConfigImpl.getServiceOptionHierarchy();
    assertNull(resp);
  }

  @Test
  @DisplayName("Get Recommendation Engine Enabled Flag")
  void getRecommendationEngineEnabledFlagTest() {
    when(sourcingConfig.getSourcing())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                Map.of("recommendation-enabled", true),
                DEFAULT,
                Map.of("recommendation-enabled", false)));
    Boolean resp = iTenantYmlConfigImpl.getRecommendationEngineEnabledFlag();
    assertTrue(resp);
  }

  @Test
  @DisplayName("Get Ship Together Enabled Flag")
  void getShipTogetherEnabledFlagTest() {
    when(sourcingConfig.getSourcing())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                Map.of("ship-together-enabled", true),
                DEFAULT,
                Map.of("ship-together-enabled", false)));
    Boolean resp = iTenantYmlConfigImpl.getShipTogetherEnabledFlag();
    assertTrue(resp);
  }

  @Test
  @DisplayName("Get Enable Future Availability")
  void getEnableFutureAvailabilityTest() {
    CurrentThreadContext.getLogContext().setTenantId(TestUtil.ORG_ID);
    when(sourcingConfig.getSourcing())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                Map.of(ENABLE_FUTURE_AVAILABILITY_CONFIG_KEY, true),
                DEFAULT,
                Map.of(ENABLE_FUTURE_AVAILABILITY_CONFIG_KEY, false)));
    assertTrue(iTenantYmlConfigImpl.getEnableFutureAvailability());
  }

  @Test
  @DisplayName("Get Enable Availability Sorting")
  void getEnableAvailabilitySortingTest() {
    CurrentThreadContext.getLogContext().setTenantId(TestUtil.ORG_ID);
    when(sourcingConfig.getSourcing())
        .thenReturn(
            Map.of(
                TestUtil.ORG_ID,
                Map.of(ENABLE_AVAILABILITY_SORTING_CONFIG_KEY, true),
                DEFAULT,
                Map.of(ENABLE_AVAILABILITY_SORTING_CONFIG_KEY, false)));
    assertTrue(iTenantYmlConfigImpl.getEnableAvailabilitySorting());
  }

  @Test
  @DisplayName("Test getTransitHorizonDays() returns configured value")
  void getTransitHorizonDaysTest() {
    // Set up a test value for transitHorizonDays
    Integer expectedTransitHorizon = 15;
    ReflectionTestUtils.setField(
        iTenantYmlConfigImpl, "transitHorizonDays", expectedTransitHorizon);

    // Call the method and verify the result
    Integer result = iTenantYmlConfigImpl.getTransitHorizonDays();

    // Assertions
    assertNotNull(result);
    assertEquals(expectedTransitHorizon, result);
  }

  @Test
  @DisplayName("Test getCarrierCalenderPastLookupDays() returns configured value")
  void getCarrierCalenderPastLookupDaysTest() {
    Integer expectedCarrierCalenderPastLookupDays = 15;
    ReflectionTestUtils.setField(
        iTenantYmlConfigImpl,
        "carrierCalenderPastLookupDays",
        expectedCarrierCalenderPastLookupDays);

    // Call the method and verify the result
    Integer result = iTenantYmlConfigImpl.getCarrierCalenderPastLookupDays();

    // Assertions
    assertNotNull(result);
    assertEquals(expectedCarrierCalenderPastLookupDays, result);
  }

  @Test
  @DisplayName("Test getNodeCalenderPastLookupDays() returns configured value")
  void getNodeCalenderPastLookupDaysTest() {
    Integer expectedNodeCalenderPastLookupDays = 15;
    ReflectionTestUtils.setField(
        iTenantYmlConfigImpl, "nodeCalenderPastLookupDays", expectedNodeCalenderPastLookupDays);

    // Call the method and verify the result
    Integer result = iTenantYmlConfigImpl.getNodeCalenderPastLookupDays();

    // Assertions
    assertNotNull(result);
    assertEquals(expectedNodeCalenderPastLookupDays, result);
  }
}
