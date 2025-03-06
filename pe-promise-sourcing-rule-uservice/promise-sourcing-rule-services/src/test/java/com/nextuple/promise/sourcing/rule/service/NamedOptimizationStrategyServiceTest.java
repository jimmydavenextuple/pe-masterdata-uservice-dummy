/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingConstraintEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingConstraintTypeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.DeleteOptimizationRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NamedOptimizationStrategyRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.OptimizationStrategyUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.DetailedOptimizationStrategyResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NamedOptimizationStrategyResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.OptimizationRuleUIResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeInfo;
import com.nextuple.promise.sourcing.rule.domain.mapper.NamedOptimizationStrategyMapper;
import com.nextuple.promise.sourcing.rule.persistence.domain.GroupDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.NamedOptimizationStrategyDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributesDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingConstraintDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.*;
import com.nextuple.promise.sourcing.rule.utils.FetchRulesUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

class NamedOptimizationStrategyServiceTest {
  @InjectMocks private NamedOptimizationStrategyService namedOptimizationStrategyService;

  @Mock private GroupDefinitionPersistenceService groupDefinitionPersistenceService;
  @Mock private SourcingConstraintPersistenceService sourcingConstraintPersistenceService;

  @Mock private GroupDefinitionService groupDefinitionService;
  @Mock private FetchRulesUtil fetchRulesUtil;

  @Mock
  private NamedOptimizationStrategyPersistenceService namedOptimizationStrategyPersistenceService;

  @Mock private SourcingAttributesDefinitionPersistenceService sourcingAttributesDefinitionDomain;
  @Mock private SourcingAttributePersistenceService sourcingAttributePersistenceService;
  @Mock private SourcingAttributesDefinitionService sourcingAttributesDefinitionService;

  @Mock private SourcingAttributeService sourcingAttributeService;

  @InjectMocks private TestUtil testUtil;

  private static final NamedOptimizationStrategyMapper INSTANCE =
      Mappers.getMapper(NamedOptimizationStrategyMapper.class);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processAddOptimizationStrategyTest() throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();

    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of());
    when(namedOptimizationStrategyPersistenceService
            .fetchOptimizationStrategyByOrgIdAndStrategyName(anyString(), anyString()))
        .thenReturn(List.of());
    when(namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
            any(NamedOptimizationStrategyDomainDto.class)))
        .thenReturn(namedOptimizationStrategyEntity);

    NamedOptimizationStrategyResponse namedOptimizationStrategyResponse =
        namedOptimizationStrategyService.processAddOptimizationStrategy(
            testUtil.getNamedOptimizationStrategyRequest());
    assertEquals(
        namedOptimizationStrategyEntity.getId(), namedOptimizationStrategyResponse.getId());
    assertEquals(
        namedOptimizationStrategyEntity.getCustomAttributes(),
        namedOptimizationStrategyResponse.getCustomAttributes());

    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .saveOptimizationStrategy(any(NamedOptimizationStrategyDomainDto.class));
  }

  @Test
  void processAddOptimizationStrategyCostScenarioTest()
      throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    namedOptimizationStrategyEntity.setOptimizationStrategyDetails("COST");
    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of());
    when(namedOptimizationStrategyPersistenceService
            .fetchOptimizationStrategyByOrgIdAndStrategyName(anyString(), anyString()))
        .thenReturn(List.of());
    when(namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
            any(NamedOptimizationStrategyDomainDto.class)))
        .thenReturn(namedOptimizationStrategyEntity);

    NamedOptimizationStrategyRequest namedOptimizationStrategyRequest =
        testUtil.getNamedOptimizationStrategyRequest();
    namedOptimizationStrategyRequest.setOptimizationStrategyDetails("COST");
    NamedOptimizationStrategyResponse namedOptimizationStrategyResponse =
        namedOptimizationStrategyService.processAddOptimizationStrategy(
            namedOptimizationStrategyRequest);
    assertEquals(
        namedOptimizationStrategyEntity.getId(), namedOptimizationStrategyResponse.getId());
    assertEquals("COST", namedOptimizationStrategyResponse.getOptimizationStrategyDetails());

    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .saveOptimizationStrategy(any(NamedOptimizationStrategyDomainDto.class));
  }

  @Test
  void processAddOptimizationStrategyExceptionTest1() throws PromiseEngineException {

    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.processAddOptimizationStrategy(
                  testUtil.getNamedOptimizationStrategyRequest());
            });
    assertEquals("Invalid groupId", ex.getMessage());

    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processAddOptimizationStrategyExceptionTest2() throws PromiseEngineException {

    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of(testUtil.getNamedOptimizationStrategyEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.processAddOptimizationStrategy(
                  testUtil.getNamedOptimizationStrategyRequest());
            });
    assertEquals(
        "Named optimization strategy already associated for given orgId and groupId",
        ex.getMessage());

    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void processAddOptimizationStrategyExceptionTest3() throws PromiseEngineException {
    NamedOptimizationStrategyRequest namedOptimizationStrategyRequest =
        testUtil.getNamedOptimizationStrategyRequest();
    namedOptimizationStrategyRequest.setOptimizationStrategyDetails("xyz");

    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of());
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.processAddOptimizationStrategy(
                  namedOptimizationStrategyRequest);
            });
    assertEquals("Invalid named optimization strategy detail", ex.getMessage());

    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void processGetOptimizationStrategyByIdAndOrgIdTest()
      throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.of(namedOptimizationStrategyEntity));

    NamedOptimizationStrategyResponse namedOptimizationStrategyResponse =
        namedOptimizationStrategyService.processGetOptimizationStrategyByIdAndOrgId(
            TestUtil.OPTIMIZATION_STRATEGY_ID, TestUtil.ORG_ID);
    assertEquals(
        namedOptimizationStrategyEntity.getId(), namedOptimizationStrategyResponse.getId());

    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processGetOptimizationStrategyByIdExceptionTest() throws PromiseEngineException {

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyById(anyLong()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.processGetOptimizationStrategyByIdAndOrgId(
                  TestUtil.OPTIMIZATION_STRATEGY_ID, TestUtil.ORG_ID);
            });
    assertEquals("Named optimization strategy not found for given id", ex.getMessage());

    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processGetOptimizationStrategyByOrgIdAndGroupIdTest1()
      throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of(namedOptimizationStrategyEntity));
    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityById(
            anyLong()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    DetailedOptimizationStrategyResponse detailedOptimizationStrategyResponse =
        namedOptimizationStrategyService.processGetOptimizationStrategyByOrgIdAndGroupId(
            TestUtil.ORG_ID, TestUtil.GROUP_ID);
    assertEquals(
        namedOptimizationStrategyEntity.getId(), detailedOptimizationStrategyResponse.getId());

    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void processGetOptimizationStrategyByOrgIdAndGroupIdTest2()
      throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    namedOptimizationStrategyEntity.setGroupId("DEFAULT");
    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), eq("DEFAULT")))
        .thenReturn(List.of(namedOptimizationStrategyEntity));

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
        .doAnswer(
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
        .getRequiredAttributeDetails(any(), any(List.class), any(), any(), any());
    DetailedOptimizationStrategyResponse detailedOptimizationStrategyResponse =
        namedOptimizationStrategyService.processGetOptimizationStrategyByOrgIdAndGroupId(
            TestUtil.ORG_ID, TestUtil.GROUP_ID);
    assertEquals(
        namedOptimizationStrategyEntity.getId(), detailedOptimizationStrategyResponse.getId());
    assertEquals(TestUtil.DEFAULT_GROUP_ID, detailedOptimizationStrategyResponse.getGroupName());
    assertNull(detailedOptimizationStrategyResponse.getRequiredAttributes());

    verify(namedOptimizationStrategyPersistenceService, times(2))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void processGetOptimizationStrategyByOrgIdAndGroupIdTestDefaultNotFound()
      throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    namedOptimizationStrategyEntity.setGroupId("DEFAULT");
    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), eq("DEFAULT")))
        .thenReturn(Collections.emptyList());

    Assertions.assertThrows(
        CommonServiceException.class,
        () -> {
          namedOptimizationStrategyService.processGetOptimizationStrategyByOrgIdAndGroupId(
              TestUtil.ORG_ID, TestUtil.GROUP_ID);
        });

    verify(namedOptimizationStrategyPersistenceService, times(2))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void processUpdateOptimizationStrategyTest()
      throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of(namedOptimizationStrategyEntity));
    when(namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
            any(NamedOptimizationStrategyDomainDto.class)))
        .thenReturn(testUtil.getUpdatedNamedOptimizationStrategyEntity());

    NamedOptimizationStrategyResponse namedOptimizationStrategyResponse =
        namedOptimizationStrategyService.processUpdateOptimizationStrategy(
            TestUtil.ORG_ID,
            TestUtil.GROUP_ID,
            testUtil.getNamedOptimizationStrategyUpdationRequest());
    assertEquals(
        namedOptimizationStrategyEntity.getId(), namedOptimizationStrategyResponse.getId());
    assertEquals(
        namedOptimizationStrategyEntity.getCustomAttributes(),
        namedOptimizationStrategyResponse.getCustomAttributes());

    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .saveOptimizationStrategy(any(NamedOptimizationStrategyDomainDto.class));
  }

  @Test
  void processUpdateOptimizationStrategyExceptionTest() throws PromiseEngineException {

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.processUpdateOptimizationStrategy(
                  TestUtil.ORG_ID,
                  TestUtil.GROUP_ID,
                  testUtil.getNamedOptimizationStrategyUpdationRequest());
            });
    assertEquals(
        "Named optimization strategy not found for given orgId and groupId", ex.getMessage());

    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void processDeleteOptimizationStrategyTest()
      throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of(namedOptimizationStrategyEntity));
    doNothing()
        .when(namedOptimizationStrategyPersistenceService)
        .deleteOptimizationStrategy(any(NamedOptimizationStrategyDomainDto.class));

    NamedOptimizationStrategyResponse namedOptimizationStrategyResponse =
        namedOptimizationStrategyService.processDeleteOptimizationStrategy(
            TestUtil.ORG_ID, TestUtil.GROUP_ID);
    assertEquals(
        namedOptimizationStrategyEntity.getId(), namedOptimizationStrategyResponse.getId());

    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .deleteOptimizationStrategy(any(NamedOptimizationStrategyDomainDto.class));
  }

  @Test
  void processDeleteOptimizationStrategyExceptionTest() throws PromiseEngineException {

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.processDeleteOptimizationStrategy(
                  TestUtil.ORG_ID, TestUtil.GROUP_ID);
            });
    assertEquals(
        "Named optimization strategy not found for given orgId and groupId", ex.getMessage());

    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void getOptimizationRuleByOrgIdAndNamedOptimizationStrategyWithoutServiceOptionTest()
      throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();

    GroupDefinitionResponse groupDefinitionResponse = testUtil.getGroupDefinitionResponse();

    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);

    SourcingAttributeResponse sourcingAttributeResponse = testUtil.getSourcingAttributeResponse();

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.of(namedOptimizationStrategyEntity));

    when(groupDefinitionService.processGetGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(groupDefinitionResponse);

    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(sourcingAttributesDefinitionResponse);

    when(sourcingAttributeService.getSourcingAttributeByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(sourcingAttributeResponse);
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSourcingConstraintEntity()));

    OptimizationRuleUIResponse optimizationRuleUIResponse =
        namedOptimizationStrategyService.getOptimizationRuleByOrgIdAndNamedOptimizationStrategyId(
            namedOptimizationStrategyEntity.getOrgId(), namedOptimizationStrategyEntity.getId());

    assertEquals(
        namedOptimizationStrategyEntity.getId(),
        Long.valueOf(optimizationRuleUIResponse.getOptimizationRuleId()));
    assertEquals(
        groupDefinitionResponse.getId(),
        Long.valueOf(optimizationRuleUIResponse.getSourcingAttributesDefinitionId()));

    assertTrue(
        groupDefinitionResponse
            .getReqAttributesValue()
            .contains(
                optimizationRuleUIResponse.getRequiredAttributes().get(0).getAttributeValue()));

    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByIdAndOrgId(anyLong(), anyString());
    verify(groupDefinitionService, times(1))
        .processGetGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), anyString());
    verify(sourcingAttributeService, times(2))
        .getSourcingAttributeByIdAndOrgId(anyLong(), anyString());
    verify(sourcingConstraintPersistenceService, times(2))
        .fetchByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void getOptimizationRuleByOrgIdAndNamedOptimizationStrategyIdWithServiceOptionTest()
      throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();

    GroupDefinitionResponse groupDefinitionResponse = testUtil.getGroupDefinitionResponse();

    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);

    SourcingAttributeResponse sourcingAttributeResponse =
        testUtil.getSourcingAttributeResponseWithServiceOption();

    SourcingConstraintDomainDto sourcingConstraintEntity = testUtil.getSourcingConstraintEntity();

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.of(namedOptimizationStrategyEntity));

    when(groupDefinitionService.processGetGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(groupDefinitionResponse);

    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(sourcingAttributesDefinitionResponse);

    when(sourcingAttributeService.getSourcingAttributeByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(sourcingAttributeResponse);

    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(anyString(), anyString()))
        .thenReturn(List.of(sourcingConstraintEntity));

    OptimizationRuleUIResponse optimizationRuleUIResponse =
        namedOptimizationStrategyService.getOptimizationRuleByOrgIdAndNamedOptimizationStrategyId(
            namedOptimizationStrategyEntity.getOrgId(), namedOptimizationStrategyEntity.getId());

    assertEquals(
        namedOptimizationStrategyEntity.getId(),
        Long.valueOf(optimizationRuleUIResponse.getOptimizationRuleId()));
    assertEquals(
        groupDefinitionResponse.getId(),
        Long.valueOf(optimizationRuleUIResponse.getSourcingAttributesDefinitionId()));

    assertTrue(
        groupDefinitionResponse
            .getReqAttributesValue()
            .contains(
                optimizationRuleUIResponse.getRequiredAttributes().get(0).getAttributeValue()));

    assertEquals(
        sourcingConstraintEntity.getSourcingConstraint().name(),
        optimizationRuleUIResponse.getConstraints().get(0).getValue());

    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByIdAndOrgId(anyLong(), anyString());
    verify(groupDefinitionService, times(1))
        .processGetGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), anyString());
    verify(sourcingAttributeService, times(2))
        .getSourcingAttributeByIdAndOrgId(anyLong(), anyString());
    verify(sourcingConstraintPersistenceService, times(2))
        .fetchByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void processGetAllUIConstraintsHappyPathTest() {
    SourcingConstraintEnum newEnum = mock(SourcingConstraintEnum.class);
    when(newEnum.getConstraintName()).thenReturn("new Enum");
    when(newEnum.getDescription()).thenReturn(String.valueOf(SourcingConstraintTypeEnum.SOFT));
    mockStatic(SourcingConstraintEnum.class);
    when(SourcingConstraintEnum.values())
        .thenReturn(
            new SourcingConstraintEnum[] {
              SourcingConstraintEnum.PRE_ORDER_PROMISE_DATE,
              SourcingConstraintEnum.SHIP_COMPLETE_LINE,
              newEnum
            });
    assertEquals(
        newEnum.getConstraintName(),
        namedOptimizationStrategyService.getAllUIConstraints().get(2).getLabel());
  }

  @Test
  void processGetOptimizationStrategyByOrgIdAndGroupIdsHappyPathTest()
      throws PromiseEngineException, CommonServiceException {

    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyEntityList =
        List.of(namedOptimizationStrategyEntity);

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).descending());

    Page<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyEntityPage =
        new PageImpl<>(
            namedOptimizationStrategyEntityList,
            pageable,
            namedOptimizationStrategyEntityList.size());

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString(), any()))
        .thenReturn(namedOptimizationStrategyEntityPage);

    Page<NamedOptimizationStrategyResponse> response =
        namedOptimizationStrategyService.processGetOptimizationStrategyByOrgIdAndGroupIds(
            TestUtil.ORG_ID, List.of(TestUtil.GROUP_ID), pageable);

    Page<NamedOptimizationStrategyResponse> namedOptimizationStrategyResponses =
        namedOptimizationStrategyEntityPage.map(INSTANCE::toNamedOptimizationStrategyResponse);

    assertEquals(namedOptimizationStrategyResponses, response);

    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString(), any());
  }

  @Test
  void processGetOptimizationStrategyByOrgIdAndGroupIdsException() throws PromiseEngineException {

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).descending());

    Page<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyEntityPage =
        new PageImpl<>(new ArrayList<>(), pageable, 0);

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString(), any()))
        .thenReturn(namedOptimizationStrategyEntityPage);

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.processGetOptimizationStrategyByOrgIdAndGroupIds(
                  TestUtil.ORG_ID, List.of(TestUtil.GROUP_ID), pageable);
            });

    assertEquals("Named optimization strategies not found for given org id", ex.getMessage());

    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString(), any());
  }

  @Test
  @DisplayName(
      "When optional attribute is part of sourcing attr rule for is not present in group def")
  void processGetOptimizationStrategyByOrgIdAndGroupIdsNoOptionalAttrTest()
      throws PromiseEngineException, CommonServiceException {

    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyEntityList =
        List.of(namedOptimizationStrategyEntity);

    NamedOptimizationStrategyDomainDto namedOptimizationStrategyDto =
        namedOptimizationStrategyEntityList.get(0);
    GroupDefinitionDomainDto groupDef = testUtil.getGroupDefinitionEntity();
    groupDef.setOptionalAttributesValue(null);
    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(
            Long.valueOf(TestUtil.GROUP_ID), TestUtil.ORG_ID))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of(namedOptimizationStrategyDto));

    doNothing()
        .when(fetchRulesUtil)
        .getRequiredAttributeDetails(anyString(), any(), any(), any(), any());

    SourcingAttributesDefinitionDomainDto sourcingAttrDefDto =
        new SourcingAttributesDefinitionDomainDto();
    sourcingAttrDefDto.setName("Test");
    sourcingAttrDefDto.setReqAttributes("123:456");
    sourcingAttrDefDto.setOptAttributes("789:012");
    sourcingAttrDefDto.setOrgId(TestUtil.ORG_ID);
    sourcingAttrDefDto.setModule("Test");

    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityById(
            Long.valueOf(1L)))
        .thenReturn(Optional.of(sourcingAttrDefDto));

    DetailedOptimizationStrategyResponse response =
        namedOptimizationStrategyService.processGetOptimizationStrategyByOrgIdAndGroupId(
            TestUtil.ORG_ID, TestUtil.GROUP_ID);
    Assertions.assertNotNull(response);
  }

  @Test
  @DisplayName("Sourcing attr def not found")
  void processGetOptimizationStrategyByOrgIdAndGroupIdsNoSourcingAttrTest()
      throws PromiseEngineException, CommonServiceException {

    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyEntityList =
        List.of(namedOptimizationStrategyEntity);

    NamedOptimizationStrategyDomainDto namedOptimizationStrategyDto =
        namedOptimizationStrategyEntityList.get(0);
    GroupDefinitionDomainDto groupDef = testUtil.getGroupDefinitionEntity();
    groupDef.setOptionalAttributesValue(null);
    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(
            Long.valueOf(TestUtil.GROUP_ID), TestUtil.ORG_ID))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of(namedOptimizationStrategyDto));

    doNothing()
        .when(fetchRulesUtil)
        .getRequiredAttributeDetails(anyString(), any(), any(), any(), any());

    SourcingAttributesDefinitionDomainDto sourcingAttrDefDto =
        new SourcingAttributesDefinitionDomainDto();
    sourcingAttrDefDto.setName("Test");
    sourcingAttrDefDto.setReqAttributes("123:456");
    sourcingAttrDefDto.setOptAttributes("789:012");
    sourcingAttrDefDto.setOrgId(TestUtil.ORG_ID);
    sourcingAttrDefDto.setModule("Test");

    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityById(
            Long.valueOf(1L)))
        .thenReturn(Optional.empty());

    DetailedOptimizationStrategyResponse response =
        namedOptimizationStrategyService.processGetOptimizationStrategyByOrgIdAndGroupId(
            TestUtil.ORG_ID, TestUtil.GROUP_ID);
    Assertions.assertNotNull(response);
    Assertions.assertNull(response.getOptionalAttributes());
    Assertions.assertNull(response.getRequiredAttributes());
  }

  @Test
  @DisplayName("When optional attribute is part of sourcing attr rule for is present in group def")
  void processGetOptimizationStrategyByOrgIdAndGroupIdsTest()
      throws PromiseEngineException, CommonServiceException {

    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyEntityList =
        List.of(namedOptimizationStrategyEntity);

    NamedOptimizationStrategyDomainDto namedOptimizationStrategyDto =
        namedOptimizationStrategyEntityList.get(0);
    GroupDefinitionDomainDto groupDef = testUtil.getGroupDefinitionEntity();
    groupDef.setOptionalAttributesValue("V3:V4");
    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(
            Long.valueOf(TestUtil.GROUP_ID), TestUtil.ORG_ID))
        .thenReturn(Optional.of(groupDef));

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of(namedOptimizationStrategyDto));

    doNothing()
        .when(fetchRulesUtil)
        .getRequiredAttributeDetails(anyString(), any(), any(), any(), any());

    SourcingAttributesDefinitionDomainDto sourcingAttrDefDto =
        new SourcingAttributesDefinitionDomainDto();
    sourcingAttrDefDto.setName("Test");
    sourcingAttrDefDto.setReqAttributes("123:456");
    sourcingAttrDefDto.setOptAttributes("789:012");
    sourcingAttrDefDto.setOrgId(TestUtil.ORG_ID);
    sourcingAttrDefDto.setModule("Test");

    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityById(
            Long.valueOf(1L)))
        .thenReturn(Optional.of(sourcingAttrDefDto));

    DetailedOptimizationStrategyResponse response =
        namedOptimizationStrategyService.processGetOptimizationStrategyByOrgIdAndGroupId(
            TestUtil.ORG_ID, TestUtil.GROUP_ID);
    Assertions.assertNotNull(response);
  }

  @Test
  void getOptimizationRuleByOrgIdAndNamedOptimizationStrategyResponsesHappyPath()
      throws PromiseEngineException, CommonServiceException {

    NamedOptimizationStrategyResponse namedOptimizationStrategyResponse =
        testUtil.getNamedOptimizationStrategyResponse();

    GroupDefinitionResponse groupDefinitionResponse = testUtil.getGroupDefinitionResponse();

    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionResponse.setOptAttributes("3,4");

    SourcingAttributeResponse sourcingAttributeResponse = testUtil.getSourcingAttributeResponse();

    when(groupDefinitionService.processGetGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(groupDefinitionResponse);

    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(sourcingAttributesDefinitionResponse);

    when(sourcingAttributeService.getSourcingAttributeByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(sourcingAttributeResponse);
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSourcingConstraintEntity()));

    OptimizationRuleUIResponse optimizationRuleUIResponse =
        namedOptimizationStrategyService
            .getOptimizationRuleByOrgIdAndNamedOptimizationStrategyResponse(
                namedOptimizationStrategyResponse.getOrgId(), namedOptimizationStrategyResponse);

    assertEquals(
        namedOptimizationStrategyResponse.getId(),
        Long.valueOf(optimizationRuleUIResponse.getOptimizationRuleId()));
    assertEquals(
        groupDefinitionResponse.getId(),
        Long.valueOf(optimizationRuleUIResponse.getSourcingAttributesDefinitionId()));

    assertTrue(
        groupDefinitionResponse
            .getReqAttributesValue()
            .contains(
                optimizationRuleUIResponse.getRequiredAttributes().get(0).getAttributeValue()));
    assertTrue(
        groupDefinitionResponse
            .getOptionalAttributesValue()
            .contains(
                optimizationRuleUIResponse.getOptionalAttributes().get(0).getAttributeValue()));

    verify(groupDefinitionService, times(1))
        .processGetGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), anyString());
    verify(sourcingAttributeService, times(4))
        .getSourcingAttributeByIdAndOrgId(anyLong(), anyString());
    verify(sourcingConstraintPersistenceService, times(4))
        .fetchByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void getOptimizationRuleByOrgIdAndNamedOptimizationStrategyResponsesException()
      throws PromiseEngineException, CommonServiceException {

    NamedOptimizationStrategyResponse namedOptimizationStrategyResponse =
        testUtil.getNamedOptimizationStrategyResponse();

    GroupDefinitionResponse groupDefinitionResponse = testUtil.getGroupDefinitionResponse();

    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponseWithRequiredAttributeInconsistency(
            SourcingAttributesDefinitionStatus.ACTIVE);

    when(groupDefinitionService.processGetGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(groupDefinitionResponse);

    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(sourcingAttributesDefinitionResponse);

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService
                  .getOptimizationRuleByOrgIdAndNamedOptimizationStrategyResponse(
                      namedOptimizationStrategyResponse.getOrgId(),
                      namedOptimizationStrategyResponse);
            });

    assertEquals(
        "Error in getting optimization rule due to inconsistent attribute values.",
        ex.getMessage());

    verify(groupDefinitionService, times(1))
        .processGetGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), anyString());
    verify(sourcingAttributeService, times(0))
        .getSourcingAttributeByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getAllOptimizationRulesByOrgIdHappyPathTest()
      throws PromiseEngineException, CommonServiceException {
    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).descending());

    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyEntityList =
        List.of(testUtil.getNamedOptimizationStrategyEntity());
    List<NamedOptimizationStrategyDomainDto> defaultNamedOptimizationStrategyDto =
        List.of(testUtil.getDefaultNamedOptimizationStrategyEntity());

    Page<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyEntityPage =
        new PageImpl<>(
            namedOptimizationStrategyEntityList,
            pageable,
            namedOptimizationStrategyEntityList.size());

    Page<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyEntityPage2 =
        new PageImpl<>(
            defaultNamedOptimizationStrategyDto,
            pageable,
            namedOptimizationStrategyEntityList.size());

    when(sourcingAttributesDefinitionDomain
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                anyString(), any(), any()))
        .thenReturn(
            List.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    when(groupDefinitionPersistenceService
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
                anyString(), anyLong()))
        .thenReturn(List.of(testUtil.getGroupDefinitionEntity()));

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString(), any()))
        .thenReturn(namedOptimizationStrategyEntityPage)
        .thenReturn(namedOptimizationStrategyEntityPage2);

    GroupDefinitionResponse groupDefinitionResponse = testUtil.getGroupDefinitionResponse();

    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);

    SourcingAttributeResponse sourcingAttributeResponse = testUtil.getSourcingAttributeResponse();

    when(groupDefinitionService.processGetGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(groupDefinitionResponse);

    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(sourcingAttributesDefinitionResponse);

    when(sourcingAttributeService.getSourcingAttributeByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(sourcingAttributeResponse);
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSourcingConstraintEntity()));

    PageParams pageParams =
        testUtil.getPageParams(
            Optional.of(2),
            Optional.of(1),
            Optional.of(TestUtil.SORT_BY),
            Optional.of(TestUtil.DEFAULT_SORT_ORDER));

    PageResponse<OptimizationRuleUIResponse> responsePageResponse =
        namedOptimizationStrategyService.getAllOptimizationRulesByOrgId(
            TestUtil.ORG_ID, pageParams);
    assertNotNull(responsePageResponse);
    assertEquals(2, responsePageResponse.getData().size());
    verify(sourcingAttributesDefinitionDomain, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
            anyString(), any(), any());
    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(anyString(), anyLong());
    verify(namedOptimizationStrategyPersistenceService, times(2))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString(), any());

    verify(groupDefinitionService, times(1))
        .processGetGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), anyString());
    verify(sourcingAttributeService, times(2))
        .getSourcingAttributeByIdAndOrgId(anyLong(), anyString());
    verify(sourcingConstraintPersistenceService, times(2))
        .fetchByOrgIdAndGroupId(anyString(), anyString());
  }

  @DisplayName("Testing the functionality to fetch the default rule of an orgId.")
  @Test
  void getAllOptimizationRulesAndDefaultRuleByOrgIdHappyPathTest()
      throws PromiseEngineException, CommonServiceException {
    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).descending());
    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyEntityList =
        List.of(testUtil.getDefaultNamedOptimizationStrategyEntity());
    Page<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyEntityPage =
        new PageImpl<>(
            namedOptimizationStrategyEntityList,
            pageable,
            namedOptimizationStrategyEntityList.size());

    when(sourcingAttributesDefinitionDomain
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                anyString(), any(), any()))
        .thenReturn(
            List.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    when(groupDefinitionPersistenceService
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
                anyString(), anyLong()))
        .thenReturn(List.of());

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString(), any()))
        .thenReturn(namedOptimizationStrategyEntityPage);

    GroupDefinitionResponse groupDefinitionResponse = testUtil.getGroupDefinitionResponse();

    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);

    SourcingAttributeResponse sourcingAttributeResponse = testUtil.getSourcingAttributeResponse();

    when(groupDefinitionService.processGetGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(groupDefinitionResponse);

    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(sourcingAttributesDefinitionResponse);

    when(sourcingAttributeService.getSourcingAttributeByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(sourcingAttributeResponse);
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSourcingConstraintEntity()));

    PageParams pageParams =
        testUtil.getPageParams(
            Optional.of(1),
            Optional.of(1),
            Optional.of(TestUtil.SORT_BY),
            Optional.of(TestUtil.DEFAULT_SORT_ORDER));

    PageResponse<OptimizationRuleUIResponse> responsePageResponse =
        namedOptimizationStrategyService.getAllOptimizationRulesByOrgId(
            TestUtil.ORG_ID, pageParams);
    assertNotNull(responsePageResponse);
    assertEquals(1, responsePageResponse.getData().size());
    assertEquals("DEFAULT", responsePageResponse.getData().get(0).getGroupId());
    verify(sourcingAttributesDefinitionDomain, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
            anyString(), any(), any());
    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(anyString(), anyLong());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString(), any());
  }

  @Test
  void getAllOptimizationRulesByOrgIdNoActiveSourcingAttributeDefinitionExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributesDefinitionDomain
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                anyString(), any(), any()))
        .thenReturn(List.of());
    PageParams pageParams =
        testUtil.getPageParams(
            Optional.of(2),
            Optional.of(1),
            Optional.of(TestUtil.SORT_BY),
            Optional.of(TestUtil.DEFAULT_SORT_ORDER));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.getAllOptimizationRulesByOrgId(
                  TestUtil.ORG_ID, pageParams);
            });
    assertEquals(
        "No active sourcing rule attributes definition exists for given orgId", ex.getMessage());
    verify(sourcingAttributesDefinitionDomain, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
            anyString(), any(), any());
  }

  @Test
  @DisplayName("Happy path for edit optimization rule")
  void processEditOptimizationRuleUITest() throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    NamedOptimizationStrategyDomainDto updatedResponse =
        testUtil.getUpdatedNamedOptimizationStrategyEntityUI();

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyById(anyLong()))
        .thenReturn(Optional.of(namedOptimizationStrategyEntity));
    when(namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
            any(NamedOptimizationStrategyDomainDto.class)))
        .thenReturn(updatedResponse);
    when(groupDefinitionPersistenceService.fetchGroupDefinitionById(anyLong()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSourcingConstraintEntity()));
    when(sourcingConstraintPersistenceService.saveSourcingConstraintEntities(any()))
        .thenReturn(List.of(testUtil.getSourcingConstraintEntity()));
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            anyString(), anyString(), any()))
        .thenReturn(List.of(testUtil.getSourcingConstraintEntity()));
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityById(
            anyLong()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));

    OptimizationRuleUIResponse response =
        namedOptimizationStrategyService.processEditOptimizationRuleUI(
            TestUtil.ORG_ID,
            TestUtil.OPTIMIZATION_STRATEGY_ID,
            testUtil.getOptimizationRuleUpdationUIRequest());

    assertEquals(namedOptimizationStrategyEntity.getId(), response.getOptimizationRuleId());
    assertEquals(updatedResponse.getOptimizationStrategyName(), response.getOptimizationRuleName());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyById(anyLong());
  }

  @Test
  @DisplayName("When constraints in request body is null for edit optimization rule")
  void processEditOptimizationRuleUIConstraintsInRequestIsNullTest()
      throws PromiseEngineException, CommonServiceException {
    var namedOptimizationStrategyEntity = testUtil.getNamedOptimizationStrategyEntity();
    var updatedResponse = testUtil.getUpdatedNamedOptimizationStrategyEntityUI();
    var requestBody = testUtil.getOptimizationRuleUpdationUIRequest();
    requestBody.setConstraints(null);
    var sourcingConstraintEntities = testUtil.getSourcingConstraintEntity();
    sourcingConstraintEntities.setSourcingConstraintValue("0");

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyById(anyLong()))
        .thenReturn(Optional.of(namedOptimizationStrategyEntity));
    when(namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
            any(NamedOptimizationStrategyDomainDto.class)))
        .thenReturn(updatedResponse);
    when(groupDefinitionPersistenceService.fetchGroupDefinitionById(anyLong()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(anyString(), anyString()))
        .thenReturn(List.of(sourcingConstraintEntities));
    when(sourcingConstraintPersistenceService.saveSourcingConstraintEntities(any()))
        .thenReturn(List.of(sourcingConstraintEntities));
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            anyString(), anyString(), any()))
        .thenReturn(List.of(testUtil.getSourcingConstraintEntity()));
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityById(
            anyLong()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));

    OptimizationRuleUIResponse response =
        namedOptimizationStrategyService.processEditOptimizationRuleUI(
            TestUtil.ORG_ID, TestUtil.OPTIMIZATION_STRATEGY_ID, requestBody);

    assertEquals(List.of(), response.getConstraints());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyById(anyLong());
  }

  @Test
  @DisplayName("When new constraint is added to optimization rule")
  void processEditOptimizationRuleUIWithNewConstraintTest()
      throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    NamedOptimizationStrategyDomainDto updatedResponse =
        testUtil.getUpdatedNamedOptimizationStrategyEntityUI();
    SourcingConstraintDomainDto sourcingConstraintEntity = testUtil.getSourcingConstraintEntity();

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyById(anyLong()))
        .thenReturn(Optional.of(namedOptimizationStrategyEntity));
    when(namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
            any(NamedOptimizationStrategyDomainDto.class)))
        .thenReturn(updatedResponse);
    when(groupDefinitionPersistenceService.fetchGroupDefinitionById(anyLong()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingConstraintPersistenceService.saveSourcingConstraintEntities(any()))
        .thenReturn(List.of(sourcingConstraintEntity));
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            anyString(), anyString(), any()))
        .thenReturn(List.of(sourcingConstraintEntity));
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityById(
            anyLong()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));

    OptimizationRuleUIResponse response =
        namedOptimizationStrategyService.processEditOptimizationRuleUI(
            TestUtil.ORG_ID,
            TestUtil.OPTIMIZATION_STRATEGY_ID,
            testUtil.getOptimizationRuleUpdationUIRequest());

    assertEquals(namedOptimizationStrategyEntity.getId(), response.getOptimizationRuleId());
    assertEquals(updatedResponse.getOptimizationStrategyName(), response.getOptimizationRuleName());
    verify(sourcingConstraintPersistenceService, times(1)).saveSourcingConstraintEntities(any());
  }

  @Test
  @DisplayName("When optimizationRuleId is invalid")
  void processEditOptimizationRuleUIWithInvalidOptimizationRuleIdTest()
      throws PromiseEngineException {
    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyById(anyLong()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.processEditOptimizationRuleUI(
                  TestUtil.ORG_ID,
                  TestUtil.OPTIMIZATION_STRATEGY_ID,
                  testUtil.getOptimizationRuleUpdationUIRequest());
            });
    assertEquals("Optimization Rule not found for given optimizationRuleId", ex.getMessage());
    verify(sourcingConstraintPersistenceService, times(0))
        .saveSourcingConstraintEntities(
            Collections.singletonList(any(SourcingConstraintDomainDto.class)));
  }

  @Test
  void createOptimizationRuleUITest() throws PromiseEngineException, CommonServiceException {
    OptimizationStrategyUIRequest request = testUtil.getOptimizationStrategyUIRequest();
    GroupDefinitionDomainDto groupDefinitionEntity = testUtil.getGroupDefinitionEntity();
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    when(groupDefinitionService.processAddGroupDefinition(any()))
        .thenReturn(testUtil.getGroupDefinitionResponse());
    when(namedOptimizationStrategyPersistenceService
            .fetchOptimizationStrategyByOrgIdAndStrategyName(anyString(), anyString()))
        .thenReturn(List.of());

    when(groupDefinitionPersistenceService.fetchGroupDefinitionListByOrgIdAndName(
            anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityById(
            anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(groupDefinitionEntity));
    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of());

    when(namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
            any(NamedOptimizationStrategyDomainDto.class)))
        .thenReturn(namedOptimizationStrategyEntity);

    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            anyString(), anyString(), any()))
        .thenReturn(List.of());
    when(sourcingConstraintPersistenceService.saveSourcingConstraintEntity(
            any(SourcingConstraintDomainDto.class)))
        .thenReturn(testUtil.getSourcingConstraintEntity());

    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));

    OptimizationRuleUIResponse response =
        namedOptimizationStrategyService.createOptimizationRuleUI(TestUtil.ORG_ID, request);

    Long sourcingAttributeDefinitionId = Long.valueOf(response.getSourcingAttributesDefinitionId());

    assertEquals(namedOptimizationStrategyEntity.getId(), response.getOptimizationRuleId());
    assertEquals(
        groupDefinitionEntity.getSourcingAttributesDefinitionId(), sourcingAttributeDefinitionId);

    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());

    verify(sourcingConstraintPersistenceService, times(1))
        .fetchByOrgIdAndGroupIdAndConstraint(anyString(), anyString(), any());
  }

  @Test
  void createOptimizationRuleUITestWithWeightageStrategy()
      throws PromiseEngineException, CommonServiceException {
    OptimizationStrategyUIRequest request = testUtil.getOptimizationStrategyUIRequest2();
    GroupDefinitionDomainDto groupDefinitionEntity = testUtil.getGroupDefinitionEntity();
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();

    when(namedOptimizationStrategyPersistenceService
            .fetchOptimizationStrategyByOrgIdAndStrategyName(anyString(), anyString()))
        .thenReturn(List.of());

    when(groupDefinitionPersistenceService
            .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of());
    when(groupDefinitionPersistenceService.fetchGroupDefinitionListByOrgIdAndName(
            anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityById(
            anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(groupDefinitionService.processAddGroupDefinition(any()))
        .thenReturn(testUtil.getGroupDefinitionResponse());

    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(groupDefinitionEntity));
    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of());

    when(namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
            any(NamedOptimizationStrategyDomainDto.class)))
        .thenReturn(namedOptimizationStrategyEntity);

    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            anyString(), anyString(), any()))
        .thenReturn(List.of());
    when(sourcingConstraintPersistenceService.saveSourcingConstraintEntity(
            any(SourcingConstraintDomainDto.class)))
        .thenReturn(testUtil.getSourcingConstraintEntity());

    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));

    OptimizationRuleUIResponse response =
        namedOptimizationStrategyService.createOptimizationRuleUI(TestUtil.ORG_ID, request);

    Long sourcingAttributeDefinitionId = Long.valueOf(response.getSourcingAttributesDefinitionId());

    assertEquals(namedOptimizationStrategyEntity.getId(), response.getOptimizationRuleId());
    assertEquals(
        groupDefinitionEntity.getSourcingAttributesDefinitionId(), sourcingAttributeDefinitionId);

    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());

    verify(sourcingConstraintPersistenceService, times(1))
        .fetchByOrgIdAndGroupIdAndConstraint(anyString(), anyString(), any());
  }

  @Test
  void createOptimizationRuleUITestWithoutConstraints()
      throws PromiseEngineException, CommonServiceException {
    OptimizationStrategyUIRequest request = testUtil.getOptimizationStrategyUIRequest3();
    GroupDefinitionDomainDto groupDefinitionEntity = testUtil.getGroupDefinitionEntity();
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();

    when(namedOptimizationStrategyPersistenceService
            .fetchOptimizationStrategyByOrgIdAndStrategyName(anyString(), anyString()))
        .thenReturn(List.of());

    when(groupDefinitionPersistenceService.fetchGroupDefinitionListByOrgIdAndName(
            anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityById(
            anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(groupDefinitionService.processAddGroupDefinition(any()))
        .thenReturn(testUtil.getGroupDefinitionResponse());

    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(groupDefinitionEntity));
    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of());

    when(namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
            any(NamedOptimizationStrategyDomainDto.class)))
        .thenReturn(namedOptimizationStrategyEntity);

    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));

    OptimizationRuleUIResponse response =
        namedOptimizationStrategyService.createOptimizationRuleUI(TestUtil.ORG_ID, request);

    Long sourcingAttributeDefinitionId = Long.valueOf(response.getSourcingAttributesDefinitionId());

    assertEquals(namedOptimizationStrategyEntity.getId(), response.getOptimizationRuleId());
    assertEquals(
        groupDefinitionEntity.getSourcingAttributesDefinitionId(), sourcingAttributeDefinitionId);
    assertEquals(0, response.getConstraints().size());
    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void createOptimizationRuleUITestWhenConstraintsIsEmpty()
      throws PromiseEngineException, CommonServiceException {
    OptimizationStrategyUIRequest request = testUtil.getOptimizationStrategyUIRequest4();
    GroupDefinitionDomainDto groupDefinitionEntity = testUtil.getGroupDefinitionEntity();
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();

    when(namedOptimizationStrategyPersistenceService
            .fetchOptimizationStrategyByOrgIdAndStrategyName(anyString(), anyString()))
        .thenReturn(List.of());

    when(groupDefinitionPersistenceService.fetchGroupDefinitionListByOrgIdAndName(
            anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityById(
            anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(groupDefinitionService.processAddGroupDefinition(any()))
        .thenReturn(testUtil.getGroupDefinitionResponse());
    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(groupDefinitionEntity));
    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of());

    when(namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
            any(NamedOptimizationStrategyDomainDto.class)))
        .thenReturn(namedOptimizationStrategyEntity);

    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));

    OptimizationRuleUIResponse response =
        namedOptimizationStrategyService.createOptimizationRuleUI(TestUtil.ORG_ID, request);

    Long sourcingAttributeDefinitionId = Long.valueOf(response.getSourcingAttributesDefinitionId());

    assertEquals(namedOptimizationStrategyEntity.getId(), response.getOptimizationRuleId());
    assertEquals(
        groupDefinitionEntity.getSourcingAttributesDefinitionId(), sourcingAttributeDefinitionId);
    assertEquals(0, response.getConstraints().size());
    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void createOptimizationRuleUIExceptionTestGroupDefinitionAlreadyExists()
      throws PromiseEngineException, CommonServiceException {
    when(groupDefinitionService.processAddGroupDefinition(any()))
        .thenThrow(
            new CommonServiceException(
                "Group already exist for given orgId , sourcingAttributesDefinitionId, reqAttributesValue and optionalAttributesValue.",
                HttpStatus.BAD_REQUEST,
                0X1771,
                null));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.createOptimizationRuleUI(
                  TestUtil.ORG_ID, testUtil.getOptimizationStrategyUIRequest());
            });
    assertEquals(
        "Group already exist for given orgId , sourcingAttributesDefinitionId, reqAttributesValue and optionalAttributesValue.",
        ex.getMessage());
  }

  @Test
  void createOptimizationRuleUIExceptionTestOptimizationStrategyAlreadyExists()
      throws PromiseEngineException, CommonServiceException {
    when(namedOptimizationStrategyPersistenceService
            .fetchOptimizationStrategyByOrgIdAndStrategyName(anyString(), anyString()))
        .thenReturn(List.of());
    when(groupDefinitionPersistenceService.fetchGroupDefinitionListByOrgIdAndName(
            anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityById(
            anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(groupDefinitionService.processAddGroupDefinition(any()))
        .thenReturn(testUtil.getGroupDefinitionResponse());

    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of(testUtil.getNamedOptimizationStrategyEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.createOptimizationRuleUI(
                  TestUtil.ORG_ID, testUtil.getOptimizationStrategyUIRequest());
            });
    assertEquals(
        "Named optimization strategy already associated for given orgId and groupId",
        ex.getMessage());

    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void createOptimizationRuleUIExceptionTestSourcingConstraintAlreadyExists()
      throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    when(namedOptimizationStrategyPersistenceService
            .fetchOptimizationStrategyByOrgIdAndStrategyName(anyString(), anyString()))
        .thenReturn(List.of());

    when(groupDefinitionPersistenceService.fetchGroupDefinitionListByOrgIdAndName(
            anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityById(
            anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntityForOptimization(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(groupDefinitionService.processAddGroupDefinition(any()))
        .thenReturn(testUtil.getGroupDefinitionResponse());

    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(List.of());

    when(namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
            any(NamedOptimizationStrategyDomainDto.class)))
        .thenReturn(namedOptimizationStrategyEntity);

    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            anyString(), anyString(), any()))
        .thenReturn(List.of(testUtil.getSourcingConstraintEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.createOptimizationRuleUI(
                  TestUtil.ORG_ID, testUtil.getOptimizationStrategyUIRequest());
            });
    assertEquals("This constraint is already defined for given orgId", ex.getMessage());

    verify(sourcingConstraintPersistenceService, times(1))
        .fetchByOrgIdAndGroupIdAndConstraint(anyString(), anyString(), any());
  }

  @Test
  void createOptimizationRuleUIWithInactiveSourcingAttributeDefinitionExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(groupDefinitionService.processAddGroupDefinition(any()))
        .thenThrow(
            new CommonServiceException(
                "Invalid sourcing attributes definition for OPTIMIZATION scope/ Sourcing  attributes definition exists but not in ACTIVE status",
                HttpStatus.BAD_REQUEST,
                0x1771,
                null));
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.createOptimizationRuleUI(
                  TestUtil.ORG_ID, testUtil.getOptimizationStrategyUIRequest());
            });
    assertEquals(
        "Invalid sourcing attributes definition for OPTIMIZATION scope/ Sourcing  attributes definition exists but not in ACTIVE status",
        ex.getMessage());

    verify(groupDefinitionService, times(1)).processAddGroupDefinition(any());
  }

  @Test
  void createOptimizationRuleUIWhenSourcingAttributeDefinitionDoesNotExistExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(groupDefinitionService.processAddGroupDefinition(any()))
        .thenThrow(
            new CommonServiceException(
                "Invalid sourcing attributes definition for OPTIMIZATION scope/ Sourcing  attributes definition exists but not in ACTIVE status",
                HttpStatus.BAD_REQUEST,
                0x1771,
                null));
    when(groupDefinitionPersistenceService.fetchGroupDefinitionListByOrgIdAndName(
            anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.createOptimizationRuleUI(
                  TestUtil.ORG_ID, testUtil.getOptimizationStrategyUIRequest());
            });
    assertEquals(
        "Invalid sourcing attributes definition for OPTIMIZATION scope/ Sourcing  attributes definition exists but not in ACTIVE status",
        ex.getMessage());
  }

  @Test
  void createOptimizationRuleUIWhenAllRequiredAttributesNotProvidedExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(groupDefinitionService.processAddGroupDefinition(any()))
        .thenThrow(
            new CommonServiceException(
                "Can't add the group definition as all the required attributes values are not present",
                HttpStatus.BAD_REQUEST,
                null,
                null));
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.createOptimizationRuleUI(
                  TestUtil.ORG_ID, testUtil.getOptimizationStrategyUIRequest5());
            });
    assertEquals(
        "Can't add the group definition as all the required attributes values are not present",
        ex.getMessage());
  }

  @Test
  void processDeleteMultipleOptimizationStrategyTest()
      throws PromiseEngineException, CommonServiceException {
    DeleteOptimizationRulesRequest optimisationRuleIds =
        new DeleteOptimizationRulesRequest(List.of(TestUtil.OPTIMIZATION_STRATEGY_ID));
    List<NamedOptimizationStrategyDomainDto> strategies =
        List.of(testUtil.getNamedOptimizationStrategyEntity());

    GroupDefinitionDomainDto groupDefinitionEntity = testUtil.getGroupDefinitionEntity();

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    List<SourcingConstraintDomainDto> sourcingConstraintEntities =
        List.of(testUtil.getSourcingConstraintEntity());

    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyById(any()))
        .thenReturn(Optional.of(testUtil.getNamedOptimizationStrategyEntity()));

    when(groupDefinitionPersistenceService.fetchGroupDefinitionById(any()))
        .thenReturn(Optional.of(groupDefinitionEntity));

    when(sourcingAttributesDefinitionDomain.getSourcingRuleAttributesDefinitionEntityById(
            anyLong()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));

    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));

    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(any(), any()))
        .thenReturn(sourcingConstraintEntities);
    doNothing().when(namedOptimizationStrategyPersistenceService).deleteByIdIn(any());
    doNothing().when(sourcingConstraintPersistenceService).deleteByIdIn(any());
    doNothing().when(groupDefinitionPersistenceService).deleteByIdIn(any());

    List<OptimizationRuleUIResponse> response =
        namedOptimizationStrategyService.processDeleteMultipleOptimizationStrategy(
            TestUtil.ORG_ID, optimisationRuleIds);

    assertEquals(1, response.size());
    assertEquals(strategies.get(0).getId(), response.get(0).getOptimizationRuleId());
    assertEquals(TestUtil.ORG_ID, response.get(0).getOrgId());
  }

  @Test
  void processDeleteMultipleOptimizationStrategyExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    DeleteOptimizationRulesRequest optimisationRuleIds =
        new DeleteOptimizationRulesRequest(List.of(TestUtil.OPTIMIZATION_STRATEGY_ID));
    when(namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyById(anyLong()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              namedOptimizationStrategyService.processDeleteMultipleOptimizationStrategy(
                  TestUtil.ORG_ID, optimisationRuleIds);
            });
    assertEquals("No optimization strategy entity found for given id", ex.getMessage());
    verify(namedOptimizationStrategyPersistenceService, times(1))
        .fetchOptimizationStrategyById(anyLong());
  }
}
