package com.nextuple.pe.configs.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.exception.PromisingEngineException;
import com.nextuple.configuration.cache.domain.TenantConfigdataCacheValue;
import com.nextuple.configuration.cache.service.TenantConfigdataNearCacheService;
import com.nextuple.pe.util.TestUtil;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class TenantDBConfigImplTest {

  @InjectMocks TenantDBConfigImpl tenantDBConfigImpl;

  @Mock TenantConfigdataNearCacheService tenantConfigdataNearCacheService;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    CurrentThreadContext.getLogContext().setTenantId(TestUtil.ORG_ID);
    ReflectionTestUtils.setField(
        tenantDBConfigImpl,
        "defaultProcessingTimeComputation",
        TestUtil.DEFAULT_PROCESSING_TIME_COMPUTATION);
    ReflectionTestUtils.setField(
        tenantDBConfigImpl, "defaultNodeWorkingHours", TestUtil.DEFAULT_NODE_WORKING_HOURS);
    ReflectionTestUtils.setField(
        tenantDBConfigImpl, "defaultItemBufferEnabled", TestUtil.DEFAULT_ITEM_BUFFER_ENABLED);
    ReflectionTestUtils.setField(
        tenantDBConfigImpl,
        "defaultPromisingIntermediateEventEnabled",
        TestUtil.DEFAULT_PROMISE_INTERMEDIATE_EVENT_ENABLED_FLAG);
    ReflectionTestUtils.setField(
        tenantDBConfigImpl, "defaultCapacityFlag", TestUtil.DEFAULT_CAPACITY_ENABLED_FLAG);
    ReflectionTestUtils.setField(
        tenantDBConfigImpl, "defaultTransfersEnabled", TestUtil.DEFAULT_TRANSFER_ENABLED);
  }

  @Test
  void getOrgIdTest() {
    String orgId = tenantDBConfigImpl.getOrgId();
    Assertions.assertEquals(TestUtil.ORG_ID, orgId);
  }

  @Test
  void getServiceOptionsTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForServiceOptions();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);

    String response = tenantDBConfigImpl.getServiceOptions();
    assertNotNull(response);
    Assertions.assertEquals(cacheValue.getConfigValue(), response);
  }

  @Test
  void getServiceOptionsExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(PromisingEngineException.class, () -> tenantDBConfigImpl.getServiceOptions());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey service-options",
        ex.getMessage());
  }

  @Test
  void getServiceOptionsListTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForServiceOptions();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    Set<String> response = tenantDBConfigImpl.getServiceOptionsList();
    assertNotNull(response);
    assertEquals(3, response.size());
  }

  @Test
  void getServiceOptionsListExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(
            PromisingEngineException.class, () -> tenantDBConfigImpl.getServiceOptionsList());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey service-options",
        ex.getMessage());
  }

  @Test
  void getServiceOptionInventoryTypeMappingTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForServiceOptionsInventoryTypeMapping();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    String response = tenantDBConfigImpl.getServiceOptionInventoryTypeMapping();
    assertNotNull(response);
    Assertions.assertEquals(cacheValue.getConfigValue(), response);
  }

  @Test
  void getServiceOptionInventoryTypeMappingExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(
            PromisingEngineException.class,
            () -> tenantDBConfigImpl.getServiceOptionInventoryTypeMapping());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey service-option-inventory-type-mapping",
        ex.getMessage());
  }

  @Test
  void getPublishEddResponseOnPageTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForPublishEddResponse();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    String response = tenantDBConfigImpl.getPublishEddResponseOnPage();
    assertNotNull(response);
    Assertions.assertEquals(cacheValue.getConfigValue(), response);
  }

  @Test
  void getPublishEddResponseOnPageExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(
            PromisingEngineException.class, () -> tenantDBConfigImpl.getPublishEddResponseOnPage());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey publish-edd-response-on-page",
        ex.getMessage());
  }

  @Test
  void getPublishEddResponseOnPageListTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForPublishEddResponse();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    Set<String> response = tenantDBConfigImpl.getPublishEddResponseOnPageList();
    assertNotNull(response);
    assertEquals(1, response.size());
  }

  @Test
  void getPublishEddResponseOnPageListExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(
            PromisingEngineException.class,
            () -> tenantDBConfigImpl.getPublishEddResponseOnPageList());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey publish-edd-response-on-page",
        ex.getMessage());
  }

  @Test
  void getDefaultCarrierPriorityTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForDefaultCarrierPriority();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    String response = tenantDBConfigImpl.getDefaultCarrierPriority();
    assertNotNull(response);
    Assertions.assertEquals(cacheValue.getConfigValue(), response);
  }

  @Test
  void getDefaultCarrierPriorityExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(
            PromisingEngineException.class, () -> tenantDBConfigImpl.getDefaultCarrierPriority());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey default-carrier-priority-value",
        ex.getMessage());
  }

  @Test
  void getLogSuppressionServiceOptionsTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForLogSuppression();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    String response = tenantDBConfigImpl.getLogSuppressionServiceOptions();
    assertNotNull(response);
    Assertions.assertEquals(cacheValue.getConfigValue(), response);
  }

  @Test
  void getLogSuppressionServiceOptionsExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(
            PromisingEngineException.class,
            () -> tenantDBConfigImpl.getLogSuppressionServiceOptions());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey log-suppression-service-options",
        ex.getMessage());
  }

  @Test
  void getLogSuppressionServiceOptionsListTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForLogSuppression();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    Set<String> response = tenantDBConfigImpl.getLogSuppressionServiceOptionsList();
    assertNotNull(response);
    assertEquals(2, response.size());
  }

  @Test
  void getLogSuppressionServiceOptionsListExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(
            PromisingEngineException.class,
            () -> tenantDBConfigImpl.getLogSuppressionServiceOptionsList());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey log-suppression-service-options",
        ex.getMessage());
  }

  @Test
  void getNumberOfSolutionsTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForNoOfSolution();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    Integer response = tenantDBConfigImpl.getNumberOfSolutions();
    assertNotNull(response);
    assertEquals(Integer.valueOf(cacheValue.getConfigValue()), response);
  }

  @Test
  void getNumberOfSolutionsExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(
            PromisingEngineException.class, () -> tenantDBConfigImpl.getNumberOfSolutions());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey sourcing-no-of-solution",
        ex.getMessage());
  }

  @Test
  void getNumberOfNodesTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForNoOfNodes();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    Integer response = tenantDBConfigImpl.getNumberOfNodes();
    assertNotNull(response);
    assertEquals(Integer.valueOf(cacheValue.getConfigValue()), response);
  }

  @Test
  void getNumberOfNodesExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(PromisingEngineException.class, () -> tenantDBConfigImpl.getNumberOfNodes());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey sourcing-no-of-nodes",
        ex.getMessage());
  }

  @Test
  void getAllowedPagesListForPublishingEventTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueGetAllowedPages();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    Set<String> response = tenantDBConfigImpl.getAllowedPagesListForPublishingEvent();
    assertNotNull(response);
    assertEquals(1, response.size());
  }

  @Test
  void getAllowedPagesListForPublishingEventExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(
            PromisingEngineException.class,
            () -> tenantDBConfigImpl.getAllowedPagesListForPublishingEvent());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey event-allowed-pages",
        ex.getMessage());
  }

  @Test
  void getPublishEnabledMapTest() {
    var cacheValue1 = testUtil.getTenantConfigCacheValueGetPublishEnabled();
    var cacheValue2 = testUtil.getTenantConfigCacheValueGetPublishEnabled();
    cacheValue2.setConfigValue("{}");
    when(tenantConfigdataNearCacheService.get(any()))
        .thenReturn(cacheValue1)
        .thenReturn(cacheValue2);
    Map<String, Boolean> response = tenantDBConfigImpl.getPublishEnabledMap();
    assertNotNull(response);
    assertTrue(response.containsKey("ItemDetailsEvent"));

    response = tenantDBConfigImpl.getPublishEnabledMap();
    assertNotNull(response);
    assertFalse(response.containsKey("ItemDetailsEvent"));
  }

  @Test
  void getPublishEnabledMapExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(
            PromisingEngineException.class, () -> tenantDBConfigImpl.getPublishEnabledMap());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey event-publish-enabled",
        ex.getMessage());
  }

  @Test
  void getLogLevelMapTest() {
    var cacheValue1 = testUtil.getTenantConfigCacheValueGetLogLevel();
    var cacheValue2 = testUtil.getTenantConfigCacheValueGetLogLevel();
    cacheValue2.setConfigValue("{ExceptionEvent: DEBUG}");
    when(tenantConfigdataNearCacheService.get(any()))
        .thenReturn(cacheValue1)
        .thenReturn(cacheValue2);
    Map<String, String> response = tenantDBConfigImpl.getLogLevelMap();
    assertNotNull(response);
    assertTrue(response.containsKey("ExceptionEvent"));

    response = tenantDBConfigImpl.getLogLevelMap();
    assertNotNull(response);
    assertEquals("DEBUG", response.get("ExceptionEvent"));
  }

  @Test
  void getLogLevelMapExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(PromisingEngineException.class, () -> tenantDBConfigImpl.getLogLevelMap());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey event-log-level",
        ex.getMessage());
  }

  @Test
  void getConsoleLogListenEnabledMapTest() {
    var cacheValue1 = testUtil.getTenantConfigCacheValueConsoleLogListenEnabledMap();
    var cacheValue2 = testUtil.getTenantConfigCacheValueConsoleLogListenEnabledMap();
    cacheValue2.setConfigValue("{ExceptionEvent: true}");
    when(tenantConfigdataNearCacheService.get(any()))
        .thenReturn(cacheValue1)
        .thenReturn(cacheValue2);
    Map<String, Boolean> response = tenantDBConfigImpl.getConsoleLogListenEnabledMap();
    assertNotNull(response);

    response = tenantDBConfigImpl.getConsoleLogListenEnabledMap();
    assertTrue(response.containsKey("ExceptionEvent"));
  }

  @Test
  void getConsoleLogListenEnabledMapExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(
            PromisingEngineException.class,
            () -> tenantDBConfigImpl.getConsoleLogListenEnabledMap());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey event-console-log-listen",
        ex.getMessage());
  }

  @Test
  void getSortByTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueGetSortBy();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    String response = tenantDBConfigImpl.getSortBy();
    assertNotNull(response);
    Assertions.assertEquals(cacheValue.getConfigValue(), response);
  }

  @Test
  void getSortByExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(PromisingEngineException.class, () -> tenantDBConfigImpl.getSortBy());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey event-sort-by",
        ex.getMessage());
  }

  @Test
  @DisplayName(
      "Fetching default processing time computation mode when tenant config property is successfully fetched")
  void getProcessingTimeComputationTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForProcessingTimeComputation();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    String response = tenantDBConfigImpl.getProcessingTimeComputation();
    assertNotNull(response);
    assertEquals("ADDITION", response);

    verify(tenantConfigdataNearCacheService, times(1)).get(any());
  }

  @Test
  @DisplayName(
      "Fetching default processing time computation mode when there is no tenant config property assigned")
  void getDefaultProcessingTimeComputationTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    String response = tenantDBConfigImpl.getProcessingTimeComputation();
    assertNotNull(response);
    Assertions.assertEquals(TestUtil.DEFAULT_PROCESSING_TIME_COMPUTATION, response);

    verify(tenantConfigdataNearCacheService, times(1)).get(any());
  }

  @Test
  @DisplayName("Fetch node working hours from tenant configuration")
  void getNodeWorkingHoursTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForNodeWorkingHours();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    String response = tenantDBConfigImpl.getNodeWorkingHours();
    assertNotNull(response);
    assertEquals("08:00-19:00", response);

    verify(tenantConfigdataNearCacheService, times(1)).get(any());
  }

  @Test
  @DisplayName("Fetch default node working hours")
  void getDefaultNodeWorkingHoursTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    String response = tenantDBConfigImpl.getNodeWorkingHours();
    assertNotNull(response);
    Assertions.assertEquals(TestUtil.DEFAULT_NODE_WORKING_HOURS, response);

    verify(tenantConfigdataNearCacheService, times(1)).get(any());
  }

  @Test
  @DisplayName("Fetch default processing time ml override class from tenant configuration")
  void getProcessingTimeMLOverrideClassTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForMLProcessingTimeOverride();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    String response = tenantDBConfigImpl.getNodeWorkingHours();

    assertNotNull(response);
    assertEquals("com.nextuple.promise.sourcing.impl.DefaultMLOverridingImpl", response);
    verify(tenantConfigdataNearCacheService, times(1)).get(any());
  }

  @Test
  @DisplayName("Fetch default processing time ml override class - exception scenario")
  void getProcessingTimeMLOverrideClassExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(
            PromisingEngineException.class,
            () -> tenantDBConfigImpl.getProcessingTimeMLOverrideClass());

    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey processing-time-ml-override-class",
        ex.getMessage());
  }

  @Test
  @DisplayName("Fetch is-ml-processing-time-cutoff-applied - happy path")
  void getIsMlProcessingTimeCutoffAppliedTest() {
    when(tenantConfigdataNearCacheService.get(any()))
        .thenReturn(
            TenantConfigdataCacheValue.builder()
                .configKey("is-ml-processing-time-cutoff-applied")
                .configValue("true")
                .build());
    boolean result = tenantDBConfigImpl.getIsCutoffApplied();
    Assertions.assertTrue(result);
    when(tenantConfigdataNearCacheService.get(any()))
        .thenReturn(
            TenantConfigdataCacheValue.builder()
                .configKey("is-ml-processing-time-cutoff-applied")
                .configValue("false")
                .build());
    result = tenantDBConfigImpl.getIsCutoffApplied();
    Assertions.assertFalse(result);
  }

  @Test
  @DisplayName("Fetch is-ml-processing-time-cutoff-applied - default case")
  void getIsMlProcessingTimeCutoffAppliedDefaultTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    boolean result = tenantDBConfigImpl.getIsCutoffApplied();
    Assertions.assertTrue(result);
  }

  @Test
  @DisplayName("Fetch item buffer enabled from tenant configuration")
  void getItemBufferEnabledTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForItemBufferEnabled();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    Boolean response = tenantDBConfigImpl.getItemBufferEnabled();
    assertNotNull(response);
    assertEquals(true, response);

    verify(tenantConfigdataNearCacheService, times(1)).get(any());
  }

  @Test
  @DisplayName("Fetch default item buffer enabled")
  void getDefaultItemBufferEnabledTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    Boolean response = tenantDBConfigImpl.getItemBufferEnabled();
    assertNotNull(response);
    assertEquals(Boolean.valueOf(TestUtil.DEFAULT_ITEM_BUFFER_ENABLED), response);

    verify(tenantConfigdataNearCacheService, times(1)).get(any());
  }

  @Test
  @DisplayName("Get promising intermediate event enabled flag for tenant")
  void getPromisingIntermediateEventEnabledFlag() {
    when(tenantConfigdataNearCacheService.get(any()))
        .thenReturn(
            TenantConfigdataCacheValue.builder()
                .configKey("promising-intermediate-event-enabled")
                .configValue("false")
                .build());
    assertFalse(tenantDBConfigImpl.getPromisingIntermediateEventsEnabled());
  }

  @Test
  @DisplayName("Get default promising intermediate event enabled flag")
  void getPromisingIntermediateEventEnabledFlagException() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    assertTrue(tenantDBConfigImpl.getPromisingIntermediateEventsEnabled());
    verify(tenantConfigdataNearCacheService, times(1)).get(any());
  }

  @Test
  @DisplayName("Get Capacity enabled flag for tenant")
  void getCapacityEnabledFlag() {
    when(tenantConfigdataNearCacheService.get(any()))
        .thenReturn(
            TenantConfigdataCacheValue.builder()
                .configKey("capacity-enabled")
                .configValue("true")
                .build());
    assertTrue(tenantDBConfigImpl.getCapacityEnabledFlag());
  }

  @Test
  @DisplayName("Get default capacity enabled flag")
  void getCapacityEnabledFlagException() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    assertFalse(tenantDBConfigImpl.getCapacityEnabledFlag());
    verify(tenantConfigdataNearCacheService, times(1)).get(any());
  }

  @Test
  @DisplayName("Get transfers enabled flag for tenant")
  void getTransfersEnabledFlag() {
    when(tenantConfigdataNearCacheService.get(any()))
        .thenReturn(
            TenantConfigdataCacheValue.builder()
                .configKey("transfers-enabled")
                .configValue("true")
                .build());
    assertTrue(tenantDBConfigImpl.getCapacityEnabledFlag());
  }

  @Test
  @DisplayName("Get Capacity horizon days for tenant")
  void getCapacityHorizon() {
    when(tenantConfigdataNearCacheService.get(any()))
        .thenReturn(
            TenantConfigdataCacheValue.builder()
                .configKey("capacity-horizon")
                .configValue("7")
                .build());
    assertEquals(7, tenantDBConfigImpl.getCapacityHorizon());
  }

  @Test
  @DisplayName("Get Capacity horizon for tenant: Exception")
  void getCapacityHorizonException() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(PromisingEngineException.class, () -> tenantDBConfigImpl.getCapacityHorizon());

    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey capacity-horizon",
        ex.getMessage());
  }

  @Test
  @DisplayName("Get default transfers enabled flag")
  void getTransfersEnabledFlagException() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    assertFalse(tenantDBConfigImpl.getTransfersEnabled());
    verify(tenantConfigdataNearCacheService, times(1)).get(any());
  }

  @Test
  @DisplayName("Get Number of solutions with capacity enabled flag as true")
  void getNumberOfSolutionsWithCapacityFlagEnabledTest() {
    Integer response = tenantDBConfigImpl.getNumberOfSolutions(Boolean.TRUE);
    assertNotNull(response);
    assertEquals(1, response);
  }

  @Test
  @DisplayName("Get Number of solutions with capacity enabled flag as false")
  void getNumberOfSolutionsWithCapacityFlagDisabledTest() {
    TenantConfigdataCacheValue cacheValue = testUtil.getTenantConfigCacheValueForNoOfSolution();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    Integer response = tenantDBConfigImpl.getNumberOfSolutions(Boolean.FALSE);
    assertNotNull(response);
    assertEquals(3, response);
  }

  @Test
  @DisplayName("Fetch ship charge capping logic flag from tenant configuration db")
  void getShipChargeCappingLogicEnabledFlagTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForShipChargeCappingLogic();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    Boolean response = tenantDBConfigImpl.getShipChargeCappingLogicEnabledFlag();
    assertNotNull(response);
    assertEquals(Boolean.valueOf(cacheValue.getConfigValue()), response);
  }

  @Test
  @DisplayName("When ship charge capping logic flag from tenant configuration db is not found")
  void getShipChargeCappingLogicEnabledFlagExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    PromisingEngineException ex =
        assertThrows(
            PromisingEngineException.class,
            () -> tenantDBConfigImpl.getShipChargeCappingLogicEnabledFlag());
    assertNotNull(ex);
    assertEquals(
        "Tenant Configuration not found for given orgId and configKey ship-charge-capping-logic-enabled",
        ex.getMessage());
  }

  @Test
  @DisplayName("Fetch ship charge capping constants from tenant configuration db")
  void getShipChargeCappingConstantsTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForShipChargeCappingConstants();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    String response = tenantDBConfigImpl.getShipChargeCappingConstants();
    assertNotNull(response);
    assertEquals(cacheValue.getConfigValue(), response);
  }

  @Test
  @DisplayName("Fetch ship charge capping constants from tenant configuration db with null value")
  void getShipChargeCappingConstantsNullTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForShipChargeCappingConstants();
    cacheValue.setConfigValue(null);
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    String response = tenantDBConfigImpl.getShipChargeCappingConstants();
    assertNull(response);
  }

  @Test
  @DisplayName("When ship charge capping constants from tenant configuration db is not found")
  void getShipChargeCappingConstantsExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    String response = tenantDBConfigImpl.getShipChargeCappingConstants();
    assertNull(response);
  }

  @Test
  @DisplayName("Fetch ship charge and cost types mapping from tenant configuration db")
  void getShipChargeCappingConstantsAndCostTypesMappingTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForShipChargeCappingConstants();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    String response = tenantDBConfigImpl.getShipChargeConstantsAndCostTypesMapping();
    assertNotNull(response);
    assertEquals(cacheValue.getConfigValue(), response);
  }

  @Test
  @DisplayName("When ship charge and cost types mapping from tenant configuration db is not found")
  void getShipChargeCappingConstantsAndCostTypesMappingExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    String response = tenantDBConfigImpl.getShipChargeConstantsAndCostTypesMapping();
    assertNull(response);
  }

  @Test
  void getAttributeForTargetProfitMarginsTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForSelectedAttributeForTargetProfitMargins();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    String response = tenantDBConfigImpl.getAttributeForTargetProfitMargins();
    assertNotNull(response);
    assertEquals(cacheValue.getConfigValue(), response);
  }

  @Test
  void getAttributeForTargetProfitMarginsExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    String response = tenantDBConfigImpl.getAttributeForTargetProfitMargins();
    assertNull(response);
  }

  @Test
  @DisplayName("Fetch service option hierarchy from tenant configuration db")
  void getServiceOptionHierarchyTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForServiceOptionHierarchy();
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    String response = tenantDBConfigImpl.getServiceOptionHierarchy();
    assertNotNull(response);
    assertEquals(cacheValue.getConfigValue(), response);
  }

  @Test
  @DisplayName("When service option hierarchy from tenant configuration db is not found")
  void getServiceOptionHierarchyExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    String response = tenantDBConfigImpl.getServiceOptionHierarchy();
    assertNull(response);
  }

  @Test
  @DisplayName("Get transfers horizon days for tenant")
  void getTransfersHorizonDays() {
    when(tenantConfigdataNearCacheService.get(any()))
        .thenReturn(
            TenantConfigdataCacheValue.builder()
                .configKey("transfer-schedule-horizon-days")
                .configValue("10")
                .build());
    assertEquals(10, tenantDBConfigImpl.getTransferScheduleHorizonDays());
  }

  @Test
  @DisplayName("Get transfers past days for tenant")
  void getTransfersPastDays() {
    when(tenantConfigdataNearCacheService.get(any()))
        .thenReturn(
            TenantConfigdataCacheValue.builder()
                .configKey("transfer-schedule-past-days")
                .configValue("10")
                .build());
    assertEquals(10, tenantDBConfigImpl.getTransferSchedulePastDays());
  }

  @Test
  void getTargetProfitMarginsTest() {
    var cacheValue = testUtil.getTenantConfigCacheValueForTargetProfitMargins("itemCategory");
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(cacheValue);
    String response = tenantDBConfigImpl.getTargetProfitMargins("itemCategory");
    assertNotNull(response);
    assertEquals(cacheValue.getConfigValue(), response);
  }

  @Test
  void getTargetProfitMarginsExceptionTest() {
    when(tenantConfigdataNearCacheService.get(any())).thenReturn(null);
    String response = tenantDBConfigImpl.getTargetProfitMargins("itemCategory");
    assertNull(response);
  }

  @Test
  @DisplayName("Get Recommendation Engine Enabled Flag")
  void getRecommendationEngineEnabledFlagTest() {
    when(tenantConfigdataNearCacheService.get(any()))
        .thenReturn(
            TenantConfigdataCacheValue.builder()
                .configKey("recommendation-engine-enabled")
                .configValue("true")
                .build());
    assertTrue(tenantDBConfigImpl.getRecommendationEngineEnabledFlag());
  }

  @Test
  @DisplayName("Get Line Threshold Test")
  void getLineThresholdTest() {
    when(tenantConfigdataNearCacheService.get(any()))
        .thenReturn(
            TenantConfigdataCacheValue.builder()
                .configKey("line-threshold")
                .configValue("10")
                .build());
    assertEquals(10, tenantDBConfigImpl.getLineThreshold());
  }
}
