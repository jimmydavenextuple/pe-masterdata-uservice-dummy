/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service.impl;

import static com.nextuple.promise.sourcing.rule.persistence.service.impl.TestUtil.ORG_ID;
import static com.nextuple.promise.sourcing.rule.persistence.service.impl.TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID;
import static com.nextuple.promise.sourcing.rule.persistence.service.impl.TestUtil.SOURCING_RULE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.persistence.domain.RulesConfigurationDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.entity.RulesConfigurationEntity;
import com.nextuple.promise.sourcing.rule.persistence.mapper.RulesConfigurationEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.RulesConfigurationRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class RulesConfigurationPersistenceServiceImplTest {

  @InjectMocks
  private RulesConfigurationPersistenceServiceImpl rulesConfigurationPersistenceService;

  @Mock private RulesConfigurationRepository rulesConfigurationRepository;

  @Mock private RulesConfigurationEntityMapper rulesConfigurationEntityMapper;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(
        rulesConfigurationPersistenceService, "repository", rulesConfigurationRepository);
    ReflectionTestUtils.setField(
        rulesConfigurationPersistenceService, "mapper", rulesConfigurationEntityMapper);
  }

  @Test
  @DisplayName("Saved Rules Configuration Test - Happy path")
  void saveRulesConfigurationTest() throws PromiseEngineException {
    RulesConfigurationEntity rulesConfigurationEntity = new RulesConfigurationEntity();
    rulesConfigurationEntity.setOrgId("ORG1");
    rulesConfigurationEntity.setRuleName("Rule-1");

    RulesConfigurationDomainDto rulesConfigurationDomainDto = new RulesConfigurationDomainDto();
    rulesConfigurationDomainDto.setOrgId("ORG1");
    rulesConfigurationDomainDto.setRuleName("Rule-1");

    when(rulesConfigurationEntityMapper.toEntity(any(RulesConfigurationDomainDto.class)))
        .thenReturn(rulesConfigurationEntity);
    when(rulesConfigurationRepository.save(any(RulesConfigurationEntity.class)))
        .thenReturn(rulesConfigurationEntity);
    when(rulesConfigurationEntityMapper.toDomain(any(RulesConfigurationEntity.class)))
        .thenReturn(rulesConfigurationDomainDto);

    RulesConfigurationDomainDto savedDomainDto =
        rulesConfigurationPersistenceService.saveRulesConfiguration(rulesConfigurationDomainDto);

    assertNotNull(savedDomainDto);
    assertEquals(rulesConfigurationDomainDto.getOrgId(), savedDomainDto.getOrgId());
    assertEquals(rulesConfigurationDomainDto.getRuleName(), savedDomainDto.getRuleName());

    verify(rulesConfigurationRepository, times(1)).save(any(RulesConfigurationEntity.class));
  }

  @Test
  @DisplayName("Saved Rules Configuration Test - Exception test")
  void saveRulesConfigurationExceptionTest() {
    RulesConfigurationEntity rulesConfigurationEntity = new RulesConfigurationEntity();
    RulesConfigurationDomainDto rulesConfigurationDomainDto = new RulesConfigurationDomainDto();

    when(rulesConfigurationEntityMapper.toEntity(any(RulesConfigurationDomainDto.class)))
        .thenReturn(rulesConfigurationEntity);
    when(rulesConfigurationRepository.save(any(RulesConfigurationEntity.class)))
        .thenThrow(new RuntimeException("Database error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () ->
                rulesConfigurationPersistenceService.saveRulesConfiguration(
                    rulesConfigurationDomainDto));

    assertEquals("Error while saving rules configuration", exception.getMessage());

    verify(rulesConfigurationRepository, times(1)).save(any(RulesConfigurationEntity.class));
  }

  @Test
  @DisplayName("Find Rules By Org Id And Attribute Definition Id And Rule Starts With: Happy Path")
  void findByOrgIdAndAttributeDefinitionIdAndRuleStartsWithTest() throws PromiseEngineException {
    var rulesConfigurationEntity = testUtil.getRulesConfigurationEntity();
    var rulesConfigurationDomainDto = testUtil.getRulesConfigurationDomainDto();

    when(rulesConfigurationEntityMapper.toEntity(any(RulesConfigurationDomainDto.class)))
        .thenReturn(rulesConfigurationEntity);
    when(rulesConfigurationRepository.findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(
            anyString(), anyLong(), anyString()))
        .thenReturn(List.of(rulesConfigurationEntity));
    when(rulesConfigurationEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(rulesConfigurationDomainDto));

    var fetchedDomainDto =
        rulesConfigurationPersistenceService.findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(
            ORG_ID, SOURCING_ATTRIBUTES_DEFINITION_ID, SOURCING_RULE);

    assertNotNull(fetchedDomainDto);
    assertEquals(rulesConfigurationDomainDto.getOrgId(), fetchedDomainDto.getFirst().getOrgId());
    assertEquals(
        rulesConfigurationDomainDto.getRuleName(), fetchedDomainDto.getFirst().getRuleName());

    verify(rulesConfigurationRepository, times(1))
        .findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(anyString(), anyLong(), anyString());
  }

  @Test
  @DisplayName("Find Rules By Org Id And Attribute Definition Id And Rule Starts With: Exception")
  void findByOrgIdAndAttributeDefinitionIdAndRuleStartsWithExceptionTest() {
    when(rulesConfigurationRepository.findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(
            anyString(), anyLong(), anyString()))
        .thenThrow(new RuntimeException("Database error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () ->
                rulesConfigurationPersistenceService
                    .findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(
                        ORG_ID, SOURCING_ATTRIBUTES_DEFINITION_ID, SOURCING_RULE));

    assertEquals("Error while finding rules configuration", exception.getMessage());

    verify(rulesConfigurationRepository, times(1))
        .findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(anyString(), anyLong(), anyString());
  }

  @Test
  @DisplayName("Find Rules By Org Id And Attribute Definition Id And Rule: Happy Path")
  void findByOrgIdAndAttributeDefinitionIdAndRuleTest() throws PromiseEngineException {
    var rulesConfigurationEntity = testUtil.getRulesConfigurationEntity();
    var rulesConfigurationDomainDto = testUtil.getRulesConfigurationDomainDto();

    when(rulesConfigurationEntityMapper.toEntity(any(RulesConfigurationDomainDto.class)))
        .thenReturn(rulesConfigurationEntity);
    when(rulesConfigurationRepository.findByOrgIdAndAttributeDefinitionIdAndRule(
            anyString(), anyLong(), anyString()))
        .thenReturn(Optional.of(rulesConfigurationEntity));
    when(rulesConfigurationEntityMapper.toDomain(any(RulesConfigurationEntity.class)))
        .thenReturn(rulesConfigurationDomainDto);

    var fetchedDomainDto =
        rulesConfigurationPersistenceService.findByOrgIdAndAttributeDefinitionIdAndRule(
            ORG_ID, SOURCING_ATTRIBUTES_DEFINITION_ID, SOURCING_RULE);

    assertNotNull(fetchedDomainDto);
    assertEquals(rulesConfigurationDomainDto.getOrgId(), fetchedDomainDto.get().getOrgId());
    assertEquals(rulesConfigurationDomainDto.getRuleName(), fetchedDomainDto.get().getRuleName());

    verify(rulesConfigurationRepository, times(1))
        .findByOrgIdAndAttributeDefinitionIdAndRule(anyString(), anyLong(), anyString());
  }

  @Test
  @DisplayName("Find Rules By Org Id And Attribute Definition Id And Rule Starts With: Exception")
  void findByOrgIdAndAttributeDefinitionIdAndRuleExceptionTest() {
    when(rulesConfigurationRepository.findByOrgIdAndAttributeDefinitionIdAndRule(
            anyString(), anyLong(), anyString()))
        .thenThrow(new RuntimeException("Database error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () ->
                rulesConfigurationPersistenceService.findByOrgIdAndAttributeDefinitionIdAndRule(
                    ORG_ID, SOURCING_ATTRIBUTES_DEFINITION_ID, SOURCING_RULE));

    assertEquals("Error while finding rules configuration", exception.getMessage());

    verify(rulesConfigurationRepository, times(1))
        .findByOrgIdAndAttributeDefinitionIdAndRule(anyString(), anyLong(), anyString());
  }

  @Test
  @DisplayName("Delete Rules Configuration Test - Happy path")
  void deleteRuleConfigurationTest() throws PromiseEngineException {
    when(rulesConfigurationEntityMapper.toEntity(any(RulesConfigurationDomainDto.class)))
        .thenReturn(testUtil.getRulesConfigurationEntity());
    doNothing().when(rulesConfigurationRepository).delete(any(RulesConfigurationEntity.class));
    assertDoesNotThrow(
        () ->
            rulesConfigurationPersistenceService.deleteRuleConfiguration(
                testUtil.getRulesConfigurationDomainDto()));

    verify(rulesConfigurationRepository, times(1)).delete(any(RulesConfigurationEntity.class));
  }

  @Test
  @DisplayName("Delete Rules Configuration Test - Exception test")
  void deleteRuleConfigurationExceptionTest() {
    when(rulesConfigurationEntityMapper.toEntity(any(RulesConfigurationDomainDto.class)))
        .thenReturn(testUtil.getRulesConfigurationEntity());
    doThrow(new RuntimeException("Error while deleting rules configuration"))
        .when(rulesConfigurationRepository)
        .delete(any(RulesConfigurationEntity.class));

    Exception exception =
        assertThrows(
            RuntimeException.class,
            () ->
                rulesConfigurationPersistenceService.delete(
                    testUtil.getRulesConfigurationDomainDto()));

    assertEquals("Error while deleting rules configuration", exception.getMessage());

    verify(rulesConfigurationRepository, times(1)).delete(any(RulesConfigurationEntity.class));
  }
}
