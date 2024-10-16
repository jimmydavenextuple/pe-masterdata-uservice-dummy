/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.GroupDefinitionRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionListResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionResponse;
import com.nextuple.promise.sourcing.rule.persistence.domain.GroupDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.NamedOptimizationStrategyDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.GroupDefinitionPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.NamedOptimizationStrategyPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributesDefinitionPersistenceService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GroupDefinitionServiceTest {
  @InjectMocks private GroupDefinitionService groupDefinitionService;

  @Mock private GroupDefinitionPersistenceService groupDefinitionDomain;
  @Mock private SourcingAttributesDefinitionPersistenceService sourcingAttributesDefinitionDomain;
  @Mock private NamedOptimizationStrategyPersistenceService namedOptimizationStrategyDomain;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void addGroupDefinitionTest() throws PromiseEngineException, CommonServiceException {
    GroupDefinitionRequest groupDefinitionRequest = testUtil.getGroupDefinitionRequest();

    when(groupDefinitionDomain
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of());
    when(groupDefinitionDomain.fetchGroupDefinitionListByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(groupDefinitionDomain.saveGroupDefinition(any(GroupDefinitionDomainDto.class)))
        .thenReturn(testUtil.getGroupDefinitionEntity());

    GroupDefinitionResponse groupDefinitionResponse =
        groupDefinitionService.processAddGroupDefinition(groupDefinitionRequest);
    assertEquals(testUtil.getGroupDefinitionEntity().getId(), groupDefinitionResponse.getId());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
            anyString(), anyLong(), anyString());
    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndName(anyString(), anyString());
    verify(sourcingAttributesDefinitionDomain, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
    verify(groupDefinitionDomain, times(1))
        .saveGroupDefinition(any(GroupDefinitionDomainDto.class));
  }

  @Test
  void addGroupDefinitionExceptionTest1() throws PromiseEngineException {

    when(groupDefinitionDomain
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of(testUtil.getGroupDefinitionEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              groupDefinitionService.processAddGroupDefinition(
                  testUtil.getGroupDefinitionRequest());
            });
    assertEquals(
        "Group already exist for given orgId , sourcingAttributesDefinitionId and reqAttributesValue",
        ex.getMessage());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
            anyString(), anyLong(), anyString());
  }

  @Test
  void addGroupDefinitionExceptionTest2() throws PromiseEngineException {

    when(groupDefinitionDomain
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of());
    when(groupDefinitionDomain.fetchGroupDefinitionListByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getGroupDefinitionEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              groupDefinitionService.processAddGroupDefinition(
                  testUtil.getGroupDefinitionRequest());
            });
    assertEquals("Combination of orgId and groupName should be unique", ex.getMessage());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
            anyString(), anyLong(), anyString());
    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndName(anyString(), anyString());
  }

  @Test
  void addGroupDefinitionExceptionTest3() throws PromiseEngineException {

    when(groupDefinitionDomain
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of());
    when(groupDefinitionDomain.fetchGroupDefinitionListByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.INACTIVE)));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              groupDefinitionService.processAddGroupDefinition(
                  testUtil.getGroupDefinitionRequest());
            });
    assertEquals(
        "Invalid sourcing attributes definition for OPTIMIZATION scope/ Sourcing  attributes definition exists but not in ACTIVE status",
        ex.getMessage());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
            anyString(), anyLong(), anyString());
    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndName(anyString(), anyString());
    verify(sourcingAttributesDefinitionDomain, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void addGroupDefinitionExceptionTest4() throws PromiseEngineException {

    when(groupDefinitionDomain
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of());
    when(groupDefinitionDomain.fetchGroupDefinitionListByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              groupDefinitionService.processAddGroupDefinition(
                  testUtil.getGroupDefinitionRequest());
            });
    assertEquals(
        "Invalid sourcing attributes definition for OPTIMIZATION scope/ Sourcing  attributes definition exists but not in ACTIVE status",
        ex.getMessage());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
            anyString(), anyLong(), anyString());
    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndName(anyString(), anyString());
    verify(sourcingAttributesDefinitionDomain, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void addGroupDefinitionExceptionTest5() throws PromiseEngineException {
    GroupDefinitionRequest groupDefinitionRequest = testUtil.getGroupDefinitionRequest();
    groupDefinitionRequest.setReqAttributesValue("V1");

    when(groupDefinitionDomain
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of());
    when(groupDefinitionDomain.fetchGroupDefinitionListByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              groupDefinitionService.processAddGroupDefinition(groupDefinitionRequest);
            });
    assertEquals(
        "Can't add the group definition as all the required attributes values are not present",
        ex.getMessage());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
            anyString(), anyLong(), anyString());
    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndName(anyString(), anyString());
    verify(sourcingAttributesDefinitionDomain, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processGetGroupDefinitionByIdAndOrgIdTest()
      throws PromiseEngineException, CommonServiceException {

    when(groupDefinitionDomain.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));

    GroupDefinitionResponse groupDefinitionResponse =
        groupDefinitionService.processGetGroupDefinitionByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID);
    assertEquals(testUtil.getGroupDefinitionEntity().getId(), groupDefinitionResponse.getId());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processGetGroupDefinitionByIdAndOrgIdExceptionTest() throws PromiseEngineException {

    when(groupDefinitionDomain.fetchGroupDefinitionById(anyLong())).thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              groupDefinitionService.processGetGroupDefinitionByIdAndOrgId(
                  TestUtil.ID, TestUtil.ORG_ID);
            });
    assertEquals("Group definition not found", ex.getMessage());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processGetGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdTest1()
      throws PromiseEngineException, CommonServiceException {

    when(groupDefinitionDomain.fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
            anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getGroupDefinitionEntity()));

    GroupDefinitionListResponse groupDefinitionResponse =
        groupDefinitionService
            .processGetGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
                TestUtil.ORG_ID, TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID);
    assertEquals(1, groupDefinitionResponse.getGroupDefinitionInfoList().size());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(anyString(), anyLong());
  }

  @Test
  void processGetGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdTest2()
      throws PromiseEngineException, CommonServiceException {

    when(groupDefinitionDomain.fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
            anyString(), anyLong()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              groupDefinitionService
                  .processGetGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
                      TestUtil.ORG_ID, TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID);
            });

    assertEquals("Group definition not found", ex.getMessage());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(anyString(), anyLong());
  }

  @Test
  void updateGroupDefinitionTest() throws PromiseEngineException, CommonServiceException {
    String updatedGroupName = "group2";
    GroupDefinitionRequest groupDefinitionRequest = testUtil.getGroupDefinitionRequest();
    groupDefinitionRequest.setGroupName(updatedGroupName);

    GroupDefinitionDomainDto updatedGroupDefinitionEntity = testUtil.getGroupDefinitionEntity();
    updatedGroupDefinitionEntity.setGroupName(updatedGroupName);

    when(groupDefinitionDomain
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of(testUtil.getGroupDefinitionEntity()));
    when(groupDefinitionDomain.fetchGroupDefinitionListByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of());
    when(groupDefinitionDomain.saveGroupDefinition(any(GroupDefinitionDomainDto.class)))
        .thenReturn(updatedGroupDefinitionEntity);

    GroupDefinitionResponse groupDefinitionResponse =
        groupDefinitionService.processUpdateGroupDefinition(groupDefinitionRequest);
    assertEquals(testUtil.getGroupDefinitionEntity().getId(), groupDefinitionResponse.getId());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
            anyString(), anyLong(), anyString());
    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndName(anyString(), anyString());
    verify(groupDefinitionDomain, times(1))
        .saveGroupDefinition(any(GroupDefinitionDomainDto.class));
  }

  @Test
  void updateGroupDefinitionExceptionTest() throws PromiseEngineException, CommonServiceException {
    String updatedGroupName = "group2";
    GroupDefinitionRequest groupDefinitionRequest = testUtil.getGroupDefinitionRequest();
    groupDefinitionRequest.setGroupName(updatedGroupName);

    when(groupDefinitionDomain
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              groupDefinitionService.processUpdateGroupDefinition(groupDefinitionRequest);
            });
    assertEquals(
        "Group definition not found for given orgId , sourcingAttributesDefinitionId and reqAttributesValue",
        ex.getMessage());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
            anyString(), anyLong(), anyString());
  }

  @Test
  void processUpdateGroupDefinitionExceptionTest2()
      throws PromiseEngineException, CommonServiceException {
    GroupDefinitionRequest groupDefinitionRequest = testUtil.getGroupDefinitionRequest();
    groupDefinitionRequest.setReqAttributesValue("VR1,VR2");

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              groupDefinitionService.processUpdateGroupDefinition(groupDefinitionRequest);
            });
    assertEquals("Invalid format! All the characters except comma are allowed", ex.getMessage());
  }

  @Test
  void processDeleteGroupDefinitionTest1() throws PromiseEngineException, CommonServiceException {

    when(groupDefinitionDomain.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(namedOptimizationStrategyDomain.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of());
    doNothing()
        .when(groupDefinitionDomain)
        .deleteGroupDefinition(any(GroupDefinitionDomainDto.class));

    GroupDefinitionResponse groupDefinitionResponse =
        groupDefinitionService.processDeleteGroupDefinition(TestUtil.ID, TestUtil.ORG_ID);
    assertEquals(testUtil.getGroupDefinitionEntity().getId(), groupDefinitionResponse.getId());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(groupDefinitionDomain, times(1))
        .deleteGroupDefinition(any(GroupDefinitionDomainDto.class));
    verify(namedOptimizationStrategyDomain, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void processDeleteGroupDefinitionTest2() throws PromiseEngineException, CommonServiceException {

    when(groupDefinitionDomain.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(namedOptimizationStrategyDomain.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of(testUtil.getNamedOptimizationStrategyEntity()));
    doNothing()
        .when(namedOptimizationStrategyDomain)
        .deleteOptimizationStrategy(any(NamedOptimizationStrategyDomainDto.class));
    doNothing()
        .when(groupDefinitionDomain)
        .deleteGroupDefinition(any(GroupDefinitionDomainDto.class));

    GroupDefinitionResponse groupDefinitionResponse =
        groupDefinitionService.processDeleteGroupDefinition(TestUtil.ID, TestUtil.ORG_ID);
    assertEquals(testUtil.getGroupDefinitionEntity().getId(), groupDefinitionResponse.getId());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(groupDefinitionDomain, times(1))
        .deleteGroupDefinition(any(GroupDefinitionDomainDto.class));
    verify(namedOptimizationStrategyDomain, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
    verify(namedOptimizationStrategyDomain, times(1))
        .deleteOptimizationStrategy(any(NamedOptimizationStrategyDomainDto.class));
  }

  @Test
  void processDeleteGroupDefinitionExceptionTest() throws PromiseEngineException {

    when(groupDefinitionDomain.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              groupDefinitionService.processDeleteGroupDefinition(TestUtil.ID, TestUtil.ORG_ID);
            });
    assertEquals("Group definition not found", ex.getMessage());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processGetGroupDefinitionByOrgIdAndIdTest()
      throws PromiseEngineException, CommonServiceException {

    when(groupDefinitionDomain.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));

    GroupDefinitionResponse groupDefinitionResponse =
        groupDefinitionService.processGetGroupDefinitionByOrgIdAndId(
            TestUtil.ORG_ID, Long.valueOf(TestUtil.GROUP_ID));
    assertEquals(testUtil.getGroupDefinitionEntity().getId(), groupDefinitionResponse.getId());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processGetGroupDefinitionByOrgIdAndIdExceptionTest() throws PromiseEngineException {

    when(groupDefinitionDomain.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              groupDefinitionService.processGetGroupDefinitionByOrgIdAndId(
                  TestUtil.ORG_ID, Long.valueOf(TestUtil.GROUP_ID));
            });
    assertEquals("Group definition not found", ex.getMessage());

    verify(groupDefinitionDomain, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
  }
}
