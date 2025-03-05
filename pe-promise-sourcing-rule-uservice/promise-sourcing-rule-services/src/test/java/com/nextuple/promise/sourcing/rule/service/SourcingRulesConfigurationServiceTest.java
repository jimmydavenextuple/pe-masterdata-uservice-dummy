/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import static com.nextuple.promise.sourcing.rule.TestUtil.SOURCING_RULE_NAME;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchSourcingRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.RulesConfigurationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingRuleIdRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdateSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.AllSourcingRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchSourcingRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.edit.sourcing.rules.UpdateSourcingRuleConfigurationResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingAttributeValuesInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingRuleDetails;
import com.nextuple.promise.sourcing.rule.persistence.domain.*;
import com.nextuple.promise.sourcing.rule.persistence.service.*;
import com.nextuple.promise.sourcing.rule.service.impl.RuleRetrievalFactory;
import com.nextuple.promise.sourcing.rule.service.impl.SourcingRuleConfigurationImpl;
import com.nextuple.promise.sourcing.rule.utils.FetchRulesUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

class SourcingRulesConfigurationServiceTest {
  @InjectMocks private SourcingRulesConfigurationService sourcingRulesConfigurationService;

  @Mock
  private SourcingRulesConfigurationPersistenceService sourcingRulesConfigurationPersistenceService;

  @Mock
  private SourcingAttributesDefinitionPersistenceService
      sourcingAttributesDefinitionPersistenceService;

  @Mock private NodeGroupPersistenceService nodeGroupPersistenceService;
  @Mock private NodePriorityPersistenceService nodePriorityPersistenceService;
  @Mock private SourcingAttributePersistenceService sourcingAttributePersistenceService;
  @Mock private SourcingAttributesDefinitionService sourcingAttributesDefinitionService;
  @Mock private SourcingRuleDetailsPersistenceService sourcingRuleDetailsPersistenceService;
  @Mock private AttributeValuesPersistenceService attributeValuesPersistenceService;
  @Mock private SourcingRuleConfigurationImpl ruleRetrievalService;
  @Mock private RuleRetrievalFactory ruleRetrievalFactory;
  @Mock FetchRulesUtil fetchRulesUtil;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processConfigureSourcingRuleTest1() throws PromiseEngineException, CommonServiceException {
    RulesConfigurationRequest rulesConfigurationRequest = testUtil.getRulesConfigurationRequest();

    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(Optional.empty());
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingRulesConfigurationPersistenceService.saveSourcingRule(
            any(SourcingRulesConfigurationDomainDto.class)))
        .thenReturn(testUtil.getSourcingRulesEntity());
    when(sourcingRuleDetailsPersistenceService.saveSourcingNodes(
            any(SourcingRuleDetailsDomainDto.class)))
        .thenReturn(testUtil.getSourcingRuleDetailsEntity());

    SourcingRuleDetails sourcingRuleDetails =
        sourcingRulesConfigurationService.processConfigureSourcingRule(rulesConfigurationRequest);
    assertEquals(testUtil.getSourcingRulesEntity().getId(), sourcingRuleDetails.getId());
    assertEquals(
        testUtil.getSourcingRulesEntity().getSourcingRuleName(),
        sourcingRuleDetails.getSourcingRuleName());
    assertEquals(
        testUtil.getSourcingRulesEntity().getSourcingAttributesDefinitionId(),
        sourcingRuleDetails.getSourcingAttributesDefinitionId());
    assertEquals(
        testUtil.getSourcingRulesEntity().getCustomAttributes(),
        sourcingRuleDetails.getCustomAttributes());

    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
            anyString(), anyLong(), anyString());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
    verify(nodePriorityPersistenceService, times(1))
        .fetchNodePriorityListByOrgIdAndNodeGroupId(anyString(), anyLong());
    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .saveSourcingRule(any(SourcingRulesConfigurationDomainDto.class));
  }

  @Test
  void processConfigureSourcingRuleTest2() throws PromiseEngineException, CommonServiceException {
    RulesConfigurationRequest rulesConfigurationRequest = testUtil.getRulesConfigurationRequest();

    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));
    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingRuleDetailsPersistenceService.saveSourcingNodes(
            any(SourcingRuleDetailsDomainDto.class)))
        .thenReturn(testUtil.getSourcingRuleDetailsEntity());

    SourcingRuleDetails sourcingRuleDetails =
        sourcingRulesConfigurationService.processConfigureSourcingRule(rulesConfigurationRequest);
    assertEquals(testUtil.getSourcingRulesEntity().getId(), sourcingRuleDetails.getId());
    assertEquals(
        testUtil.getSourcingRulesEntity().getSourcingRuleName(),
        sourcingRuleDetails.getSourcingRuleName());

    assertEquals(
        testUtil.getSourcingRulesEntity().getCustomAttributes(),
        sourcingRuleDetails.getCustomAttributes());

    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
            anyString(), anyLong(), anyString());
    verify(nodePriorityPersistenceService, times(1))
        .fetchNodePriorityListByOrgIdAndNodeGroupId(anyString(), anyLong());
  }

  @Test
  void processConfigureSourcingRuleExceptionTest1() throws PromiseEngineException {
    RulesConfigurationRequest rulesConfigurationRequest = testUtil.getRulesConfigurationRequest();
    rulesConfigurationRequest.setSourcingRule("1,2");

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.processConfigureSourcingRule(
                    rulesConfigurationRequest));
    assertEquals("Invalid format! All the characters except comma are allowed", ex.getMessage());
  }

  @Test
  void processConfigureSourcingRuleExceptionTest2() throws PromiseEngineException {
    RulesConfigurationRequest rulesConfigurationRequest = testUtil.getRulesConfigurationRequest();

    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByOrgIdAndSourcingRule(
            anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.INACTIVE)));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.processConfigureSourcingRule(
                    rulesConfigurationRequest));
    Assertions.assertTrue(
        ex.getMessage()
            .startsWith(
                "Invalid attributes definition for given scope: SOURCING_RULE / Sourcing attributes definition exists but not in ACTIVE status"));

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processConfigureSourcingRuleExceptionTest3() throws PromiseEngineException {
    RulesConfigurationRequest rulesConfigurationRequest = testUtil.getRulesConfigurationRequest();
    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByOrgIdAndSourcingRule(
            anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.processConfigureSourcingRule(
                    rulesConfigurationRequest));
    assertTrue(
        ex.getMessage()
            .startsWith(
                "Invalid attributes definition for given scope: SOURCING_RULE / Sourcing attributes definition exists but not in ACTIVE status"));

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processConfigureSourcingRuleExceptionTest4() throws PromiseEngineException {
    RulesConfigurationRequest rulesConfigurationRequest = testUtil.getRulesConfigurationRequest();
    rulesConfigurationRequest.setSourcingRule("1");

    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByOrgIdAndSourcingRule(
            anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.processConfigureSourcingRule(
                    rulesConfigurationRequest));
    assertEquals(
        "Can't add or fetch the sourcing rule as all the required attributes values are not present",
        ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processConfigureSourcingRuleExceptionTest5() throws PromiseEngineException {
    RulesConfigurationRequest rulesConfigurationRequest = testUtil.getRulesConfigurationRequest();
    NodeGroupDomainDto nodeGroupEntity = testUtil.getNodeGroupEntity();
    nodeGroupEntity.setId(2L);

    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong())).thenReturn(Optional.empty());
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByOrgIdAndSourcingRule(
            anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(nodeGroupPersistenceService.fetchNodeGroupListByOrgId(anyString()))
        .thenReturn(List.of(nodeGroupEntity));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.processConfigureSourcingRule(
                    rulesConfigurationRequest));
    assertEquals("Node group doesn't exist", ex.getMessage());
  }

  @Test
  void processConfigureSourcingRuleExceptionTest6() throws PromiseEngineException {
    RulesConfigurationRequest rulesConfigurationRequest = testUtil.getRulesConfigurationRequest();

    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.processConfigureSourcingRule(
                    rulesConfigurationRequest));
    assertEquals("Node group exist but no nodes are associated to it", ex.getMessage());
  }

  @Test
  void processConfigureSourcingRuleExceptionTest7() throws PromiseEngineException {
    RulesConfigurationRequest rulesConfigurationRequest = testUtil.getRulesConfigurationRequest();

    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));
    when(sourcingRulesConfigurationPersistenceService.saveSourcingRule(any()))
        .thenReturn(testUtil.getSourcingRulesEntity());
    when(sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionDomainDto =
        testUtil.getSourcingRuleAttributesDefinitionDomainDto("R1:R2", "O1:O2");
    sourcingAttributesDefinitionDomainDto.setScope(
        SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    sourcingAttributesDefinitionDomainDto.setStatus(SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionDomainDto));
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.processConfigureSourcingRule(
                    rulesConfigurationRequest));
    assertEquals("Combination of sourcing rule and node group already exists", ex.getMessage());
  }

  @Test
  void processConfigureSourcingRuleExceptionTest8() throws PromiseEngineException {
    RulesConfigurationRequest rulesConfigurationRequest = testUtil.getRulesConfigurationRequest();
    rulesConfigurationRequest.setSourcingRule("1:2:3");

    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByOrgIdAndSourcingRule(
            anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.processConfigureSourcingRule(
                    rulesConfigurationRequest));
    assertEquals(
        "Can't add the sourcing rule as length of attributes is more than optional and required attributes combined",
        ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processConfigureSourcingRuleExceptionTest9() throws PromiseEngineException {
    RulesConfigurationRequest rulesConfigurationRequest = testUtil.getRulesConfigurationRequest();
    rulesConfigurationRequest.setSourcingRule(":2:3");
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByOrgIdAndSourcingRule(
            anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.processConfigureSourcingRule(
                    rulesConfigurationRequest));
    assertEquals(
        "Can't add the sourcing rule as length of attributes is more than optional and required attributes combined",
        ex.getMessage());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processConfigureSourcingRuleExceptionTest13() throws PromiseEngineException {
    RulesConfigurationRequest rulesConfigurationRequest = testUtil.getRulesConfigurationRequest();
    rulesConfigurationRequest.setSourcingRule("1:2:3:4");
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3");

    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByOrgIdAndSourcingRule(
            anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(sourcingAttributesDefinitionEntity));
    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.processConfigureSourcingRule(
                    rulesConfigurationRequest));
    assertEquals(
        "Can't add the sourcing rule as length of attributes is more than optional and required attributes combined",
        ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @DisplayName("When Create Sourcing Rule Request Has Invalid Node Group")
  @Test
  void processConfigureSourcingRuleExceptionWhenRequestHasInvalidNodeGroup()
      throws PromiseEngineException {
    RulesConfigurationRequest rulesConfigurationRequest =
        testUtil.getRulesConfigurationRequestWithInvalidNodeGroup();

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.processConfigureSourcingRule(
                    rulesConfigurationRequest));

    assertEquals("Invalid format! Only numeric characters allowed.", ex.getMessage());
    verify(sourcingRulesConfigurationPersistenceService, times(0))
        .getSourcingRuleByOrgIdAndSourcingRule(anyString(), anyString());
  }

  @DisplayName("When Create Sourcing Rule Request Has Duplicate Order Attributes Values")
  @Test
  void processConfigureSourcingRuleExceptionWhenRequestHasDuplicateOrderAttributeValues()
      throws PromiseEngineException {
    RulesConfigurationRequest rulesConfigurationRequest = testUtil.getRulesConfigurationRequest();
    rulesConfigurationRequest.setSourcingRuleName("rule-2");
    rulesConfigurationRequest.setNodeGroups("100");

    NodeGroupDomainDto nodeGroupEntity = testUtil.getNodeGroupEntity();
    nodeGroupEntity.setId(100L);

    NodePriorityDomainDto nodePriorityEntity = testUtil.getNodePriorityEntity();
    nodePriorityEntity.setNodeGroupId(100L);

    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(nodeGroupEntity));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(nodePriorityEntity));
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.processConfigureSourcingRule(
                    rulesConfigurationRequest));

    String errorMessage =
        "Can’t create/update sourcing rule as sourcing rule %s already has the same order attributes value"
            .formatted(testUtil.getSourcingRulesEntity().getSourcingRuleName());
    assertEquals(errorMessage, ex.getMessage());

    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
            anyString(), anyLong(), anyString());
  }

  @Test
  void processConfigureSourcingRuleWhenSourcingRuleNameExistsTest() throws PromiseEngineException {
    RulesConfigurationRequest rulesConfigurationRequest = testUtil.getRulesConfigurationRequest();

    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
                anyString(), anyLong(), anyString()))
        .thenReturn(testUtil.getSourcingRulesEntity());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.processConfigureSourcingRule(
                    rulesConfigurationRequest));
    assertEquals("Sourcing Rule exists with given sourcingRuleName", ex.getMessage());

    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
            anyString(), anyLong(), anyString());
  }

  @Test
  void processConfigureSourcingRuleTestSameNodeIdsInAlreadyExistingRuleConfig()
      throws PromiseEngineException, CommonServiceException {
    RulesConfigurationRequest rulesConfigurationRequest = testUtil.getRulesConfigurationRequest();
    rulesConfigurationRequest.setNodeGroups(String.valueOf(3));
    NodePriorityDomainDto existingMockResponse1 = testUtil.getNodePriorityEntity();
    existingMockResponse1.setNodeId("Node-1");
    existingMockResponse1.setNodeGroupId(1L);

    NodePriorityDomainDto existingMockResponse2 = testUtil.getNodePriorityEntity();
    existingMockResponse2.setNodeId("Node-2");
    existingMockResponse2.setNodeGroupId(1L);

    NodePriorityDomainDto existingMockResponse3 = testUtil.getNodePriorityEntity();
    existingMockResponse3.setNodeId("Node-3");
    existingMockResponse3.setNodeGroupId(2L);

    NodePriorityDomainDto existingMockResponse4 = testUtil.getNodePriorityEntity();
    existingMockResponse4.setNodeId("Node-4");
    existingMockResponse4.setNodeGroupId(2L);

    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(existingMockResponse1, existingMockResponse2))
        .thenReturn(List.of(existingMockResponse1, existingMockResponse2))
        .thenReturn(List.of(existingMockResponse3, existingMockResponse4))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByOrgIdAndSourcingRule(
            any(), any()))
        .thenReturn(Optional.of(List.of(testUtil.getSourcingRulesEntity())));
    when(sourcingRuleDetailsPersistenceService.saveSourcingNodes(
            any(SourcingRuleDetailsDomainDto.class)))
        .thenReturn(testUtil.getSourcingRuleDetailsEntity());

    SourcingRuleDetailsDomainDto sourcingRuleDetailsEntity1 =
        testUtil.getSourcingRuleDetailsEntity();
    SourcingRuleDetailsDomainDto sourcingRuleDetailsEntity2 =
        testUtil.getSourcingRuleDetailsEntity();
    sourcingRuleDetailsEntity2.setNodeGroups("2");
    when(sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(any(), any()))
        .thenReturn(List.of(sourcingRuleDetailsEntity1, sourcingRuleDetailsEntity2));
    SourcingRuleDetails sourcingRuleDetails =
        sourcingRulesConfigurationService.processConfigureSourcingRule(rulesConfigurationRequest);

    assertEquals(testUtil.getSourcingRulesEntity().getId(), sourcingRuleDetails.getId());
    assertEquals(
        testUtil.getSourcingRulesEntity().getSourcingRuleName(),
        sourcingRuleDetails.getSourcingRuleName());
    assertEquals(
        testUtil.getSourcingRulesEntity().getSourcingAttributesDefinitionId(),
        sourcingRuleDetails.getSourcingAttributesDefinitionId());

    assertEquals(
        testUtil.getSourcingRulesEntity().getCustomAttributes(),
        sourcingRuleDetails.getCustomAttributes());

    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
            anyString(), anyLong(), anyString());
    verify(nodePriorityPersistenceService, times(1))
        .fetchNodePriorityListByOrgIdAndNodeGroupId(anyString(), anyLong());
    verify(sourcingRuleDetailsPersistenceService, times(1))
        .saveSourcingNodes(any(SourcingRuleDetailsDomainDto.class));
    verify(sourcingRulesConfigurationPersistenceService, times(0))
        .saveSourcingRule(any(SourcingRulesConfigurationDomainDto.class));
  }

  @Test
  void getSourcingRuleByIdandOrgIdTest() throws PromiseEngineException, CommonServiceException {
    when(sourcingRuleDetailsPersistenceService.getSourcingRuleDetailsByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getSourcingRuleDetailsEntity()));
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));
    SourcingRuleDetails sourcingRuleDetails =
        sourcingRulesConfigurationService.processGetSourcingRuleDetailsByIdandOrgId(
            TestUtil.SOURCING_RULE_ID, TestUtil.ORG_ID);

    assertEquals(testUtil.getSourcingRulesEntity().getId(), sourcingRuleDetails.getId());
    assertEquals(
        testUtil.getSourcingRulesEntity().getCustomAttributes(),
        sourcingRuleDetails.getCustomAttributes());
    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .getSourcingRuleByIdAndOrgId(anyLong(), anyString());
    verify(sourcingRuleDetailsPersistenceService, times(1))
        .getSourcingRuleDetailsByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getSourcingRuleByIdandOrgIdExceptionTest1() throws PromiseEngineException {
    when(sourcingRuleDetailsPersistenceService.getSourcingRuleDetailsByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingRulesConfigurationService.processGetSourcingRuleDetailsByIdandOrgId(
                  TestUtil.SOURCING_RULE_ID, TestUtil.ORG_ID);
            });

    assertEquals("Sourcing rule details record not found for given id", ex.getMessage());

    verify(sourcingRuleDetailsPersistenceService, times(1))
        .getSourcingRuleDetailsByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getSourcingRuleByIdandOrgIdExceptionTest2() throws PromiseEngineException {
    when(sourcingRuleDetailsPersistenceService.getSourcingRuleDetailsByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getSourcingRuleDetailsEntity()));
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingRulesConfigurationService.processGetSourcingRuleDetailsByIdandOrgId(
                  TestUtil.SOURCING_RULE_ID, TestUtil.ORG_ID);
            });

    assertEquals("Invalid sourcing rule id", ex.getMessage());

    verify(sourcingRuleDetailsPersistenceService, times(1))
        .getSourcingRuleDetailsByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void deleteSourcingRuleTest() throws PromiseEngineException, CommonServiceException {
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));
    when(sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    doNothing()
        .when(sourcingRulesConfigurationPersistenceService)
        .deleteSourcingRule(any(SourcingRulesConfigurationDomainDto.class));
    doNothing()
        .when(sourcingRuleDetailsPersistenceService)
        .deleteMultipleSourcingRuleDetails(any());

    SourcingRuleDetails sourcingRuleDetails =
        sourcingRulesConfigurationService.processDeleteSourcingRuleDetails(
            TestUtil.ORG_ID, TestUtil.SOURCING_RULE_ID);

    assertEquals(testUtil.getSourcingRulesEntity().getId(), sourcingRuleDetails.getId());

    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .deleteSourcingRule(any(SourcingRulesConfigurationDomainDto.class));
    verify(sourcingRuleDetailsPersistenceService, times(1))
        .deleteMultipleSourcingRuleDetails(any());
  }

  @Test
  void deleteSourcingRuleExceptionTest() throws PromiseEngineException {

    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingRulesConfigurationService.processDeleteSourcingRuleDetails(
                  TestUtil.ORG_ID, TestUtil.SOURCING_RULE_ID);
            });

    assertEquals("Sourcing rule not found for given orgId and sourcingRuleId", ex.getMessage());

    verify(sourcingRulesConfigurationPersistenceService, times(0))
        .deleteSourcingRule(any(SourcingRulesConfigurationDomainDto.class));
  }

  @Test
  void processFetchSourcingRulesTest1() throws PromiseEngineException, CommonServiceException {
    SourcingAttributeValuesInfo sourcingAttributeValuesInfo = new SourcingAttributeValuesInfo();
    sourcingAttributeValuesInfo.setRequiredAttributesValue(TestUtil.SOURCING_RULE);
    FetchSourcingRulesRequest fetchSourcingRulesRequest =
        testUtil.fetchSourcingRulesRequest(sourcingAttributeValuesInfo);
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of(testUtil.getSourcingRulesEntity()));
    when(sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));
    when(ruleRetrievalFactory.getRuleRetrievalService(any())).thenReturn(ruleRetrievalService);
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("1")
                          .attributeName("R1")
                          .attributeValue("r1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("2")
                          .attributeName("R2")
                          .attributeValue("r2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getRequiredAttributeDetails(any(), any(List.class), any(), any(), any());
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("3")
                          .attributeName("O1")
                          .attributeValue("o1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("4")
                          .attributeName("O2")
                          .attributeValue("o2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getOptionalAttributeDetails(any(), any(List.class), any(), any(), any(), any());
    FetchSourcingRulesResponse fetchSourcingRulesResponse =
        sourcingRulesConfigurationService.processGetSourcingRules(fetchSourcingRulesRequest);
    assertEquals(1, fetchSourcingRulesResponse.getSourcingRulesInfo().size());
    assertEquals(
        "V1:V2", fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getSourcingRule());
    assertEquals(
        "sourcingRule1",
        fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getSourcingRuleName());
    assertEquals(
        2, fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getRequiredAttributes().size());
    assertEquals(
        2, fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getOptionalAttributes().size());
    assertEquals(
        testUtil.getSourcingRulesEntity().getCustomAttributes(),
        fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getCustomAttributes());

    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
            anyString(), anyLong(), anyString());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processFetchSourcingRulesTest2() throws PromiseEngineException, CommonServiceException {
    SourcingAttributeValuesInfo sourcingAttributeValuesInfo = new SourcingAttributeValuesInfo();
    sourcingAttributeValuesInfo.setRequiredAttributesValue(TestUtil.SOURCING_RULE);
    sourcingAttributeValuesInfo.setOptionalAttributesValue("V3:V4");
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");

    FetchSourcingRulesRequest fetchSourcingRulesRequest =
        testUtil.fetchSourcingRulesRequest(sourcingAttributeValuesInfo);

    List<SourcingRulesConfigurationDomainDto> sourcingRulesConfigurationEntityList =
        new ArrayList<>();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity1 =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity1.setSourcingRule("V1:V2:V3");
    sourcingRulesConfigurationEntityList.add(sourcingRulesConfigurationEntity);
    sourcingRulesConfigurationEntityList.add(sourcingRulesConfigurationEntity1);

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(sourcingRulesConfigurationEntityList);
    when(sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));
    when(ruleRetrievalService.filterAllMatchingRulesByScoring(any(), any(), any(), anyInt(), any()))
        .thenReturn(sourcingRulesConfigurationEntityList);
    when(ruleRetrievalFactory.getRuleRetrievalService(any())).thenReturn(ruleRetrievalService);

    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("1")
                          .attributeName("R1")
                          .attributeValue("r1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("2")
                          .attributeName("R2")
                          .attributeValue("r2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getRequiredAttributeDetails(any(), any(List.class), any(), any(), any());
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("3")
                          .attributeName("O1")
                          .attributeValue("o1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("4")
                          .attributeName("O2")
                          .attributeValue("o2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getOptionalAttributeDetails(any(), any(List.class), any(), any(), any(), any());

    FetchSourcingRulesResponse fetchSourcingRulesResponse =
        sourcingRulesConfigurationService.processGetSourcingRules(fetchSourcingRulesRequest);
    assertEquals(1, fetchSourcingRulesResponse.getSourcingRulesInfo().size());
    assertEquals(
        "V1:V2", fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getSourcingRule());
    assertEquals(
        "sourcingRule1",
        fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getSourcingRuleName());
    assertEquals(
        2, fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getRequiredAttributes().size());
    assertEquals(
        2, fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getOptionalAttributes().size());

    assertEquals(
        testUtil.getSourcingRulesEntity().getCustomAttributes(),
        fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getCustomAttributes());

    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
            anyString(), anyLong(), anyString());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processFetchSourcingRulesTest3() throws PromiseEngineException, CommonServiceException {
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
    SourcingAttributeValuesInfo sourcingAttributeValuesInfo = new SourcingAttributeValuesInfo();
    sourcingAttributeValuesInfo.setRequiredAttributesValue(TestUtil.SOURCING_RULE);
    sourcingAttributeValuesInfo.setOptionalAttributesValue("V3:V4");
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");

    FetchSourcingRulesRequest fetchSourcingRulesRequest =
        testUtil.fetchSourcingRulesRequest(sourcingAttributeValuesInfo);

    List<SourcingRulesConfigurationDomainDto> sourcingRulesConfigurationEntityList =
        new ArrayList<>();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity1 =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity1.setSourcingRule("V1:V2:V3");
    sourcingRulesConfigurationEntity.setSourcingRule("V1:V2:V4");
    sourcingRulesConfigurationEntityList.add(sourcingRulesConfigurationEntity);
    sourcingRulesConfigurationEntityList.add(sourcingRulesConfigurationEntity1);

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(sourcingAttributesDefinitionEntity));
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(sourcingRulesConfigurationEntityList);
    when(sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));
    when(ruleRetrievalService.filterAllMatchingRulesByScoring(any(), any(), any(), anyInt(), any()))
        .thenReturn(List.of());
    when(ruleRetrievalFactory.getRuleRetrievalService(any())).thenReturn(ruleRetrievalService);

    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("1")
                          .attributeName("R1")
                          .attributeValue("r1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("2")
                          .attributeName("R2")
                          .attributeValue("r2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getRequiredAttributeDetails(any(), any(List.class), any(), any(), any());
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("3")
                          .attributeName("O1")
                          .attributeValue("o1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("4")
                          .attributeName("O2")
                          .attributeValue("o2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getOptionalAttributeDetails(any(), any(List.class), any(), any(), any(), any());

    FetchSourcingRulesResponse fetchSourcingRulesResponse =
        sourcingRulesConfigurationService.processGetSourcingRules(fetchSourcingRulesRequest);
    assertEquals(1, fetchSourcingRulesResponse.getSourcingRulesInfo().size());
    assertEquals(
        "V1:V2:V4", fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getSourcingRule());
    assertEquals(
        "sourcingRule1",
        fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getSourcingRuleName());
    assertEquals(
        2, fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getRequiredAttributes().size());
    assertEquals(
        2, fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getOptionalAttributes().size());
    assertEquals(
        testUtil.getSourcingRulesEntity().getCustomAttributes(),
        fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getCustomAttributes());

    verify(sourcingRulesConfigurationPersistenceService, times(2))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
            anyString(), anyLong(), argumentCaptor.capture());
    List<String> capturedArguments = argumentCaptor.getAllValues();
    assertEquals("DEFAULT", capturedArguments.get(1));
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processFetchSourcingRulesTestWithBlankOptionalValue()
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributeValuesInfo sourcingAttributeValuesInfo = new SourcingAttributeValuesInfo();
    sourcingAttributeValuesInfo.setRequiredAttributesValue(TestUtil.SOURCING_RULE);
    FetchSourcingRulesRequest fetchSourcingRulesRequest =
        testUtil.fetchSourcingRulesRequest(sourcingAttributeValuesInfo);
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("");

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of(testUtil.getSourcingRulesEntity()));
    when(sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));

    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("1")
                          .attributeName("R1")
                          .attributeValue("r1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("2")
                          .attributeName("R2")
                          .attributeValue("r2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getRequiredAttributeDetails(any(), any(List.class), any(), any(), any());
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("3")
                          .attributeName("O1")
                          .attributeValue("o1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("4")
                          .attributeName("O2")
                          .attributeValue("o2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getOptionalAttributeDetails(any(), any(List.class), any(), any(), any(), any());

    FetchSourcingRulesResponse fetchSourcingRulesResponse =
        sourcingRulesConfigurationService.processGetSourcingRules(fetchSourcingRulesRequest);
    assertEquals(1, fetchSourcingRulesResponse.getSourcingRulesInfo().size());
    assertEquals(
        "V1:V2", fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getSourcingRule());
    assertEquals(
        "sourcingRule1",
        fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getSourcingRuleName());
    assertEquals(
        2, fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getRequiredAttributes().size());
    assertEquals(
        0, fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getOptionalAttributes().size());

    assertEquals(
        testUtil.getSourcingRulesEntity().getCustomAttributes(),
        fetchSourcingRulesResponse.getSourcingRulesInfo().get(0).getCustomAttributes());

    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
            anyString(), anyLong(), anyString());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processFetchSourcingRulesExceptionTest1() throws PromiseEngineException {
    SourcingAttributeValuesInfo sourcingAttributeValuesInfo = new SourcingAttributeValuesInfo();
    sourcingAttributeValuesInfo.setRequiredAttributesValue(TestUtil.SOURCING_RULE);
    sourcingAttributeValuesInfo.setOptionalAttributesValue("V3");

    FetchSourcingRulesRequest fetchSourcingRulesRequest =
        testUtil.fetchSourcingRulesRequest(sourcingAttributeValuesInfo);

    List<SourcingRulesConfigurationDomainDto> sourcingRulesConfigurationEntityList =
        new ArrayList<>();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity1 =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity1.setSourcingRule("V1:V2:V4");
    sourcingRulesConfigurationEntityList.add(sourcingRulesConfigurationEntity1);

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(sourcingAttributesDefinitionEntity));
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of());
    when(sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingRulesConfigurationService.processGetSourcingRules(fetchSourcingRulesRequest);
            });

    assertEquals("Default sourcing rules not configured", ex.getMessage());

    verify(sourcingRulesConfigurationPersistenceService, times(2))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
            anyString(), anyLong(), anyString());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processFetchSourcingRulesExceptionWhenNoDefaultRuleConfiguredTest()
      throws PromiseEngineException {
    SourcingAttributeValuesInfo sourcingAttributeValuesInfo = new SourcingAttributeValuesInfo();
    sourcingAttributeValuesInfo.setRequiredAttributesValue(TestUtil.SOURCING_RULE);
    sourcingAttributeValuesInfo.setOptionalAttributesValue("V3:V4");

    FetchSourcingRulesRequest fetchSourcingRulesRequest =
        testUtil.fetchSourcingRulesRequest(sourcingAttributeValuesInfo);
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(new ArrayList<>());

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingRulesConfigurationService.processGetSourcingRules(fetchSourcingRulesRequest);
            });

    assertEquals("Default sourcing rules not configured", ex.getMessage());

    verify(sourcingRulesConfigurationPersistenceService, times(2))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
            anyString(), anyLong(), anyString());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processFetchSourcingRulesExtraReqAttributesTest() throws PromiseEngineException {
    SourcingAttributeValuesInfo sourcingAttributeValuesInfo = new SourcingAttributeValuesInfo();
    sourcingAttributeValuesInfo.setRequiredAttributesValue("V1:V2:V4");
    sourcingAttributeValuesInfo.setOptionalAttributesValue("V3");

    FetchSourcingRulesRequest fetchSourcingRulesRequest =
        testUtil.fetchSourcingRulesRequest(sourcingAttributeValuesInfo);

    List<SourcingRulesConfigurationDomainDto> sourcingRulesConfigurationEntityList =
        new ArrayList<>();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity1 =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity1.setSourcingRule("V1:V2:V4");
    sourcingRulesConfigurationEntityList.add(sourcingRulesConfigurationEntity1);
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(sourcingRulesConfigurationEntityList);

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingRulesConfigurationService.processGetSourcingRules(fetchSourcingRulesRequest);
            });

    assertEquals(
        "Can't fetch the sourcing rule as required attributes values do not match the sourcing attribute definition.",
        ex.getMessage());

    verify(sourcingRulesConfigurationPersistenceService, times(0))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
            anyString(), anyLong(), anyString());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  @DisplayName("Happy path for processGetAllSourcingRuleDetailsByOrgId")
  void processGetAllSourcingRuleDetailsByOrgIdTest()
      throws PromiseEngineException, CommonServiceException {
    String rule1 = "Test rule 1";

    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity.setSourcingRuleName(rule1);
    sourcingRulesConfigurationEntity.setSourcingRule("V1:V2:O1:O2");

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");

    var attributesDefinition = testUtil.getSourcingAttributeDefinitionUIResponse();
    attributesDefinition.setSourcingAttributesDefinitionId("1");

    when(sourcingRuleDetailsPersistenceService.getAllSourcingRuleByOrgId(anyString()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleById(anyLong()))
        .thenReturn(Optional.of(sourcingRulesConfigurationEntity));
    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            anyString(), any()))
        .thenReturn(sourcingAttributesDefinitionResponse);
    when(sourcingAttributesDefinitionService.processGetActiveSourcingAttributeDefinitionForUI(
            anyString(), any()))
        .thenReturn(attributesDefinition);

    AttributeValuesDomainDto attributeValuesEntity1 =
        testUtil.getAttributeValuesEntity1(1L, 3L, "O1");
    AttributeValuesDomainDto attributeValuesEntity2 =
        testUtil.getAttributeValuesEntity1(2L, 4L, "O2");
    AttributeValuesDomainDto attributeValuesEntity3 =
        testUtil.getAttributeValuesEntity1(2L, 4L, "O3");

    when(attributeValuesPersistenceService.getAllAttributeValues(any()))
        .thenReturn(
            List.of(attributeValuesEntity1, attributeValuesEntity2, attributeValuesEntity3));

    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("1")
                          .attributeName("R1")
                          .attributeValue("r1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("2")
                          .attributeName("R2")
                          .attributeValue("r2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getRequiredAttributeDetails(any(), any(List.class), any(), any(), any());
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("3")
                          .attributeName("O1")
                          .attributeValue("o1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("4")
                          .attributeName("O2")
                          .attributeValue("o2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getOptionalAttributeDetails(any(), any(List.class), any(), any(), any(), any());

    List<AllSourcingRulesResponse> response =
        sourcingRulesConfigurationService.processGetAllSourcingRuleDetailsByOrgId(TestUtil.ORG_ID);
    assertNotNull(response);
    assertEquals(rule1, response.get(0).getSourcingRuleName());
    assertEquals(2, response.get(0).getRequiredAttributes().size());
    assertEquals(2, response.get(0).getOptionalAttributes().size());
    assertEquals(1, response.get(0).getNodes().size());
  }

  @Test
  void processGetAllSourcingRuleDetailsByOrgIdWithNoOptionalAttrTest()
      throws PromiseEngineException, CommonServiceException {
    String rule1 = "Test rule 1";

    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity.setSourcingRuleName(rule1);
    sourcingRulesConfigurationEntity.setSourcingRule("V1:V2");

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);

    when(sourcingRuleDetailsPersistenceService.getAllSourcingRuleByOrgId(anyString()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleById(anyLong()))
        .thenReturn(Optional.of(sourcingRulesConfigurationEntity));
    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            anyString(), any()))
        .thenReturn(sourcingAttributesDefinitionResponse);

    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("1")
                          .attributeName("R1")
                          .attributeValue("r1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("2")
                          .attributeName("R2")
                          .attributeValue("r2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getRequiredAttributeDetails(any(), any(List.class), any(), any(), any());
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("3")
                          .attributeName("O1")
                          .attributeValue("o1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("4")
                          .attributeName("O2")
                          .attributeValue("o2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getOptionalAttributeDetails(any(), any(List.class), any(), any(), any(), any());

    List<AllSourcingRulesResponse> response =
        sourcingRulesConfigurationService.processGetAllSourcingRuleDetailsByOrgId(TestUtil.ORG_ID);

    assertNotNull(response);
    assertEquals(rule1, response.get(0).getSourcingRuleName());
    assertEquals(2, response.get(0).getRequiredAttributes().size());
    assertEquals(0, response.get(0).getOptionalAttributes().size());
    assertEquals(1, response.get(0).getNodes().size());
  }

  @Test
  @DisplayName("Negative test for inactive sourcingAttributesDefinitionId")
  void processGetAllSourcingRuleDetailsByOrgIdWhenInactiveSourcingAttributesDefinitionTest()
      throws PromiseEngineException, CommonServiceException {
    String rule1 = "Negative test";

    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity.setSourcingRuleName(rule1);

    var attributesDefinition = testUtil.getSourcingAttributeDefinitionUIResponse();
    attributesDefinition.setSourcingAttributesDefinitionId("2");

    when(sourcingRuleDetailsPersistenceService.getAllSourcingRuleByOrgId(anyString()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleById(anyLong()))
        .thenReturn(Optional.of(sourcingRulesConfigurationEntity));
    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);

    sourcingAttributesDefinitionEntity.setId(2L);

    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                anyString(), any(), any()))
        .thenReturn(List.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));

    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionResponse.setId(2L);
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            anyString(), any()))
        .thenReturn(sourcingAttributesDefinitionResponse);
    when(sourcingAttributesDefinitionService.processGetActiveSourcingAttributeDefinitionForUI(
            anyString(), any()))
        .thenReturn(attributesDefinition);

    List<AllSourcingRulesResponse> response =
        sourcingRulesConfigurationService.processGetAllSourcingRuleDetailsByOrgId(TestUtil.ORG_ID);

    assertEquals(0, response.size());
  }

  @Test
  void processGetAllSourcingRuleDetailsByOrgIdWithOptionalAttrAsBlankTest()
      throws PromiseEngineException, CommonServiceException {
    String rule1 = "Test rule 1";

    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity.setSourcingRuleName(rule1);
    sourcingRulesConfigurationEntity.setSourcingRule("V1:V2");

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("");

    when(sourcingRuleDetailsPersistenceService.getAllSourcingRuleByOrgId(anyString()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleById(anyLong()))
        .thenReturn(Optional.of(sourcingRulesConfigurationEntity));
    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            anyString(), any()))
        .thenReturn(sourcingAttributesDefinitionResponse);
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("1")
                          .attributeName("R1")
                          .attributeValue("r1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("2")
                          .attributeName("R2")
                          .attributeValue("r2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getRequiredAttributeDetails(any(), any(List.class), any(), any(), any());
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("3")
                          .attributeName("O1")
                          .attributeValue("o1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("4")
                          .attributeName("O2")
                          .attributeValue("o2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getOptionalAttributeDetails(any(), any(List.class), any(), any(), any(), any());

    List<AllSourcingRulesResponse> response =
        sourcingRulesConfigurationService.processGetAllSourcingRuleDetailsByOrgId(TestUtil.ORG_ID);
    assertNotNull(response);
    assertEquals(rule1, response.get(0).getSourcingRuleName());
    assertEquals(2, response.get(0).getRequiredAttributes().size());
    assertEquals(0, response.get(0).getOptionalAttributes().size());
    assertEquals(1, response.get(0).getNodes().size());
  }

  @Test
  void processGetAllSourcingRuleDetailsByOrgIdWhenOneOfTheOptAttrIsBlankTest()
      throws PromiseEngineException, CommonServiceException {
    String rule1 = "Test rule 1";

    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity.setSourcingRuleName(rule1);
    sourcingRulesConfigurationEntity.setSourcingRule("V1:V2:O2");

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");

    when(sourcingRuleDetailsPersistenceService.getAllSourcingRuleByOrgId(anyString()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleById(anyLong()))
        .thenReturn(Optional.of(sourcingRulesConfigurationEntity));
    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            anyString(), any()))
        .thenReturn(sourcingAttributesDefinitionResponse);

    AttributeValuesDomainDto attributeValuesEntity1 =
        testUtil.getAttributeValuesEntity1(1L, 3L, "O1");
    AttributeValuesDomainDto attributeValuesEntity2 =
        testUtil.getAttributeValuesEntity1(2L, 4L, "O2");
    AttributeValuesDomainDto attributeValuesEntity3 =
        testUtil.getAttributeValuesEntity1(2L, 4L, "O3");

    when(attributeValuesPersistenceService.getAllAttributeValues(any()))
        .thenReturn(
            List.of(attributeValuesEntity1, attributeValuesEntity2, attributeValuesEntity3));
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("1")
                          .attributeName("R1")
                          .attributeValue("r1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("2")
                          .attributeName("R2")
                          .attributeValue("r2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getRequiredAttributeDetails(any(), any(List.class), any(), any(), any());
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("3")
                          .attributeName("O1")
                          .attributeValue("")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("4")
                          .attributeName("O2")
                          .attributeValue("o2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getOptionalAttributeDetails(any(), any(List.class), any(), any(), any(), any());

    List<AllSourcingRulesResponse> response =
        sourcingRulesConfigurationService.processGetAllSourcingRuleDetailsByOrgId(TestUtil.ORG_ID);
    assertNotNull(response);
    assertEquals(rule1, response.get(0).getSourcingRuleName());
    assertEquals(2, response.get(0).getRequiredAttributes().size());
    assertEquals(2, response.get(0).getOptionalAttributes().size());
    assertEquals(1, response.get(0).getNodes().size());
    assertEquals("", response.get(0).getOptionalAttributes().get(0).getAttributeValue());
  }

  @Test
  void processGetAllSourcingRuleDetailsByOrgIdWhenOneOfAttrValuesIsNullTest()
      throws PromiseEngineException, CommonServiceException {
    String rule1 = "Test rule 1";

    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity.setSourcingRuleName(rule1);
    sourcingRulesConfigurationEntity.setSourcingRule("V1:V2:O1:O2");

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");

    when(sourcingRuleDetailsPersistenceService.getAllSourcingRuleByOrgId(anyString()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleById(anyLong()))
        .thenReturn(Optional.of(sourcingRulesConfigurationEntity));
    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            anyString(), any()))
        .thenReturn(sourcingAttributesDefinitionResponse);

    AttributeValuesDomainDto attributeValuesEntity2 =
        testUtil.getAttributeValuesEntity1(2L, 4L, "O2");
    AttributeValuesDomainDto attributeValuesEntity3 =
        testUtil.getAttributeValuesEntity1(2L, 4L, "O3");

    when(attributeValuesPersistenceService.getAllAttributeValues(any()))
        .thenReturn(List.of(attributeValuesEntity2, attributeValuesEntity3));
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("1")
                          .attributeName("R1")
                          .attributeValue("r1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("2")
                          .attributeName("R2")
                          .attributeValue("r2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getRequiredAttributeDetails(any(), any(List.class), any(), any(), any());
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(AttributeInfo.builder().attributeId("3").attributeName("O1").build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("4")
                          .attributeName("O2")
                          .attributeValue("o2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getOptionalAttributeDetails(any(), any(List.class), any(), any(), any(), any());

    List<AllSourcingRulesResponse> response =
        sourcingRulesConfigurationService.processGetAllSourcingRuleDetailsByOrgId(TestUtil.ORG_ID);
    assertNotNull(response);
    assertEquals(rule1, response.get(0).getSourcingRuleName());
    assertEquals(2, response.get(0).getRequiredAttributes().size());
    assertEquals(2, response.get(0).getOptionalAttributes().size());
    assertEquals(1, response.get(0).getNodes().size());
    assertNull(response.get(0).getOptionalAttributes().get(0).getAttributeValue());
  }

  @Test
  void processGetAllSourcingRuleDetailsByOrgIdWhenBothOptAttrsAreBlankTest()
      throws PromiseEngineException, CommonServiceException {
    String rule1 = "Test rule 1";

    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity.setSourcingRuleName(rule1);
    sourcingRulesConfigurationEntity.setSourcingRule("V1:V2");

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");

    when(sourcingRuleDetailsPersistenceService.getAllSourcingRuleByOrgId(anyString()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleById(anyLong()))
        .thenReturn(Optional.of(sourcingRulesConfigurationEntity));
    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            anyString(), any()))
        .thenReturn(sourcingAttributesDefinitionResponse);

    AttributeValuesDomainDto attributeValuesEntity1 =
        testUtil.getAttributeValuesEntity1(1L, 3L, "O5");
    AttributeValuesDomainDto attributeValuesEntity2 =
        testUtil.getAttributeValuesEntity1(2L, 4L, "O6");
    AttributeValuesDomainDto attributeValuesEntity3 =
        testUtil.getAttributeValuesEntity1(2L, 4L, "O7");

    when(attributeValuesPersistenceService.getAllAttributeValues(any()))
        .thenReturn(
            List.of(attributeValuesEntity1, attributeValuesEntity2, attributeValuesEntity3));
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("1")
                          .attributeName("R1")
                          .attributeValue("r1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("2")
                          .attributeName("R2")
                          .attributeValue("r2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getRequiredAttributeDetails(any(), any(List.class), any(), any(), any());
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("3")
                          .attributeName("O1")
                          .attributeValue("")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("4")
                          .attributeName("O2")
                          .attributeValue("")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getOptionalAttributeDetails(any(), any(List.class), any(), any(), any(), any());
    List<AllSourcingRulesResponse> response =
        sourcingRulesConfigurationService.processGetAllSourcingRuleDetailsByOrgId(TestUtil.ORG_ID);
    assertNotNull(response);
    assertEquals(rule1, response.get(0).getSourcingRuleName());
    assertEquals(2, response.get(0).getRequiredAttributes().size());
    assertEquals(2, response.get(0).getOptionalAttributes().size());
    assertEquals(1, response.get(0).getNodes().size());
    assertEquals("", response.get(0).getOptionalAttributes().get(0).getAttributeValue());
    assertEquals("", response.get(0).getOptionalAttributes().get(1).getAttributeValue());
  }

  @Test
  @DisplayName("Sourcing attributes definition not found for given sourcingAttributesDefinitionId")
  void processGetAllSourcingRuleDetailsByOrgIdTest2()
      throws PromiseEngineException, CommonServiceException {
    String rule1 = "Test rule 1";
    String rule2 = "Test rule 2";
    List<SourcingRulesConfigurationDomainDto> sourcingRulesConfigurationEntityList =
        new ArrayList<>();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity1 =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity1.setSourcingRule("V1:V2:V3");
    sourcingRulesConfigurationEntity.setSourcingRuleName(rule1);
    sourcingRulesConfigurationEntity1.setSourcingRuleName(rule2);
    sourcingRulesConfigurationEntityList.add(sourcingRulesConfigurationEntity);
    sourcingRulesConfigurationEntityList.add(sourcingRulesConfigurationEntity1);

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");
    when(sourcingRuleDetailsPersistenceService.getAllSourcingRuleByOrgId(anyString()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleById(anyLong()))
        .thenReturn(Optional.of(sourcingRulesConfigurationEntity));
    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(Optional.empty());
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            anyString(), any()))
        .thenReturn(sourcingAttributesDefinitionResponse);

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              sourcingRulesConfigurationService.processGetAllSourcingRuleDetailsByOrgId(
                  TestUtil.ORG_ID);
            });
    assertEquals("Sourcing attributes definition not found", ex.getMessage());
  }

  @Test
  @DisplayName("Required attribute not found in sourcing attributes definition")
  void processGetAllSourcingRuleDetailsByOrgIdTest3()
      throws PromiseEngineException, CommonServiceException {
    String rule1 = "Test rule 1";
    String rule2 = "Test rule 2";
    List<SourcingRulesConfigurationDomainDto> sourcingRulesConfigurationEntityList =
        new ArrayList<>();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity1 =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity1.setSourcingRule("V1:V2:V3");
    sourcingRulesConfigurationEntity.setSourcingRuleName(rule1);
    sourcingRulesConfigurationEntity1.setSourcingRuleName(rule2);
    sourcingRulesConfigurationEntityList.add(sourcingRulesConfigurationEntity);
    sourcingRulesConfigurationEntityList.add(sourcingRulesConfigurationEntity1);

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setReqAttributes(null);

    when(sourcingRuleDetailsPersistenceService.getAllSourcingRuleByOrgId(anyString()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleById(anyLong()))
        .thenReturn(Optional.of(sourcingRulesConfigurationEntity));
    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            anyString(), any()))
        .thenReturn(sourcingAttributesDefinitionResponse);
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              sourcingRulesConfigurationService.processGetAllSourcingRuleDetailsByOrgId(
                  TestUtil.ORG_ID);
            });
    assertEquals("Required attribute not found in sourcing attributes definition", ex.getMessage());
  }

  @Test
  @DisplayName("Happy path for processDeleteMultipleSourcingRuleDetails")
  void processDeleteMultipleSourcingRuleDetailsTest()
      throws PromiseEngineException, CommonServiceException {
    SourcingRulesConfigurationDomainDto rulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();

    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            TestUtil.SOURCING_RULE_ID, TestUtil.ORG_ID))
        .thenReturn(Optional.of(rulesConfigurationEntity));
    when(sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));

    doNothing()
        .when(sourcingRulesConfigurationPersistenceService)
        .deleteMultipleSourcingRules(anyList());
    doNothing()
        .when(sourcingRuleDetailsPersistenceService)
        .deleteMultipleSourcingRuleDetails(anyList());

    SourcingRuleIdRequest sourcingRuleIdRequest =
        SourcingRuleIdRequest.builder().sourcingRuleIds(List.of(TestUtil.SOURCING_RULE_ID)).build();
    List<SourcingRuleDetails> response =
        sourcingRulesConfigurationService.processDeleteMultipleSourcingRuleDetails(
            TestUtil.ORG_ID, sourcingRuleIdRequest);

    assertEquals(TestUtil.SOURCING_RULE, response.get(0).getSourcingRule());
    assertEquals(TestUtil.ORG_ID, response.get(0).getOrgId());

    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .getSourcingRuleByIdAndOrgId(anyLong(), anyString());
    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .deleteMultipleSourcingRules(anyList());
  }

  @Test
  @DisplayName("Exception in processDeleteMultipleSourcingRuleDetails when sourcing rule not found")
  void processDeleteMultipleSourcingRuleDetailsExceptionTest1() throws PromiseEngineException {

    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByOrgIdAndSourcingRule(
            TestUtil.ORG_ID, TestUtil.SOURCING_RULE))
        .thenReturn(Optional.of(new ArrayList<>()));

    SourcingRuleIdRequest sourcingRuleIdRequest =
        SourcingRuleIdRequest.builder().sourcingRuleIds(List.of(TestUtil.SOURCING_RULE_ID)).build();
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.processDeleteMultipleSourcingRuleDetails(
                    TestUtil.ORG_ID, sourcingRuleIdRequest));

    assertTrue(ex.getMessage().contains("Sourcing rule not found"));

    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .getSourcingRuleByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processGetSourcingRuleDetailsByOrgIdAndSourcingRuleTest()
      throws PromiseEngineException, CommonServiceException {
    String rule1 = "Test rule 1";
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity.setSourcingRuleName(rule1);
    sourcingRulesConfigurationEntity.setSourcingRule("V1:V2:V3");

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");

    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            anyString(), any()))
        .thenReturn(sourcingAttributesDefinitionResponse);

    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.of(sourcingRulesConfigurationEntity));
    when(sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(anyString(), anyLong()))
        .thenReturn(testUtil.getSourcingRuleDetailsEntityList());

    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));

    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));

    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            anyString(), any()))
        .thenReturn(sourcingAttributesDefinitionResponse);

    AttributeValuesDomainDto attributeValuesEntity1 =
        testUtil.getAttributeValuesEntity1(1L, 3L, "O1");
    AttributeValuesDomainDto attributeValuesEntity2 =
        testUtil.getAttributeValuesEntity1(2L, 4L, "O2");
    when(attributeValuesPersistenceService.getAllAttributeValues(any()))
        .thenReturn(List.of(attributeValuesEntity1, attributeValuesEntity2));
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("1")
                          .attributeName("R1")
                          .attributeValue("r1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("2")
                          .attributeName("R2")
                          .attributeValue("r2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getRequiredAttributeDetails(any(), any(List.class), any(), any(), any());
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("3")
                          .attributeName("O1")
                          .attributeValue("o1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("4")
                          .attributeName("O2")
                          .attributeValue("o2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getOptionalAttributeDetails(any(), any(List.class), any(), any(), any(), any());
    AllSourcingRulesResponse response =
        sourcingRulesConfigurationService.processGetSourcingRuleDetailsByOrgIdAndSourcingRule(
            TestUtil.ORG_ID, TestUtil.SOURCING_RULE_ID);

    assertNotNull(response);
    assertEquals(rule1, response.getSourcingRuleName());
    assertEquals(2, response.getRequiredAttributes().size());
    assertEquals(2, response.getOptionalAttributes().size());
    assertEquals(1, response.getNodes().get(0).getSequence());
    assertEquals(2, response.getNodes().size());
  }

  @Test
  @DisplayName("Get Sourcing rules with missing optional values")
  void processGetSourcingRuleDetailsByOrgIdAndSourcingRuleWithMissingOptionalTest()
      throws PromiseEngineException, CommonServiceException {
    String rule1 = "Test rule 1";
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationEntity.setSourcingRuleName(rule1);
    sourcingRulesConfigurationEntity.setSourcingRule("V1:V2::O2");

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");

    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            anyString(), any()))
        .thenReturn(sourcingAttributesDefinitionResponse);

    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.of(sourcingRulesConfigurationEntity));
    when(sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(anyString(), anyLong()))
        .thenReturn(testUtil.getSourcingRuleDetailsEntityList());

    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));

    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getNodePriorityEntity()));

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));

    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));

    AttributeValuesDomainDto attributeValuesEntity1 =
        testUtil.getAttributeValuesEntity1(1L, 3L, "O1");
    AttributeValuesDomainDto attributeValuesEntity2 =
        testUtil.getAttributeValuesEntity1(2L, 4L, "O2");
    when(attributeValuesPersistenceService.getAllAttributeValues(any()))
        .thenReturn(List.of(attributeValuesEntity1, attributeValuesEntity2));
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  List<AttributeInfo> arg = invocation.getArgument(1);
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("1")
                          .attributeName("R1")
                          .attributeValue("V1")
                          .build());
                  arg.add(
                      AttributeInfo.builder()
                          .attributeId("2")
                          .attributeName("R2")
                          .attributeValue("V2")
                          .build());
                  return null;
                })
        .when(fetchRulesUtil)
        .getRequiredAttributeDetails(any(), any(List.class), any(), any(), any());
    doAnswer((Answer<Void>) invocation -> null)
        .when(fetchRulesUtil)
        .getOptionalAttributeDetails(any(), any(List.class), any(), any(), any(), any());
    AllSourcingRulesResponse response =
        sourcingRulesConfigurationService.processGetSourcingRuleDetailsByOrgIdAndSourcingRule(
            TestUtil.ORG_ID, TestUtil.SOURCING_RULE_ID);

    assertNotNull(response);
    assertEquals(rule1, response.getSourcingRuleName());
    assertEquals(2, response.getRequiredAttributes().size());
    assertEquals(0, response.getOptionalAttributes().size());
    assertEquals("V1", response.getRequiredAttributes().getFirst().getAttributeValue());
    assertEquals("V2", response.getRequiredAttributes().getLast().getAttributeValue());
    assertEquals(0, response.getOptionalAttributes().size());
    assertEquals(1, response.getNodes().get(0).getSequence());
    assertEquals(2, response.getNodes().size());
  }

  @Test
  @DisplayName("When sourcing rule not found with orgId and sourcingRuleId")
  void processGetSourcingRuleDetailsByOrgIdAndSourcingRuleTest1() throws PromiseEngineException {
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () -> {
              sourcingRulesConfigurationService.processGetSourcingRuleDetailsByOrgIdAndSourcingRule(
                  TestUtil.ORG_ID, TestUtil.SOURCING_RULE_ID);
            });

    assertEquals(TestUtil.SOURCING_RULE_EXCEPTION_MESSAGE, exception.getMessage());
  }

  @Test
  @DisplayName("Happy path for createSourcingRule")
  void createSourcingRuleTest() throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionResponse.setOptAttributes("3");
    NodeGroupDomainDto entity = new NodeGroupDomainDto();
    entity.setId(2L);
    entity.setOrgId("ABC");
    entity.setNodeGroupName("Group2");

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3");
    when(nodeGroupPersistenceService.fetchNodeGroupById(any())).thenReturn(Optional.of(entity));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(any(), eq(0L)))
        .thenReturn(testUtil.getNodePriorityEntityList());
    List<NodePriorityDomainDto> nodePriorityEntityList = testUtil.getNodePriorityEntityList();
    nodePriorityEntityList.get(0).setNodeId("Node-2");
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(any(), eq(1L)))
        .thenReturn(nodePriorityEntityList);

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(sourcingAttributesDefinitionResponse);
    when(sourcingRulesConfigurationPersistenceService.saveSourcingRule(any()))
        .thenReturn(new SourcingRulesConfigurationDomainDto());
    when(sourcingRuleDetailsPersistenceService.saveSourcingNodes(any()))
        .thenReturn(testUtil.getSourcingRuleDetailsEntity());
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));
    AllSourcingRulesResponse createRequest = testUtil.getCreateSourcingRuleRequest();
    AllSourcingRulesResponse response =
        sourcingRulesConfigurationService.createSourcingRule(TestUtil.ORG_ID, createRequest);
    Assertions.assertNotNull(response);

    verify(nodeGroupPersistenceService, times(2)).fetchNodeGroupById(anyLong());
    verify(nodePriorityPersistenceService, times(2))
        .fetchNodePriorityListByOrgIdAndNodeGroupId(any(), any());
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), anyString());
  }

  @Test
  @DisplayName("Happy path for createSourcingRule with missing optional attribute")
  void createSourcingRuleWithEmptyOptionalAttributesTest()
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionResponse.setOptAttributes("3");
    NodeGroupDomainDto entity = new NodeGroupDomainDto();
    entity.setId(2L);
    entity.setOrgId("ABC");
    entity.setNodeGroupName("Group2");

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3");
    when(nodeGroupPersistenceService.fetchNodeGroupById(any())).thenReturn(Optional.of(entity));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(any(), eq(0L)))
        .thenReturn(testUtil.getNodePriorityEntityList());
    List<NodePriorityDomainDto> nodePriorityEntityList = testUtil.getNodePriorityEntityList();
    nodePriorityEntityList.get(0).setNodeId("Node-2");
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(any(), eq(1L)))
        .thenReturn(nodePriorityEntityList);

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(sourcingAttributesDefinitionResponse);
    when(sourcingRulesConfigurationPersistenceService.saveSourcingRule(any()))
        .thenReturn(new SourcingRulesConfigurationDomainDto());
    when(sourcingRuleDetailsPersistenceService.saveSourcingNodes(any()))
        .thenReturn(testUtil.getSourcingRuleDetailsEntity());
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));
    AllSourcingRulesResponse createRequest = testUtil.getCreateSourcingRuleRequest();
    AllSourcingRulesResponse response =
        sourcingRulesConfigurationService.createSourcingRule(TestUtil.ORG_ID, createRequest);
    Assertions.assertNotNull(response);

    verify(nodeGroupPersistenceService, times(2)).fetchNodeGroupById(anyLong());
    verify(nodePriorityPersistenceService, times(2))
        .fetchNodePriorityListByOrgIdAndNodeGroupId(any(), any());
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), anyString());
  }

  @Test
  @DisplayName("createSourcingRule: Sourcing attribute definition INACTIVE")
  void createSourcingRuleTest2() throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(
            testUtil.getSourcingRuleAttributesDefinitionResponse(
                SourcingAttributesDefinitionStatus.INACTIVE));
    AllSourcingRulesResponse createRequest = testUtil.getCreateSourcingRuleRequest();
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              sourcingRulesConfigurationService.createSourcingRule(TestUtil.ORG_ID, createRequest);
            });
    Assertions.assertEquals("Sourcing attribute definition is not active", e.getMessage());
  }

  @Test
  @DisplayName("createSourcingRule: Optional attribute invalid")
  void createSourcingRuleEmptyNodesExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionResponse.setOptAttributes("4,5");
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(sourcingAttributesDefinitionResponse);
    AllSourcingRulesResponse createRequest = testUtil.getCreateSourcingRuleRequest();
    createRequest.setNodes(List.of());
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              sourcingRulesConfigurationService.createSourcingRule(TestUtil.ORG_ID, createRequest);
            });
    Assertions.assertEquals("No node/node group have been added", e.getMessage());
  }

  @Test
  @DisplayName("Happy path for createSourcingRule when optional attribute value is not present")
  void createSourcingRuleTest5() throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionResponse.setOptAttributes("3");
    NodeGroupDomainDto entity = new NodeGroupDomainDto();
    entity.setId(2L);
    entity.setOrgId("ABC");
    entity.setNodeGroupName("Group2");

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3");
    when(nodeGroupPersistenceService.fetchNodeGroupById(any())).thenReturn(Optional.of(entity));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(any(), any()))
        .thenReturn(testUtil.getNodePriorityEntityList());
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(any(), eq(0L)))
        .thenReturn(testUtil.getNodePriorityEntityList());
    List<NodePriorityDomainDto> nodePriorityEntityList = testUtil.getNodePriorityEntityList();
    nodePriorityEntityList.get(0).setNodeId("Node-2");
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(any(), eq(1L)))
        .thenReturn(nodePriorityEntityList);

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(sourcingAttributesDefinitionResponse);
    when(sourcingRulesConfigurationPersistenceService.saveSourcingRule(any()))
        .thenReturn(new SourcingRulesConfigurationDomainDto());
    when(sourcingRuleDetailsPersistenceService.saveSourcingNodes(any()))
        .thenReturn(testUtil.getSourcingRuleDetailsEntity());
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));
    AllSourcingRulesResponse createRequest = testUtil.getCreateSourcingRuleRequest();
    createRequest.getOptionalAttributes().get(0).setAttributeValue(null);
    AllSourcingRulesResponse response =
        sourcingRulesConfigurationService.createSourcingRule(TestUtil.ORG_ID, createRequest);
    Assertions.assertNotNull(response);
    Assertions.assertEquals("SDND:T2P:", response.getSourcingRule());

    verify(nodeGroupPersistenceService, times(2)).fetchNodeGroupById(anyLong());
    verify(nodePriorityPersistenceService, times(2))
        .fetchNodePriorityListByOrgIdAndNodeGroupId(any(), any());
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), anyString());
  }

  @Test
  void createSourcingRuleWhenOptionalAttributesAreEmptyTest()
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    NodeGroupDomainDto entity = new NodeGroupDomainDto();
    entity.setId(2L);
    entity.setOrgId("ABC");
    entity.setNodeGroupName("Group2");

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3");
    when(nodeGroupPersistenceService.fetchNodeGroupById(any())).thenReturn(Optional.of(entity));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(any(), any()))
        .thenReturn(testUtil.getNodePriorityEntityList());
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(any(), eq(0L)))
        .thenReturn(testUtil.getNodePriorityEntityList());
    List<NodePriorityDomainDto> nodePriorityEntityList = testUtil.getNodePriorityEntityList();
    nodePriorityEntityList.get(0).setNodeId("Node-2");
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(any(), eq(1L)))
        .thenReturn(nodePriorityEntityList);

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(sourcingAttributesDefinitionResponse);
    when(sourcingRulesConfigurationPersistenceService.saveSourcingRule(any()))
        .thenReturn(new SourcingRulesConfigurationDomainDto());
    when(sourcingRuleDetailsPersistenceService.saveSourcingNodes(any()))
        .thenReturn(testUtil.getSourcingRuleDetailsEntity());
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));
    AllSourcingRulesResponse createRequest = testUtil.getCreateSourcingRuleRequest();
    createRequest.setOptionalAttributes(new ArrayList<>());
    AllSourcingRulesResponse response =
        sourcingRulesConfigurationService.createSourcingRule(TestUtil.ORG_ID, createRequest);
    Assertions.assertNotNull(response);
    Assertions.assertEquals("SDND:T2P", response.getSourcingRule());

    verify(nodeGroupPersistenceService, times(2)).fetchNodeGroupById(anyLong());
    verify(nodePriorityPersistenceService, times(2))
        .fetchNodePriorityListByOrgIdAndNodeGroupId(any(), any());
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), anyString());
  }

  @Test
  void createSourcingRuleSimilarNodeNoExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionResponse.setOptAttributes("3");
    NodeGroupDomainDto entity = new NodeGroupDomainDto();
    entity.setId(2L);
    entity.setOrgId("ABC");
    entity.setNodeGroupName("Group2");

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setOptAttributes("3");
    when(nodeGroupPersistenceService.fetchNodeGroupById(any())).thenReturn(Optional.of(entity));
    when(nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(any(), any()))
        .thenReturn(testUtil.getNodePriorityEntityList());
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(sourcingAttributesDefinitionResponse);
    when(sourcingRulesConfigurationPersistenceService.saveSourcingRule(any()))
        .thenReturn(new SourcingRulesConfigurationDomainDto());
    when(sourcingRuleDetailsPersistenceService.saveSourcingNodes(any()))
        .thenReturn(testUtil.getSourcingRuleDetailsEntity());
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));

    AllSourcingRulesResponse createRequest = testUtil.getCreateSourcingRuleRequest();
    createRequest.getOptionalAttributes().get(0).setAttributeValue(null);

    AllSourcingRulesResponse response =
        assertDoesNotThrow(
            () ->
                sourcingRulesConfigurationService.createSourcingRule(
                    TestUtil.ORG_ID, createRequest));
    assertNotNull(response);
    assertEquals(createRequest.getOrgId(), response.getOrgId());
    assertEquals(createRequest.getRequiredAttributes(), response.getRequiredAttributes());
    verify(nodeGroupPersistenceService, times(2)).fetchNodeGroupById(anyLong());
    verify(nodePriorityPersistenceService, times(2))
        .fetchNodePriorityListByOrgIdAndNodeGroupId(any(), any());
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), anyString());
  }

  @Test
  void editSourcingRuleConfigurationExceptionTest() throws PromiseEngineException {
    UpdateSourcingRuleRequest sourcingRuleRequest = testUtil.getUpdateSourcingRuleRequest();

    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingRulesConfigurationService.editSourcingRuleConfiguration(
                  TestUtil.ORG_ID, sourcingRuleRequest);
            });
    assertEquals("Unable to find sourcing rule.", exception.getMessage());
  }

  @Test
  void editSourcingRuleConfigurationTest() throws PromiseEngineException, CommonServiceException {
    UpdateSourcingRuleRequest sourcingRuleRequest = testUtil.getUpdateSourcingRuleRequest();
    List<AttributeInfo> requiredAttributeList = testUtil.getAttributeInfo();
    requiredAttributeList.get(0).setAttributeValue("EXPRESS");
    sourcingRuleRequest.setRequiredAttributes(requiredAttributeList);

    List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsEntities =
        testUtil.getSourcingRuleConfigurations();
    sourcingRuleDetailsEntities.get(0).setNodeGroups("1");
    sourcingRuleDetailsEntities.get(1).setNodeGroups("2");
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    when(sourcingRuleDetailsPersistenceService.saveAll(anyList()))
        .thenReturn(sourcingRuleDetailsEntities);

    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of());

    UpdateSourcingRuleConfigurationResponse updateSourcingRuleConfigurationResponse =
        sourcingRulesConfigurationService.editSourcingRuleConfiguration(
            TestUtil.ORG_ID, sourcingRuleRequest);

    assertNotNull(updateSourcingRuleConfigurationResponse);
    assertEquals(2, updateSourcingRuleConfigurationResponse.getNodes().size());
    assertEquals(
        requiredAttributeList.size(),
        updateSourcingRuleConfigurationResponse.getRequiredAttributes().size());
    assertEquals(
        requiredAttributeList.get(0).getAttributeId(),
        updateSourcingRuleConfigurationResponse.getRequiredAttributes().get(0).getAttributeId());
  }

  @Test
  void editSourcingRuleConfigurationReqAndOptionalAttributesTest()
      throws PromiseEngineException, CommonServiceException {
    UpdateSourcingRuleRequest sourcingRuleRequest = testUtil.getUpdateSourcingRuleRequest();
    List<AttributeInfo> requiredAttributeList = testUtil.getAttributeInfo();
    requiredAttributeList.get(0).setAttributeValue("EXPRESS");
    sourcingRuleRequest.setRequiredAttributes(requiredAttributeList);

    List<AttributeInfo> optionalAttributeList = testUtil.getAttributeInfo();
    optionalAttributeList.get(0).setAttributeValue("EVERYDAY");
    optionalAttributeList.get(0).setAttributeName("productClass");
    optionalAttributeList.get(0).setAttributeId("2");

    sourcingRuleRequest.setRequiredAttributes(requiredAttributeList);
    sourcingRuleRequest.setOptionalAttributes(optionalAttributeList);

    List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsEntities =
        testUtil.getSourcingRuleConfigurations();
    sourcingRuleDetailsEntities.get(0).setNodeGroups("1");
    sourcingRuleDetailsEntities.get(1).setNodeGroups("2");
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of());

    when(sourcingRuleDetailsPersistenceService.saveAll(anyList()))
        .thenReturn(sourcingRuleDetailsEntities);

    when(nodeGroupPersistenceService.fetchNodeGroupById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNodeGroupEntity()));

    UpdateSourcingRuleConfigurationResponse updateSourcingRuleConfigurationResponse =
        sourcingRulesConfigurationService.editSourcingRuleConfiguration(
            TestUtil.ORG_ID, sourcingRuleRequest);

    assertNotNull(updateSourcingRuleConfigurationResponse);
    assertEquals(2, updateSourcingRuleConfigurationResponse.getNodes().size());
    assertEquals(
        requiredAttributeList.size(),
        updateSourcingRuleConfigurationResponse.getRequiredAttributes().size());
    assertEquals(
        requiredAttributeList.get(0).getAttributeId(),
        updateSourcingRuleConfigurationResponse.getRequiredAttributes().get(0).getAttributeId());
    assertEquals(
        optionalAttributeList.size(),
        updateSourcingRuleConfigurationResponse.getOptionalAttributes().size());
    assertEquals(
        optionalAttributeList.get(0).getAttributeId(),
        updateSourcingRuleConfigurationResponse.getOptionalAttributes().get(0).getAttributeId());
  }

  @Test
  @DisplayName("Edit sourcing rule with existing sourcing rule name - Exception test")
  void editSourcingRuleConfigurationWithExistingRuleNameExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    UpdateSourcingRuleRequest sourcingRuleRequest = testUtil.getUpdateSourcingRuleRequest();
    List<AttributeInfo> requiredAttributeList = testUtil.getAttributeInfo();
    requiredAttributeList.get(0).setAttributeValue("EXPRESS");
    sourcingRuleRequest.setRequiredAttributes(requiredAttributeList);

    List<AttributeInfo> optionalAttributeList = testUtil.getAttributeInfo();
    optionalAttributeList.get(0).setAttributeValue("EVERYDAY");
    optionalAttributeList.get(0).setAttributeName("productClass");
    optionalAttributeList.get(0).setAttributeId("2");

    sourcingRuleRequest.setRequiredAttributes(requiredAttributeList);
    sourcingRuleRequest.setOptionalAttributes(optionalAttributeList);

    List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsEntities =
        testUtil.getSourcingRuleConfigurations();
    sourcingRuleDetailsEntities.get(0).setNodeGroups("1");
    sourcingRuleDetailsEntities.get(1).setNodeGroups("2");
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));

    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
                anyString(), anyLong(), anyString()))
        .thenReturn(testUtil.getSourcingRulesEntity());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.editSourcingRuleConfiguration(
                    TestUtil.ORG_ID, sourcingRuleRequest));
    assertEquals("Sourcing Rule exists with given sourcingRuleName", ex.getMessage());

    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
            anyString(), anyLong(), anyString());
  }

  @Test
  @DisplayName("Edit sourcing rule with existing sourcing rule value - Exception test")
  void editSourcingRuleConfigurationWithExistingSourcingRuleExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    UpdateSourcingRuleRequest sourcingRuleRequest = testUtil.getUpdateSourcingRuleRequest();
    List<AttributeInfo> requiredAttributeList = testUtil.getAttributeInfo();
    requiredAttributeList.get(0).setAttributeValue("EXPRESS");
    sourcingRuleRequest.setRequiredAttributes(requiredAttributeList);

    List<AttributeInfo> optionalAttributeList = testUtil.getAttributeInfo();
    optionalAttributeList.get(0).setAttributeValue("EVERYDAY");
    optionalAttributeList.get(0).setAttributeName("productClass");
    optionalAttributeList.get(0).setAttributeId("2");

    sourcingRuleRequest.setRequiredAttributes(requiredAttributeList);
    sourcingRuleRequest.setOptionalAttributes(optionalAttributeList);

    List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsEntities =
        testUtil.getSourcingRuleConfigurations();
    sourcingRuleDetailsEntities.get(0).setNodeGroups("1");
    sourcingRuleDetailsEntities.get(1).setNodeGroups("2");
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));

    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of(testUtil.getSourcingRulesEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.editSourcingRuleConfiguration(
                    TestUtil.ORG_ID, sourcingRuleRequest));

    String errorMessage =
        "Can’t create/update sourcing rule as sourcing rule %s already has the same order attributes value"
            .formatted(testUtil.getSourcingRulesEntity().getSourcingRuleName());
    assertEquals(errorMessage, ex.getMessage());

    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
            anyString(), anyLong(), anyString());
  }

  @Test
  @DisplayName("Edit sourcing rule to existing value without changing sourcing rule name")
  void editSourcingRulesTest() throws PromiseEngineException {
    UpdateSourcingRuleRequest sourcingRuleRequest = testUtil.getUpdateSourcingRuleRequest();
    sourcingRuleRequest.setSourcingRuleName(SOURCING_RULE_NAME);
    List<AttributeInfo> requiredAttributeList = testUtil.getAttributeInfo();
    requiredAttributeList.get(0).setAttributeValue("EXPRESS");
    sourcingRuleRequest.setRequiredAttributes(requiredAttributeList);

    List<AttributeInfo> optionalAttributeList = testUtil.getAttributeInfo();
    optionalAttributeList.get(0).setAttributeValue("EVERYDAY");
    optionalAttributeList.get(0).setAttributeName("productClass");
    optionalAttributeList.get(0).setAttributeId("2");

    sourcingRuleRequest.setRequiredAttributes(requiredAttributeList);
    sourcingRuleRequest.setOptionalAttributes(optionalAttributeList);

    List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsEntities =
        testUtil.getSourcingRuleConfigurations();
    sourcingRuleDetailsEntities.get(0).setNodeGroups("1");
    sourcingRuleDetailsEntities.get(1).setNodeGroups("2");
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getSourcingRulesEntity()));
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of(testUtil.getSourcingRulesEntity2()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.editSourcingRuleConfiguration(
                    TestUtil.ORG_ID, sourcingRuleRequest));

    String errorMessage =
        "Can’t create/update sourcing rule as sourcing rule %s already has the same order attributes value"
            .formatted(testUtil.getSourcingRulesEntity2().getSourcingRuleName());
    assertEquals(errorMessage, ex.getMessage());

    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
            anyString(), anyLong(), anyString());
  }

  @Test
  @DisplayName("Edit sourcing rule with case sensitive sourcing rule name - Exception test")
  void editSourcingRuleConfigurationWithSameRuleNameExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto =
        testUtil.getSourcingRulesEntity();
    sourcingRulesConfigurationDomainDto.setSourcingRuleName("SOURCINGRULE1");
    UpdateSourcingRuleRequest sourcingRuleRequest = testUtil.getUpdateSourcingRuleRequest();
    List<AttributeInfo> requiredAttributeList = testUtil.getAttributeInfo();
    requiredAttributeList.get(0).setAttributeValue("EXPRESS");
    sourcingRuleRequest.setRequiredAttributes(requiredAttributeList);

    List<AttributeInfo> optionalAttributeList = testUtil.getAttributeInfo();
    optionalAttributeList.get(0).setAttributeValue("EVERYDAY");
    optionalAttributeList.get(0).setAttributeName("productClass");
    optionalAttributeList.get(0).setAttributeId("2");

    sourcingRuleRequest.setSourcingRuleName(SOURCING_RULE_NAME);
    sourcingRuleRequest.setRequiredAttributes(requiredAttributeList);
    sourcingRuleRequest.setOptionalAttributes(optionalAttributeList);

    List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsEntities =
        testUtil.getSourcingRuleConfigurations();
    sourcingRuleDetailsEntities.get(0).setNodeGroups("1");
    sourcingRuleDetailsEntities.get(1).setNodeGroups("2");
    when(sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.of(sourcingRulesConfigurationDomainDto));

    when(sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
                anyString(), anyLong(), anyString()))
        .thenReturn(sourcingRulesConfigurationDomainDto);

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingRulesConfigurationService.editSourcingRuleConfiguration(
                    TestUtil.ORG_ID, sourcingRuleRequest));
    assertEquals("Sourcing Rule exists with given sourcingRuleName", ex.getMessage());

    verify(sourcingRulesConfigurationPersistenceService, times(1))
        .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
            anyString(), anyLong(), anyString());
  }
}
