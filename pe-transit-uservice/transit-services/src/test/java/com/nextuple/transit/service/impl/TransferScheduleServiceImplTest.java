/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.service.RulesConfigurationService;
import com.nextuple.promise.sourcing.rule.service.SourcingAttributesDefinitionService;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.inbound.TransferScheduleCreationRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleRangeRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainRequest;
import com.nextuple.transit.persistence.service.TransferSchedulePersistenceService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    List<TransferScheduleResponse> actualResponse =
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
  public void testFetchTransfersWithNullRuleAndRuleNameAndActiveRuleDefinition()
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
    List<TransferScheduleResponse> result =
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
    List<TransferScheduleResponse> actualResponse =
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
}
