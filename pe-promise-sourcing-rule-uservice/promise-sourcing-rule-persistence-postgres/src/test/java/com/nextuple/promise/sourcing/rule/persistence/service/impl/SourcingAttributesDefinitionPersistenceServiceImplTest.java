/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.projection.SourcingRuleByNodeGroupCountProjection;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributesDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingAttributesDefinitionEntity;
import com.nextuple.promise.sourcing.rule.persistence.mapper.SourcingAttributesDefinitionEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.SourcingAttributesDefinitionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.test.util.ReflectionTestUtils;

class SourcingAttributesDefinitionPersistenceServiceImplTest {

  @InjectMocks
  private SourcingAttributesDefinitionPersistenceServiceImpl
      sourcingAttributesDefinitionPersistenceService;

  @Mock private SourcingAttributesDefinitionRepository sourcingRuleAttributesDefinitionRepository;

  @Mock private SourcingAttributesDefinitionEntityMapper sourcingAttributesDefinitionEntityMapper;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        sourcingAttributesDefinitionPersistenceService,
        "repository",
        sourcingRuleAttributesDefinitionRepository);
    ReflectionTestUtils.setField(
        sourcingAttributesDefinitionPersistenceService,
        "mapper",
        sourcingAttributesDefinitionEntityMapper);
  }

  @Test
  void saveSourcingRuleAttributesDefinitionTest() throws PromiseEngineException {
    SourcingAttributesDefinitionEntity sourcingRuleAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionDomainDto =
        testUtil.getSourcingAttributesDefinitionDomainDto(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingRuleAttributesDefinitionRepository.save(
            any(SourcingAttributesDefinitionEntity.class)))
        .thenReturn(sourcingRuleAttributesDefinitionEntity);
    when(sourcingAttributesDefinitionEntityMapper.toDomain(
            any(SourcingAttributesDefinitionEntity.class)))
        .thenReturn(sourcingAttributesDefinitionDomainDto);
    when(sourcingAttributesDefinitionEntityMapper.toEntity(
            any(SourcingAttributesDefinitionDomainDto.class)))
        .thenReturn(sourcingRuleAttributesDefinitionEntity);
    SourcingAttributesDefinitionDomainDto saved_result =
        sourcingAttributesDefinitionPersistenceService.saveSourcingRuleAttributesDefinitionEntity(
            sourcingAttributesDefinitionDomainDto);
    assertEquals(sourcingAttributesDefinitionDomainDto, saved_result);
    verify(sourcingRuleAttributesDefinitionRepository, times(1)).save(any());
  }

  @Test
  void saveSourcingRuleAttributesDefinitionExceptionTest() throws PromiseEngineException {
    when(sourcingRuleAttributesDefinitionRepository.save(
            any(SourcingAttributesDefinitionEntity.class)))
        .thenThrow(new RuntimeException("error"));
    when(sourcingAttributesDefinitionEntityMapper.toEntity(
            any(SourcingAttributesDefinitionDomainDto.class)))
        .thenReturn(
            testUtil.getSourcingRuleAttributesDefinitionEntity(
                SourcingAttributesDefinitionStatus.ACTIVE));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingAttributesDefinitionPersistenceService
                  .saveSourcingRuleAttributesDefinitionEntity(
                      testUtil.getSourcingAttributesDefinitionDomainDto(
                          SourcingAttributesDefinitionStatus.ACTIVE));
            });
    assertEquals("Unable to save sourcing rule attributes definition entity", ex.getMessage());
  }

  @Test
  void getSourcingAttributesDefinitionByIdTest() throws PromiseEngineException {
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionDomainDto =
        testUtil.getSourcingAttributesDefinitionDomainDto(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingRuleAttributesDefinitionRepository.findById(anyLong()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(sourcingAttributesDefinitionEntityMapper.toDomain(
            any(SourcingAttributesDefinitionEntity.class)))
        .thenReturn(sourcingAttributesDefinitionDomainDto);
    Optional<SourcingAttributesDefinitionDomainDto> saved_result =
        sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(
                TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID);
    assertEquals(sourcingAttributesDefinitionDomainDto, saved_result.get());
    verify(sourcingRuleAttributesDefinitionRepository, times(1)).findById(anyLong());
  }

  @Test
  void getSourcingAttributesDefinitionByIdExceptionTest() throws PromiseEngineException {
    when(sourcingRuleAttributesDefinitionRepository.findById(anyLong()))
        .thenThrow(new RuntimeException("error"));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingAttributesDefinitionPersistenceService
                  .getSourcingRuleAttributesDefinitionEntityById(
                      TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID);
            });
    assertEquals(
        "Unable to find sourcing rule attributes definition entity by id", ex.getMessage());
    verify(sourcingRuleAttributesDefinitionRepository, times(1)).findById(anyLong());
  }

  @Test
  void getSourcingRuleAttributesDefinitionByOrgIdAndNameTest() throws PromiseEngineException {
    SourcingAttributesDefinitionEntity sourcingRuleAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionDomainDto =
        testUtil.getSourcingAttributesDefinitionDomainDto(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(sourcingAttributesDefinitionDomainDto));
    when(sourcingRuleAttributesDefinitionRepository.findByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of(sourcingRuleAttributesDefinitionEntity));
    List<SourcingAttributesDefinitionDomainDto> saved_result =
        sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(
                TestUtil.ORG_ID, TestUtil.SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME);
    assertEquals(sourcingAttributesDefinitionDomainDto, saved_result.get(0));
    verify(sourcingRuleAttributesDefinitionRepository, times(1))
        .findByOrgIdAndName(anyString(), anyString());
  }

  @Test
  void getSourcingRuleAttributesDefinitionByOrgIdAndNameExceptionTest()
      throws PromiseEngineException {
    when(sourcingRuleAttributesDefinitionRepository.findByOrgIdAndName(anyString(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingAttributesDefinitionPersistenceService
                  .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(
                      TestUtil.ORG_ID, TestUtil.SOURCING_RULE_ATTRIBUTES_DEFINITION_NAME);
            });
    assertEquals(
        "Unable to fetch sourcing rule attributes definition entity list by orgId and name",
        ex.getMessage());
  }

  @Test
  void getSourcingRuleAttributesDefinitionByOrgIdAndStatusAndScopeTest()
      throws PromiseEngineException {
    SourcingAttributesDefinitionEntity sourcingRuleAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionDomainDto =
        testUtil.getSourcingAttributesDefinitionDomainDto(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingRuleAttributesDefinitionRepository.findByOrgIdAndStatusAndScope(
            anyString(), any(), any()))
        .thenReturn(List.of(sourcingRuleAttributesDefinitionEntity));
    when(sourcingAttributesDefinitionEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(sourcingAttributesDefinitionDomainDto));
    List<SourcingAttributesDefinitionDomainDto> saved_result =
        sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                TestUtil.ORG_ID,
                SourcingAttributesDefinitionStatus.ACTIVE,
                SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    assertEquals(sourcingAttributesDefinitionDomainDto, saved_result.get(0));
    verify(sourcingRuleAttributesDefinitionRepository, times(1))
        .findByOrgIdAndStatusAndScope(anyString(), any(), any());
  }

  @Test
  void getSourcingRuleAttributesDefinitionByOrgIdAndStatusAndScopeExceptionTest()
      throws PromiseEngineException {
    when(sourcingRuleAttributesDefinitionRepository.findByOrgIdAndStatusAndScope(
            anyString(), any(), any()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingAttributesDefinitionPersistenceService
                  .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                      TestUtil.ORG_ID,
                      SourcingAttributesDefinitionStatus.ACTIVE,
                      SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
            });
    assertEquals(
        "Unable to fetch sourcing rule attributes definition entity list by orgId and status and scope",
        ex.getMessage());
  }

  @Test
  void fetchActiveSourcingRuleCountByNodeGroupIdsTest() {
    Long sourcingRuleDefinitionId = 1L;
    ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
    SourcingRuleByNodeGroupCountProjection projection =
        factory.createProjection(SourcingRuleByNodeGroupCountProjection.class);
    projection.setNodeGroup(TestUtil.NODE_GROUP_NAME);
    projection.setCount(5L);
    List<String> nodeGroupEntityIds = testUtil.getNodeGroupEntityIds(String.class, 5);
    List<SourcingRuleByNodeGroupCountProjection> expectedProjectionList = new ArrayList<>();
    expectedProjectionList.add(projection);

    when(sourcingRuleAttributesDefinitionRepository.fetchActiveSourcingRuleCountByNodeGroupIds(
            any(), any()))
        .thenReturn(expectedProjectionList);

    List<SourcingRuleByNodeGroupCountProjection> sourcingRuleByNodeGroupCountProjection =
        sourcingAttributesDefinitionPersistenceService.fetchActiveSourcingRuleCountByNodeGroupIds(
            nodeGroupEntityIds, sourcingRuleDefinitionId);

    assertEquals(expectedProjectionList, sourcingRuleByNodeGroupCountProjection);
    verify(sourcingRuleAttributesDefinitionRepository, times(1))
        .fetchActiveSourcingRuleCountByNodeGroupIds(nodeGroupEntityIds, sourcingRuleDefinitionId);
  }
}
