/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingRulesConfigurationDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingRulesConfigurationEntity;
import com.nextuple.promise.sourcing.rule.persistence.mapper.SourcingRulesConfigurationEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.SourcingRulesConfigurationRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class SourcingRulesConfigurationDomainTest {

  @InjectMocks
  private SourcingRulesConfigurationPersistenceServiceImpl
      sourcingRulesConfigurationPersistenceService;

  @Mock private SourcingRulesConfigurationRepository sourcingRulesConfigurationRepository;

  @Mock private SourcingRulesConfigurationEntityMapper sourcingRulesConfigurationEntityMapper;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        sourcingRulesConfigurationPersistenceService,
        "repository",
        sourcingRulesConfigurationRepository);
    ReflectionTestUtils.setField(
        sourcingRulesConfigurationPersistenceService,
        "mapper",
        sourcingRulesConfigurationEntityMapper);
  }

  @Test
  void saveSourcingRuleTest() throws PromiseEngineException {
    SourcingRulesConfigurationEntity sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto =
        testUtil.getSourcingRulesConfigurationDomainDto();
    when(sourcingRulesConfigurationRepository.save(any(SourcingRulesConfigurationEntity.class)))
        .thenReturn(sourcingRulesConfigurationEntity);
    when(sourcingRulesConfigurationEntityMapper.toDomain(
            any(SourcingRulesConfigurationEntity.class)))
        .thenReturn(sourcingRulesConfigurationDomainDto);
    when(sourcingRulesConfigurationEntityMapper.toEntity(
            any(SourcingRulesConfigurationDomainDto.class)))
        .thenReturn(sourcingRulesConfigurationEntity);
    SourcingRulesConfigurationDomainDto saved_result =
        sourcingRulesConfigurationPersistenceService.saveSourcingRule(
            sourcingRulesConfigurationDomainDto);
    assertEquals(sourcingRulesConfigurationDomainDto, saved_result);
    verify(sourcingRulesConfigurationRepository, times(1)).save(any());
  }

  @Test
  void saveSourcingRuleExceptionTest() throws PromiseEngineException {
    SourcingRulesConfigurationEntity sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto =
        testUtil.getSourcingRulesConfigurationDomainDto();
    when(sourcingRulesConfigurationEntityMapper.toEntity(
            any(SourcingRulesConfigurationDomainDto.class)))
        .thenReturn(sourcingRulesConfigurationEntity);
    when(sourcingRulesConfigurationRepository.save(any(SourcingRulesConfigurationEntity.class)))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingRulesConfigurationPersistenceService.saveSourcingRule(
                  sourcingRulesConfigurationDomainDto);
            });
    assertEquals("Unable to save sourcing rule", ex.getMessage());
  }

  @Test
  void getSourcingRuleByIdTest() throws PromiseEngineException {
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto =
        testUtil.getSourcingRulesConfigurationDomainDto();
    when(sourcingRulesConfigurationRepository.findById(anyLong()))
        .thenReturn(Optional.ofNullable(testUtil.getSourcingRulesEntity()));
    when(sourcingRulesConfigurationEntityMapper.toDomain(
            any(SourcingRulesConfigurationEntity.class)))
        .thenReturn(sourcingRulesConfigurationDomainDto);
    Optional<SourcingRulesConfigurationDomainDto> saved_result =
        sourcingRulesConfigurationPersistenceService.getSourcingRuleById(TestUtil.SOURCING_RULE_ID);
    assertEquals(sourcingRulesConfigurationDomainDto, saved_result.get());
    verify(sourcingRulesConfigurationRepository, times(1)).findById(anyLong());
  }

  @Test
  void getSourcingRuleByIdExceptionTest() {
    when(sourcingRulesConfigurationRepository.findById(anyLong()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingRulesConfigurationPersistenceService.getSourcingRuleById(
                  TestUtil.SOURCING_RULE_ID);
            });
    assertEquals("Unable to find sourcing rule by id", ex.getMessage());
    verify(sourcingRulesConfigurationRepository, times(1)).findById(anyLong());
  }

  @Test
  @DisplayName("Happy path for getSourcingRuleByOrgIdAndSourcingRule")
  void getSourcingRuleByOrgIdAndSourcingRuleTest() throws PromiseEngineException {
    SourcingRulesConfigurationEntity sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto =
        testUtil.getSourcingRulesConfigurationDomainDto();
    when(sourcingRulesConfigurationRepository.findByOrgIdAndSourcingRule(anyString(), anyString()))
        .thenReturn(Optional.of(List.of(sourcingRulesConfigurationEntity)));
    when(sourcingRulesConfigurationEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(sourcingRulesConfigurationDomainDto));
    Optional<List<SourcingRulesConfigurationDomainDto>> result =
        sourcingRulesConfigurationPersistenceService.getSourcingRuleByOrgIdAndSourcingRule(
            TestUtil.ORG_ID, TestUtil.SOURCING_RULE);
    assertEquals(sourcingRulesConfigurationDomainDto, result.get().get(0));
    verify(sourcingRulesConfigurationRepository, times(1))
        .findByOrgIdAndSourcingRule(anyString(), anyString());
  }

  @Test
  @DisplayName("Exception in getSourcingRuleByOrgIdAndSourcingRule")
  void getSourcingRuleByOrgIdAndSourcingRuleExceptionTest() throws PromiseEngineException {
    when(sourcingRulesConfigurationRepository.findByOrgIdAndSourcingRule(anyString(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () ->
                sourcingRulesConfigurationPersistenceService.getSourcingRuleByOrgIdAndSourcingRule(
                    TestUtil.ORG_ID, TestUtil.SOURCING_RULE));

    assertEquals("Unable to find sourcing rule by orgId and sourcing rule", ex.getMessage());
  }

  @Test
  void getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleTest()
      throws PromiseEngineException {
    SourcingRulesConfigurationEntity sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto =
        testUtil.getSourcingRulesConfigurationDomainDto();
    when(sourcingRulesConfigurationRepository
            .findByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleStartsWith(
                anyString(), anyLong(), anyString()))
        .thenReturn(List.of(sourcingRulesConfigurationEntity));
    when(sourcingRulesConfigurationEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(sourcingRulesConfigurationDomainDto));
    List<SourcingRulesConfigurationDomainDto> saved_result =
        sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                TestUtil.ORG_ID,
                TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
                TestUtil.SOURCING_RULE);
    assertEquals(sourcingRulesConfigurationDomainDto, saved_result.get(0));
    verify(sourcingRulesConfigurationRepository, times(1))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleStartsWith(
            anyString(), anyLong(), anyString());
  }

  @Test
  void getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleExceptionTest() {

    when(sourcingRulesConfigurationRepository
            .findByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleStartsWith(
                anyString(), anyLong(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingRulesConfigurationPersistenceService
                  .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                      TestUtil.ORG_ID,
                      TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
                      TestUtil.SOURCING_RULE);
            });

    assertEquals(
        "Unable to find sourcing rule list by orgId and sourcing attributes definition id and sourcing rule",
        ex.getMessage());
  }

  @Test
  void getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRuleTest()
      throws PromiseEngineException {
    SourcingRulesConfigurationEntity sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto =
        testUtil.getSourcingRulesConfigurationDomainDto();
    when(sourcingRulesConfigurationRepository
            .findBySourcingAttributesDefinitionIdAndSourcingRuleAndOrgId(
                anyLong(), anyString(), anyString()))
        .thenReturn(Optional.of(sourcingRulesConfigurationEntity));
    when(sourcingRulesConfigurationEntityMapper.toDomain(
            any(SourcingRulesConfigurationEntity.class)))
        .thenReturn(sourcingRulesConfigurationDomainDto);
    Optional<SourcingRulesConfigurationDomainDto> saved_result =
        sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
                TestUtil.ORG_ID,
                TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
                TestUtil.SOURCING_RULE);
    assertEquals(sourcingRulesConfigurationDomainDto, saved_result.get());
    verify(sourcingRulesConfigurationRepository, times(1))
        .findBySourcingAttributesDefinitionIdAndSourcingRuleAndOrgId(
            anyLong(), anyString(), anyString());
  }

  @Test
  void
      getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRuleExceptionTest() {

    when(sourcingRulesConfigurationRepository
            .findBySourcingAttributesDefinitionIdAndSourcingRuleAndOrgId(
                anyLong(), anyString(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingRulesConfigurationPersistenceService
                  .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
                      TestUtil.ORG_ID,
                      TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
                      TestUtil.SOURCING_RULE);
            });

    assertEquals(
        "Unable to find sourcing rule by orgId and sourcing attributes definition id and sourcing rule",
        ex.getMessage());
  }

  @Test
  void getSourcingRulesByOrgIdAndSourcingRuleNameTest() throws PromiseEngineException {
    SourcingRulesConfigurationEntity sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto =
        testUtil.getSourcingRulesConfigurationDomainDto();
    when(sourcingRulesConfigurationRepository.findByOrgIdAndSourcingRuleName(
            anyString(), anyString()))
        .thenReturn(sourcingRulesConfigurationEntity);
    when(sourcingRulesConfigurationEntityMapper.toDomain(
            any(SourcingRulesConfigurationEntity.class)))
        .thenReturn(sourcingRulesConfigurationDomainDto);
    SourcingRulesConfigurationDomainDto response =
        sourcingRulesConfigurationPersistenceService.getSourcingRulesByOrgIdAndSourcingRuleName(
            TestUtil.ORG_ID, TestUtil.SOURCING_RULE_NAME);
    assertEquals(sourcingRulesConfigurationDomainDto, response);
    verify(sourcingRulesConfigurationRepository, times(1))
        .findByOrgIdAndSourcingRuleName(anyString(), anyString());
  }

  @Test
  void getSourcingRulesByOrgIdAndSourcingRuleNameExceptionTest() throws PromiseEngineException {
    when(sourcingRulesConfigurationRepository.findByOrgIdAndSourcingRuleName(
            anyString(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingRulesConfigurationPersistenceService
                  .getSourcingRulesByOrgIdAndSourcingRuleName(
                      TestUtil.ORG_ID, TestUtil.SOURCING_RULE_NAME);
            });

    assertEquals("Unable to find sourcing rule by orgId and sourcing rule name", ex.getMessage());
  }

  @Test
  void getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleNameTest()
      throws PromiseEngineException {
    SourcingRulesConfigurationEntity sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto =
        testUtil.getSourcingRulesConfigurationDomainDto();
    when(sourcingRulesConfigurationRepository
            .findByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
                anyString(), anyLong(), anyString()))
        .thenReturn(sourcingRulesConfigurationEntity);
    when(sourcingRulesConfigurationEntityMapper.toDomain(
            any(SourcingRulesConfigurationEntity.class)))
        .thenReturn(sourcingRulesConfigurationDomainDto);
    SourcingRulesConfigurationDomainDto response =
        sourcingRulesConfigurationPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
                TestUtil.ORG_ID,
                TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
                TestUtil.SOURCING_RULE_NAME);
    assertEquals(sourcingRulesConfigurationDomainDto, response);
    verify(sourcingRulesConfigurationRepository, times(1))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
            anyString(), anyLong(), anyString());
  }

  @Test
  void getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleNameExceptionTest()
      throws PromiseEngineException {
    when(sourcingRulesConfigurationRepository
            .findByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
                anyString(), anyLong(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingRulesConfigurationPersistenceService
                  .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
                      TestUtil.ORG_ID,
                      TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
                      TestUtil.SOURCING_RULE_NAME);
            });

    assertEquals(
        "Unable to find sourcing rule by orgId, sourcingAttributesDefinitionId and sourcing rule name",
        ex.getMessage());
  }

  @Test
  void deleteSourcingRuleTest() throws PromiseEngineException {
    SourcingRulesConfigurationEntity sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto =
        testUtil.getSourcingRulesConfigurationDomainDto();
    when(sourcingRulesConfigurationEntityMapper.toEntity(
            any(SourcingRulesConfigurationDomainDto.class)))
        .thenReturn(sourcingRulesConfigurationEntity);
    doNothing()
        .when(sourcingRulesConfigurationRepository)
        .delete(any(SourcingRulesConfigurationEntity.class));
    sourcingRulesConfigurationPersistenceService.deleteSourcingRule(
        sourcingRulesConfigurationDomainDto);
    verify(sourcingRulesConfigurationRepository, times(1)).delete(any());
  }

  @Test
  void deleteSourcingRuleExceptionTest() {
    SourcingRulesConfigurationEntity sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto =
        testUtil.getSourcingRulesConfigurationDomainDto();
    when(sourcingRulesConfigurationEntityMapper.toEntity(
            any(SourcingRulesConfigurationDomainDto.class)))
        .thenReturn(sourcingRulesConfigurationEntity);
    doThrow(new RuntimeException("error"))
        .when(sourcingRulesConfigurationRepository)
        .delete(any(SourcingRulesConfigurationEntity.class));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingRulesConfigurationPersistenceService.deleteSourcingRule(
                  sourcingRulesConfigurationDomainDto);
            });
    assertEquals("Unable to delete sourcing rule", ex.getMessage());
  }

  @Test
  void getSourcingRuleByIdAndOrgIdTest() throws PromiseEngineException {
    SourcingRulesConfigurationEntity sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto =
        testUtil.getSourcingRulesConfigurationDomainDto();
    when(sourcingRulesConfigurationRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(sourcingRulesConfigurationEntity));
    when(sourcingRulesConfigurationEntityMapper.toDomain(
            any(SourcingRulesConfigurationEntity.class)))
        .thenReturn(sourcingRulesConfigurationDomainDto);
    Optional<SourcingRulesConfigurationDomainDto> result =
        sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
            TestUtil.SOURCING_RULE_ID, TestUtil.ORG_ID);

    assertEquals(sourcingRulesConfigurationDomainDto, result.get());
    verify(sourcingRulesConfigurationRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getSourcingRuleByIdAndOrgIdExceptionTest() {
    when(sourcingRulesConfigurationRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () ->
                sourcingRulesConfigurationPersistenceService.getSourcingRuleByIdAndOrgId(
                    TestUtil.SOURCING_RULE_ID, TestUtil.ORG_ID));

    assertEquals("Unable to find sourcing rule by id and orgId", ex.getMessage());
  }

  @Test
  void deleteMultipleSourcingRulesTest() throws PromiseEngineException {
    SourcingRulesConfigurationEntity sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto =
        testUtil.getSourcingRulesConfigurationDomainDto();
    doNothing().when(sourcingRulesConfigurationRepository).deleteAll(any());
    when(sourcingRulesConfigurationEntityMapper.toEntity(anyList()))
        .thenReturn(List.of(sourcingRulesConfigurationEntity));
    sourcingRulesConfigurationPersistenceService.deleteMultipleSourcingRules(
        List.of(sourcingRulesConfigurationDomainDto));
    verify(sourcingRulesConfigurationRepository, times(1)).deleteAll(any());
  }

  @Test
  void deleteMultipleSourcingRulesExceptionTest() {
    SourcingRulesConfigurationEntity sourcingRulesConfigurationEntity =
        testUtil.getSourcingRulesEntity();
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto =
        testUtil.getSourcingRulesConfigurationDomainDto();
    when(sourcingRulesConfigurationEntityMapper.toEntity(anyList()))
        .thenReturn(List.of(sourcingRulesConfigurationEntity));
    doThrow(new RuntimeException("error"))
        .when(sourcingRulesConfigurationRepository)
        .deleteAll(any());

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingRulesConfigurationPersistenceService.deleteMultipleSourcingRules(
                  List.of(sourcingRulesConfigurationDomainDto));
            });
    assertEquals("Unable to delete multiple sourcing rule", ex.getMessage());
  }
}
