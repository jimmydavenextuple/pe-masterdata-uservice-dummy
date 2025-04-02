/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.enums.ActionEnum;
import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.promise.sourcing.rule.api.domain.enums.RulesConfigurationModuleNameEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.RulesConfigurationResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.RuleConfigurationParam;
import com.nextuple.promise.sourcing.rule.service.RulesConfigurationService;
import com.nextuple.promise.sourcing.rule.service.SourcingAttributesDefinitionService;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.inbound.*;
import com.nextuple.transit.domain.outbound.TransferScheduleBatchResponse;
import com.nextuple.transit.domain.outbound.TransferScheduleConsumerResult;
import com.nextuple.transit.domain.outbound.TransferScheduleRangeResponse;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainDto;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainRequest;
import com.nextuple.transit.persistence.service.TransferSchedulePersistenceService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class TransferScheduleServiceImplTest {
  @InjectMocks TransferScheduleServiceImpl transferScheduleService;
  @InjectMocks TestUtil testUtil;

  @Mock NodeFeign nodeFeign;
  @Mock TransferSchedulePersistenceService transferSchedulePersistenceService;
  @Mock RulesConfigurationService ruleConfigurationService;
  @Mock PageParams pageParams;
  @Mock SourcingAttributesDefinitionService sourcingAttributesDefinitionService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("create transfer schedules")
  void createTransferScheduleTest() throws PromiseEngineException, CommonServiceException {
    when(nodeFeign.getNodeDetails(any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getNodeDetail("Node1")).success(true).build());
    when(transferSchedulePersistenceService.saveTransferSchedule(any()))
        .thenReturn(testUtil.getTransferScheduleEntity());
    TransferScheduleResponse response =
        transferScheduleService.createTransferSchedule(
            testUtil.getTransferScheduleCreationRequest());
    assertEquals("Node1", response.getSourceNodeId());
    verify(nodeFeign, times(2)).getNodeDetails(any(), any());
    verify(transferSchedulePersistenceService, times(1)).saveTransferSchedule(any());
  }

  @Test
  @DisplayName("create transfer schedules with rules")
  void createTransferScheduleWithRulesTest() throws PromiseEngineException, CommonServiceException {
    when(nodeFeign.getNodeDetails(any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getNodeDetail("Node1")).success(true).build());
    when(transferSchedulePersistenceService.saveTransferSchedule(any()))
        .thenReturn(testUtil.getTransferScheduleEntity());
    when(ruleConfigurationService.fetchRuleByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(any()))
        .thenReturn(Optional.of(testUtil.getRuleConfiguration()));
    TransferScheduleCreationRequest request = testUtil.getTransferScheduleCreationRequest();
    request.setRule("DC:KITCHEN");
    request.setRuleName("KitchenRule");
    TransferScheduleResponse response = transferScheduleService.createTransferSchedule(request);
    assertEquals("Node1", response.getSourceNodeId());
    verify(nodeFeign, times(2)).getNodeDetails(any(), any());
    verify(transferSchedulePersistenceService, times(1)).saveTransferSchedule(any());
  }

  @Test
  @DisplayName("create transfer schedules invalid rule")
  void createTransferScheduleInvalidRuleTest() throws PromiseEngineException {
    when(nodeFeign.getNodeDetails(any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getNodeDetail("Node1")).success(true).build());
    when(ruleConfigurationService.fetchRuleByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(any()))
        .thenReturn(Optional.empty());
    when(transferSchedulePersistenceService.saveTransferSchedule(any()))
        .thenReturn(testUtil.getTransferScheduleEntity());
    TransferScheduleCreationRequest request = testUtil.getTransferScheduleCreationRequest();
    request.setRule("DC:KITCHEN");
    request.setRuleName("KitchenRule");
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> transferScheduleService.createTransferSchedule(request));
    verify(nodeFeign, times(2)).getNodeDetails(any(), any());
    verify(ruleConfigurationService, times(1))
        .fetchRuleByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(any());
    verify(transferSchedulePersistenceService, times(0)).saveTransferSchedule(any());
  }

  @Test
  @DisplayName("create transfer schedules - start time after end time")
  void createTransferScheduleInvalidTimeTest()
      throws PromiseEngineException, CommonServiceException {
    when(nodeFeign.getNodeDetails(any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getNodeDetail("Node1")).success(true).build());
    when(transferSchedulePersistenceService.saveTransferSchedule(any()))
        .thenReturn(testUtil.getTransferScheduleEntity());
    TransferScheduleCreationRequest transferScheduleCreationRequest =
        testUtil.getTransferScheduleCreationRequest();
    DateTime dateTime = new DateTime();
    transferScheduleCreationRequest.setStartTime(dateTime.plusDays(1));
    transferScheduleCreationRequest.setEndTime(dateTime);

    Assertions.assertThrows(
        CommonServiceException.class,
        () -> transferScheduleService.createTransferSchedule(transferScheduleCreationRequest));
    verify(nodeFeign, times(0)).getNodeDetails(any(), any());
    verify(transferSchedulePersistenceService, times(0)).saveTransferSchedule(any());
  }

  @Test
  @DisplayName("create transfer schedules - invalid node - feign success false")
  void createTransferScheduleInvalidNodeTest() throws PromiseEngineException {
    when(nodeFeign.getNodeDetails(any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getNodeDetail("Node1")).success(true).build())
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getNodeDetail("Node1")).success(false).build());
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            transferScheduleService.createTransferSchedule(
                testUtil.getTransferScheduleCreationRequest()));
    verify(nodeFeign, times(2)).getNodeDetails(any(), any());
    verify(transferSchedulePersistenceService, times(0)).saveTransferSchedule(any());
  }

  @Test
  @DisplayName("create transfer schedules - invalid node - feign response empty")
  void createTransferScheduleEmptyFeignResponseTest() throws PromiseEngineException {
    when(nodeFeign.getNodeDetails(any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getNodeDetail("Node1")).success(true).build())
        .thenReturn(BaseResponse.builder().success(true).build());
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            transferScheduleService.createTransferSchedule(
                testUtil.getTransferScheduleCreationRequest()));
    verify(nodeFeign, times(2)).getNodeDetails(any(), any());
    verify(transferSchedulePersistenceService, times(0)).saveTransferSchedule(any());
  }

  @Test
  @DisplayName("Fetch transfer schedules")
  void fetchTransferScheduleTest() {
    when(transferSchedulePersistenceService.fetchUpcomingTransferSchedules(any(), any()))
        .thenReturn(List.of(testUtil.getTransferScheduleEntity()));
    List<TransferScheduleResponse> response =
        transferScheduleService.fetchTransferSchedules(TestUtil.ORG_ID, TestUtil.DROPOFF_NODE);
    assertEquals("Node1", response.get(0).getSourceNodeId());
    verify(transferSchedulePersistenceService, times(1))
        .fetchUpcomingTransferSchedules(any(), any());
  }

  @Test
  @DisplayName("Delete transfer schedules - happy path")
  void deleteTransferScheduleTest() throws PromiseEngineException, CommonServiceException {
    when(transferSchedulePersistenceService.deleteTransferSchedule(any(), any(), any(), any()))
        .thenReturn(testUtil.getTransferScheduleEntity());
    TransferScheduleResponse response =
        transferScheduleService.deleteTransferSchedule(testUtil.getTransferScheduleRequest());
    assertEquals("Node1", response.getSourceNodeId());
    verify(transferSchedulePersistenceService, times(1))
        .deleteTransferSchedule(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Fetch transfer schedules list - paginated")
  void fetchTransferScheduleListPaginatedTest()
      throws PromiseEngineException, CommonServiceException {
    var fetchTransferScheduleRequest = testUtil.getFetchTransferScheduleRequest();
    when(pageParams.getSortBy()).thenReturn(java.util.Optional.of("sourceNodeId"));
    when(pageParams.getSortOrder()).thenReturn(java.util.Optional.of("ASC"));
    when(pageParams.getPageNo()).thenReturn(java.util.Optional.of(1));
    when(pageParams.getPageSize()).thenReturn(java.util.Optional.of(10));

    Page<TransferScheduleResponse> mockPage =
        new PageImpl<>(List.of(testUtil.getTransferScheduleResponse()));

    when(transferSchedulePersistenceService.fetchTransferSchedulesList(any(), any(), any()))
        .thenReturn(mockPage);

    Page<TransferScheduleResponse> response =
        transferScheduleService.fetchTransferScheduleList(
            TestUtil.ORG_ID, true, pageParams, fetchTransferScheduleRequest);

    assertEquals(1, response.getTotalElements());
    verify(transferSchedulePersistenceService, times(1))
        .fetchTransferSchedulesList(any(), any(), any());
  }

  @Test
  @DisplayName("Fetch transfer schedules list - non-paginated")
  void fetchTransferScheduleListNonPaginatedTest()
      throws PromiseEngineException, CommonServiceException {
    var fetchTransferScheduleRequest = testUtil.getFetchTransferScheduleRequest();
    when(pageParams.getSortBy()).thenReturn(java.util.Optional.of("sourceNodeId"));
    when(pageParams.getSortOrder()).thenReturn(java.util.Optional.of("ASC"));
    when(pageParams.getPageNo()).thenReturn(java.util.Optional.of(1));

    Page<TransferScheduleResponse> mockPage =
        new PageImpl<>(List.of(testUtil.getTransferScheduleResponse()));

    when(transferSchedulePersistenceService.fetchTransferSchedulesList(any(), any(), any()))
        .thenReturn(mockPage);

    Page<TransferScheduleResponse> response =
        transferScheduleService.fetchTransferScheduleList(
            TestUtil.ORG_ID, false, pageParams, fetchTransferScheduleRequest);

    assertEquals(1, response.getTotalElements());
    verify(transferSchedulePersistenceService, times(1))
        .fetchTransferSchedulesList(any(), any(), any());
  }

  @Test
  @DisplayName("Fetch transfer schedules list - invalid sort field")
  void fetchTransferScheduleListInvalidSortByTest() {
    var fetchTransferScheduleRequest = testUtil.getFetchTransferScheduleRequest();
    when(pageParams.getSortBy()).thenReturn(java.util.Optional.of("invalidField"));
    when(pageParams.getSortOrder()).thenReturn(java.util.Optional.of("ASC"));
    when(pageParams.getPageNo()).thenReturn(java.util.Optional.of(1));

    Assertions.assertThrows(
        CommonServiceException.class,
        () -> {
          transferScheduleService.fetchTransferScheduleList(
              TestUtil.ORG_ID, true, pageParams, fetchTransferScheduleRequest);
        });
  }

  @Test
  @DisplayName("Fetch transfer schedules list - invalid sort order")
  void fetchTransferScheduleListInvalidSortOrderTest() {
    var fetchTransferScheduleRequest = testUtil.getFetchTransferScheduleRequest();
    when(pageParams.getSortBy()).thenReturn(java.util.Optional.of("sourceNodeId"));
    when(pageParams.getSortOrder()).thenReturn(java.util.Optional.of("INVALID"));
    when(pageParams.getPageNo()).thenReturn(java.util.Optional.of(1));

    Assertions.assertThrows(
        CommonServiceException.class,
        () -> {
          transferScheduleService.fetchTransferScheduleList(
              TestUtil.ORG_ID, true, pageParams, fetchTransferScheduleRequest);
        });
  }

  @Test
  @DisplayName("Fetch transfer schedules list - exception from persistence service")
  void fetchTransferScheduleListPersistenceServiceExceptionTest() throws PromiseEngineException {
    var fetchTransferScheduleRequest = testUtil.getFetchTransferScheduleRequest();
    when(pageParams.getSortBy()).thenReturn(java.util.Optional.of("sourceNodeId"));
    when(pageParams.getSortOrder()).thenReturn(java.util.Optional.of("ASC"));
    when(pageParams.getPageNo()).thenReturn(java.util.Optional.of(1));
    when(pageParams.getPageSize()).thenReturn(java.util.Optional.of(10));

    doThrow(
            new PromiseEngineException(
                ApplicationLayer.DAO_LAYER,
                ExceptionCodeMapping.DAO_SAVE_FAILED,
                "Persistence service exception"))
        .when(transferSchedulePersistenceService)
        .fetchTransferSchedulesList(any(), any(), any());

    Assertions.assertThrows(
        PromiseEngineException.class,
        () -> {
          transferScheduleService.fetchTransferScheduleList(
              TestUtil.ORG_ID, true, pageParams, fetchTransferScheduleRequest);
        });
  }

  @Test
  @DisplayName("Fetch schedules in range with both start time and end time")
  void testFetchTransferSchedulesInRange() {
    // Arrange
    TransferScheduleRangeRequest request = new TransferScheduleRangeRequest();
    request.setStartTime(DateTime.now());
    request.setEndTime(DateTime.now().plusDays(5));
    request.setHorizonDays(2);
    request.setPastDays(1);

    List<TransferScheduleResponse> expectedResponse =
        List.of(testUtil.getTransferScheduleResponse());

    when(transferSchedulePersistenceService.fetchTransferSchedulesInRange(any()))
        .thenReturn(List.of(testUtil.getTransferScheduleEntity()));

    // Act
    List<TransferScheduleRangeResponse> actualResponse =
        transferScheduleService.fetchTransferSchedulesInRange(request);

    // Assert
    assertNotNull(actualResponse);
    assertEquals(
        expectedResponse.get(0).getSourceNodeId(), actualResponse.get(0).getSourceNodeId());
    assertEquals(
        expectedResponse.get(0).getDropoffNodeId(), actualResponse.get(0).getDropoffNodeId());

    verify(transferSchedulePersistenceService, times(1))
        .fetchTransferSchedulesInRange(any(TransferScheduleDomainRequest.class));

    request.setHorizonDays(null);
    actualResponse = transferScheduleService.fetchTransferSchedulesInRange(request);
    assertNotNull(actualResponse);
    assertEquals(
        expectedResponse.get(0).getSourceNodeId(), actualResponse.get(0).getSourceNodeId());
    assertEquals(
        expectedResponse.get(0).getDropoffNodeId(), actualResponse.get(0).getDropoffNodeId());
  }

  @Test
  @DisplayName(
      "Return empty list when rule and rule name is null and there is an active transfer rule definition")
  void testFetchTransfersWithNullRuleAndRuleNameAndActiveRuleDefinition()
      throws PromiseEngineException, CommonServiceException {
    // Arrange
    TransferScheduleRangeRequest request = new TransferScheduleRangeRequest();
    request.setStartTime(DateTime.now());
    request.setEndTime(DateTime.now().plusDays(5));
    request.setHorizonDays(2);
    request.setPastDays(1);
    request.setRule(null);
    request.setRuleName(null);

    SourcingAttributesDefinitionResponse response = new SourcingAttributesDefinitionResponse();
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            request.getOrgId(), SourcingAttributesDefinitionScopeEnum.TRANSFER_SCHEDULE_RULE))
        .thenReturn(response);

    // Act
    List<TransferScheduleRangeResponse> result =
        transferScheduleService.fetchTransferSchedulesInRange(request);

    // Assert
    assertEquals(Collections.emptyList(), result);

    verify(transferSchedulePersistenceService, times(0))
        .fetchTransferSchedulesInRange(any(TransferScheduleDomainRequest.class));
  }

  @Test
  @DisplayName("Fetch schedules in range without both start time and end time")
  void testFetchTransferSchedulesInRangeWithoutRange() {
    // Arrange
    TransferScheduleRangeRequest request = new TransferScheduleRangeRequest();
    request.setRule("DC:'KITCHEN");
    request.setRuleName("Rule1");
    request.setDropoffNodeId("Node1");

    List<TransferScheduleResponse> expectedResponse =
        List.of(testUtil.getTransferScheduleResponse());

    when(transferSchedulePersistenceService.fetchTransferSchedulesInRange(any()))
        .thenReturn(List.of(testUtil.getTransferScheduleEntity()));

    // Act
    List<TransferScheduleRangeResponse> actualResponse =
        transferScheduleService.fetchTransferSchedulesInRange(request);

    // Assert
    assertNotNull(actualResponse);
    assertEquals(
        expectedResponse.get(0).getSourceNodeId(), actualResponse.get(0).getSourceNodeId());
    assertEquals(
        expectedResponse.get(0).getDropoffNodeId(), actualResponse.get(0).getDropoffNodeId());

    verify(transferSchedulePersistenceService, times(1))
        .fetchTransferSchedulesInRange(any(TransferScheduleDomainRequest.class));
  }

  @Test
  @DisplayName("Fetch Transfer Schedule List with Sourcing Attribute Id")
  void fetchTransferScheduleListWithSourcingAttributeId()
      throws CommonServiceException, PromiseEngineException {
    String orgId = "TEST";
    Boolean isPaginated = true;
    PageParams pageParams = getPageParams();
    FetchTransferScheduleRequest request = new FetchTransferScheduleRequest();
    request.setIsSourcingAttributeEnabled(true);
    request.setSourcingAttributeId(1L);

    RulesConfigurationResponse ruleConfig = new RulesConfigurationResponse();
    ruleConfig.setRuleName("TestRule");
    ruleConfig.setRule("TestRuleValue");
    when(ruleConfigurationService.fetchRuleByOrgIdAndAttributeDefinitionIdAndModuleNameAndScope(
            anyString(),
            anyLong(),
            any(RulesConfigurationModuleNameEnum.class),
            any(SourcingAttributesDefinitionScopeEnum.class)))
        .thenReturn(List.of(ruleConfig));

    Page<TransferScheduleResponse> expectedPage = Page.empty();
    when(transferSchedulePersistenceService.fetchTransferSchedulesList(
            anyString(), any(FetchTransferScheduleRequest.class), any(Pageable.class)))
        .thenReturn(expectedPage);

    Page<TransferScheduleResponse> response =
        transferScheduleService.fetchTransferScheduleList(orgId, isPaginated, pageParams, request);

    assertNotNull(response);
    assertEquals(expectedPage, response);
    verify(ruleConfigurationService, times(1))
        .fetchRuleByOrgIdAndAttributeDefinitionIdAndModuleNameAndScope(
            anyString(),
            anyLong(),
            any(RulesConfigurationModuleNameEnum.class),
            any(SourcingAttributesDefinitionScopeEnum.class));
    verify(transferSchedulePersistenceService, times(1))
        .fetchTransferSchedulesList(
            anyString(), any(FetchTransferScheduleRequest.class), any(Pageable.class));
  }

  @Test
  @DisplayName("Fetch Transfer Schedule List without Sourcing Attribute Id")
  void fetchTransferScheduleListWithoutSourcingAttributeId()
      throws CommonServiceException, PromiseEngineException {
    String orgId = "TEST";
    Boolean isPaginated = true;
    PageParams pageParameters = getPageParams();
    FetchTransferScheduleRequest request = new FetchTransferScheduleRequest();

    Page<TransferScheduleResponse> expectedPage = Page.empty();
    when(transferSchedulePersistenceService.fetchTransferSchedulesList(
            anyString(), any(FetchTransferScheduleRequest.class), any(Pageable.class)))
        .thenReturn(expectedPage);

    Page<TransferScheduleResponse> response =
        transferScheduleService.fetchTransferScheduleList(
            orgId, isPaginated, pageParameters, request);

    assertNotNull(response);
    assertEquals(expectedPage, response);
    verify(ruleConfigurationService, times(0))
        .fetchRuleByOrgIdAndAttributeDefinitionIdAndModuleNameAndScope(
            anyString(),
            anyLong(),
            any(RulesConfigurationModuleNameEnum.class),
            any(SourcingAttributesDefinitionScopeEnum.class));
    verify(transferSchedulePersistenceService, times(1))
        .fetchTransferSchedulesList(
            anyString(), any(FetchTransferScheduleRequest.class), any(Pageable.class));
  }

  @NotNull
  private static PageParams getPageParams() {
    PageParams pageParams = new PageParams();
    pageParams.setPageNo(Optional.of(1));
    pageParams.setPageSize(Optional.of(10));
    pageParams.setSortOrder(Optional.of("ASC"));
    pageParams.setSortBy(Optional.of("sourceNodeId"));
    return pageParams;
  }

  @Test
  @DisplayName("Test when SourcingAttributeEnabled is null")
  void testWhenSourcingAttributeEnabledIsNull()
      throws CommonServiceException, PromiseEngineException {
    String orgId = "test";
    Boolean isPaginated = true;
    FetchTransferScheduleRequest request = new FetchTransferScheduleRequest();
    request.setIsSourcingAttributeEnabled(null);
    PageParams pageParameters = getPageParams();
    transferScheduleService.fetchTransferScheduleList(orgId, isPaginated, pageParameters, request);

    verify(ruleConfigurationService, never())
        .fetchRuleByOrgIdAndAttributeDefinitionIdAndModuleNameAndScope(
            anyString(), anyLong(), any(), any());
  }

  @Test
  @DisplayName("Test when SourcingAttributeEnabled is false")
  void testWhenSourcingAttributeEnabledIsFalse()
      throws PromiseEngineException, CommonServiceException {
    String orgId = "test";
    Boolean isPaginated = true;
    FetchTransferScheduleRequest request = new FetchTransferScheduleRequest();
    request.setIsSourcingAttributeEnabled(false);

    PageParams pageParameters = getPageParams();
    transferScheduleService.fetchTransferScheduleList(orgId, isPaginated, pageParameters, request);

    verify(ruleConfigurationService, never())
        .fetchRuleByOrgIdAndAttributeDefinitionIdAndModuleNameAndScope(
            anyString(), anyLong(), any(), any());
  }

  @Test
  @DisplayName("Test when SourcingAttributeEnabled is true and SourcingAttributeId is null")
  void testWhenSourcingAttributeEnabledIsTrueAndSourcingAttributeIdIsNull()
      throws PromiseEngineException, CommonServiceException {
    String orgId = "test";
    Boolean isPaginated = true;
    FetchTransferScheduleRequest request = new FetchTransferScheduleRequest();
    request.setIsSourcingAttributeEnabled(true);
    request.setSourcingAttributeId(null);

    PageParams pageParameters = getPageParams();
    transferScheduleService.fetchTransferScheduleList(orgId, isPaginated, pageParameters, request);

    verify(ruleConfigurationService, never())
        .fetchRuleByOrgIdAndAttributeDefinitionIdAndModuleNameAndScope(
            anyString(), anyLong(), any(), any());
    assertTrue(request.getRuleInfo().isEmpty());
  }

  @Test
  @DisplayName("Test when SourcingAttributeEnabled is true and SourcingAttributeId is not null")
  void testWhenSourcingAttributeEnabledIsTrueAndSourcingAttributeIdIsNotNull()
      throws PromiseEngineException, CommonServiceException {
    String orgId = "test";
    Boolean isPaginated = true;
    FetchTransferScheduleRequest request = new FetchTransferScheduleRequest();
    request.setIsSourcingAttributeEnabled(true);
    request.setSourcingAttributeId(1L);

    List<RulesConfigurationResponse> ruleConfigs = new ArrayList<>();
    RulesConfigurationResponse ruleConfig = new RulesConfigurationResponse();
    ruleConfig.setRuleName("TestRule");
    ruleConfig.setRule("TestRuleValue");
    ruleConfigs.add(ruleConfig);

    when(ruleConfigurationService.fetchRuleByOrgIdAndAttributeDefinitionIdAndModuleNameAndScope(
            anyString(), anyLong(), any(), any()))
        .thenReturn(ruleConfigs);

    PageParams pageParameters = getPageParams();
    transferScheduleService.fetchTransferScheduleList(orgId, isPaginated, pageParameters, request);

    verify(ruleConfigurationService, times(1))
        .fetchRuleByOrgIdAndAttributeDefinitionIdAndModuleNameAndScope(
            anyString(), anyLong(), any(), any());
    assertEquals(1, request.getRuleInfo().size());
    assertEquals("TestRule", request.getRuleInfo().get(0).getFirst());
    assertEquals("TestRuleValue", request.getRuleInfo().get(0).getSecond());
  }

  @Test
  @DisplayName("Test when batch transfer schedule is apply validation is true")
  void testBatchTransferScheduleWithValidations() throws PromiseEngineException {
    DateTime dateTime = DateTime.now();
    TransferScheduleBatchRequest request = testUtil.getTransferScheduleBatchRequest(dateTime);
    when(nodeFeign.checkIfNodesExist(any(), any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(List.of("Node1", "Node2", "Node3"))
                .success(true)
                .build());
    when(ruleConfigurationService.fetchRuleByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(
            RuleConfigurationParam.builder()
                .orgId(TestUtil.ORG_ID)
                .rule("ABC:XYZ:001")
                .ruleName("TRANSFER_RULE")
                .moduleName(RulesConfigurationModuleNameEnum.TRANSFER_SCHEDULE)
                .scope(SourcingAttributesDefinitionScopeEnum.TRANSFER_SCHEDULE_RULE)
                .build()))
        .thenReturn(
            Optional.of(RulesConfigurationResponse.builder().orgId(TestUtil.ORG_ID).build()));
    List<TransferScheduleDomainDto> domainDtos = new ArrayList<>();
    domainDtos.add(
        new TransferScheduleDomainDto(
            1L,
            TestUtil.ORG_ID,
            "Node1",
            "Node2",
            dateTime.toDate(),
            dateTime.plusDays(5).toDate(),
            "ABC:XYZ:001",
            "TRANSFER_RULE"));
    domainDtos.add(
        new TransferScheduleDomainDto(
            1L,
            TestUtil.ORG_ID + "1",
            "Node1" + "1",
            "Node2",
            dateTime.toDate(),
            dateTime.plusDays(5).toDate(),
            "ABC:XYZ:001",
            "TRANSFER_RULE"));
    domainDtos.add(
        new TransferScheduleDomainDto(
            1L,
            TestUtil.ORG_ID,
            "Node1",
            "Node2" + "1",
            dateTime.toDate(),
            dateTime.plusDays(5).toDate(),
            "ABC:XYZ:001",
            "TRANSFER_RULE"));
    domainDtos.add(
        new TransferScheduleDomainDto(
            1L,
            TestUtil.ORG_ID,
            "Node1",
            "Node2",
            dateTime.plusHours(5).toDate(),
            dateTime.plusDays(5).toDate(),
            "ABC:XYZ:001",
            "TRANSFER_RULE"));

    when(transferSchedulePersistenceService.deleteTransferSchedules(any())).thenReturn(domainDtos);
    TransferScheduleBatchResponse response =
        transferScheduleService.batchTransferSchedules(request, TestUtil.ORG_ID);
    List<TransferScheduleConsumerResult> expectedResults = new ArrayList<>();
    expectedResults.add(
        new TransferScheduleConsumerResult(1, true, "Transfer schedule created successfully", 200));
    expectedResults.add(
        new TransferScheduleConsumerResult(
            2, false, "Rule or node or time validation failed", 400));
    expectedResults.add(
        new TransferScheduleConsumerResult(
            3, false, "Rule or node or time validation failed", 400));
    expectedResults.add(
        new TransferScheduleConsumerResult(
            4, false, "Rule or node or time validation failed", 400));
    expectedResults.add(
        new TransferScheduleConsumerResult(
            5, false, "Rule or node or time validation failed", 400));
    expectedResults.add(
        new TransferScheduleConsumerResult(
            6, false, "Rule or node or time validation failed", 400));
    expectedResults.add(
        new TransferScheduleConsumerResult(7, true, "Transfer schedule created successfully", 200));
    expectedResults.add(
        new TransferScheduleConsumerResult(8, true, "Transfer schedule deleted successfully", 200));
    expectedResults.add(
        new TransferScheduleConsumerResult(9, false, "Transfer schedule not found", 404));
    expectedResults.add(new TransferScheduleConsumerResult(10, false, "Invalid action type", 400));
    expectedResults.add(
        new TransferScheduleConsumerResult(11, false, "Action type cannot be null", 400));

    TransferScheduleBatchResponse expectedResponse =
        new TransferScheduleBatchResponse(11, 3, 8, expectedResults);

    Assertions.assertEquals(expectedResponse.getTotalCount(), response.getTotalCount());
    Assertions.assertEquals(expectedResponse.getSuccessCount(), response.getSuccessCount());
    Assertions.assertEquals(expectedResponse.getFailureCount(), response.getFailureCount());
    Assertions.assertEquals(expectedResponse.getResults().size(), response.getResults().size());
    Assertions.assertEquals(
        expectedResponse.getResults(),
        response.getResults().stream()
            .sorted((o1, o2) -> o1.getIndex().compareTo(o2.getIndex()))
            .toList());
  }

  @Test
  @DisplayName("Test when batch transfer schedule is apply validation is false")
  void testBatchTransferScheduleWithOutValidations() throws PromiseEngineException {
    DateTime dateTime = DateTime.now();
    TransferScheduleBatchRequest request = testUtil.getTransferScheduleBatchRequest(dateTime);
    request.setApplyValidation(false);
    when(nodeFeign.checkIfNodesExist(any(), any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(List.of("Node1", "Node2", "Node3"))
                .success(true)
                .build());
    when(ruleConfigurationService.fetchRuleByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(
            RuleConfigurationParam.builder()
                .orgId(TestUtil.ORG_ID)
                .rule("ABC:XYZ:001")
                .ruleName("TRANSFER_RULE")
                .moduleName(RulesConfigurationModuleNameEnum.TRANSFER_SCHEDULE)
                .scope(SourcingAttributesDefinitionScopeEnum.TRANSFER_SCHEDULE_RULE)
                .build()))
        .thenReturn(
            Optional.of(RulesConfigurationResponse.builder().orgId(TestUtil.ORG_ID).build()));
    List<TransferScheduleDomainDto> domainDtos = new ArrayList<>();
    domainDtos.add(
        new TransferScheduleDomainDto(
            1L,
            TestUtil.ORG_ID,
            "Node1",
            "Node2",
            dateTime.toDate(),
            dateTime.plusDays(5).toDate(),
            "ABC:XYZ:001",
            "TRANSFER_RULE"));
    domainDtos.add(
        new TransferScheduleDomainDto(
            1L,
            TestUtil.ORG_ID + "1",
            "Node1" + "1",
            "Node2",
            dateTime.toDate(),
            dateTime.plusDays(5).toDate(),
            "ABC:XYZ:001",
            "TRANSFER_RULE"));
    domainDtos.add(
        new TransferScheduleDomainDto(
            1L,
            TestUtil.ORG_ID,
            "Node1",
            "Node2" + "1",
            dateTime.toDate(),
            dateTime.plusDays(5).toDate(),
            "ABC:XYZ:001",
            "TRANSFER_RULE"));
    domainDtos.add(
        new TransferScheduleDomainDto(
            1L,
            TestUtil.ORG_ID,
            "Node1",
            "Node2",
            dateTime.plusHours(5).toDate(),
            dateTime.plusDays(5).toDate(),
            "ABC:XYZ:001",
            "TRANSFER_RULE"));

    when(transferSchedulePersistenceService.deleteTransferSchedules(any())).thenReturn(domainDtos);
    TransferScheduleBatchResponse response =
        transferScheduleService.batchTransferSchedules(request, TestUtil.ORG_ID);
    List<TransferScheduleConsumerResult> expectedResults = new ArrayList<>();
    expectedResults.add(
        new TransferScheduleConsumerResult(1, true, "Transfer schedule created successfully", 200));
    expectedResults.add(
        new TransferScheduleConsumerResult(2, true, "Transfer schedule created successfully", 200));
    expectedResults.add(
        new TransferScheduleConsumerResult(3, true, "Transfer schedule created successfully", 200));
    expectedResults.add(
        new TransferScheduleConsumerResult(4, true, "Transfer schedule created successfully", 200));
    expectedResults.add(
        new TransferScheduleConsumerResult(5, true, "Transfer schedule created successfully", 200));
    expectedResults.add(
        new TransferScheduleConsumerResult(6, true, "Transfer schedule created successfully", 200));
    expectedResults.add(
        new TransferScheduleConsumerResult(7, true, "Transfer schedule created successfully", 200));
    expectedResults.add(
        new TransferScheduleConsumerResult(8, true, "Transfer schedule deleted successfully", 200));
    expectedResults.add(
        new TransferScheduleConsumerResult(9, false, "Transfer schedule not found", 404));
    expectedResults.add(new TransferScheduleConsumerResult(10, false, "Invalid action type", 400));
    expectedResults.add(
        new TransferScheduleConsumerResult(11, false, "Action type cannot be null", 400));

    TransferScheduleBatchResponse expectedResponse =
        new TransferScheduleBatchResponse(11, 8, 3, expectedResults);

    Assertions.assertEquals(expectedResponse.getTotalCount(), response.getTotalCount());
    Assertions.assertEquals(expectedResponse.getSuccessCount(), response.getSuccessCount());
    Assertions.assertEquals(expectedResponse.getFailureCount(), response.getFailureCount());
    Assertions.assertEquals(expectedResponse.getResults().size(), response.getResults().size());
    Assertions.assertEquals(
        expectedResponse.getResults(),
        response.getResults().stream()
            .sorted((o1, o2) -> o1.getIndex().compareTo(o2.getIndex()))
            .toList());
  }

  @Test
  @DisplayName("Test when batch transfer schedule failure")
  void testBatchTransferScheduleFailure() throws PromiseEngineException {
    DateTime dateTime = DateTime.now();
    TransferScheduleBatchRequest request = testUtil.getTransferScheduleBatchRequest(dateTime);
    List<TransferScheduleConsumerRequest> transferScheduleConsumerRequests =
        List.of(
            request.getTransferScheduleConsumerRequests().get(0),
            request.getTransferScheduleConsumerRequests().get(1),
            request.getTransferScheduleConsumerRequests().get(2));
    transferScheduleConsumerRequests.get(2).setAction(ActionEnum.DELETE);
    request.setTransferScheduleConsumerRequests(transferScheduleConsumerRequests);
    when(transferSchedulePersistenceService.deleteTransferSchedules(any()))
        .thenThrow(new RuntimeException("Error while delete"));
    when(transferSchedulePersistenceService.saveTransferSchedules(any()))
        .thenThrow(new RuntimeException("Error while create"));
    when(nodeFeign.checkIfNodesExist(any(), any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(List.of("Node1", "Node2", "Node3"))
                .success(true)
                .build());
    when(ruleConfigurationService.fetchRuleByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(
            RuleConfigurationParam.builder()
                .orgId(TestUtil.ORG_ID)
                .rule("ABC:XYZ:001")
                .ruleName("TRANSFER_RULE")
                .moduleName(RulesConfigurationModuleNameEnum.TRANSFER_SCHEDULE)
                .scope(SourcingAttributesDefinitionScopeEnum.TRANSFER_SCHEDULE_RULE)
                .build()))
        .thenReturn(
            Optional.of(RulesConfigurationResponse.builder().orgId(TestUtil.ORG_ID).build()));
    TransferScheduleBatchResponse response =
        transferScheduleService.batchTransferSchedules(request, TestUtil.ORG_ID);
    List<TransferScheduleConsumerResult> expectedResults = new ArrayList<>();
    expectedResults.add(
        new TransferScheduleConsumerResult(1, false, "Error while saving transfer schedule", 500));
    expectedResults.add(
        new TransferScheduleConsumerResult(
            2, false, "Rule or node or time validation failed", 400));
    expectedResults.add(
        new TransferScheduleConsumerResult(
            3, false, "Error while deleting transfer schedule", 500));

    TransferScheduleBatchResponse expectedResponse =
        new TransferScheduleBatchResponse(3, 0, 3, expectedResults);

    Assertions.assertEquals(expectedResponse.getTotalCount(), response.getTotalCount());
    Assertions.assertEquals(expectedResponse.getSuccessCount(), response.getSuccessCount());
    Assertions.assertEquals(expectedResponse.getFailureCount(), response.getFailureCount());
    Assertions.assertEquals(expectedResponse.getResults().size(), response.getResults().size());
    Assertions.assertEquals(
        expectedResponse.getResults(),
        response.getResults().stream()
            .sorted((o1, o2) -> o1.getIndex().compareTo(o2.getIndex()))
            .toList());
  }
}
