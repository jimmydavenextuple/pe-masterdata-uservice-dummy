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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingRuleDetailsDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingRuleDetailsEntity;
import com.nextuple.promise.sourcing.rule.persistence.mapper.SourcingRuleDetailsEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.SourcingRuleDetailsRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class SourcingRuleDetailsPersistenceServiceImplTest {

  @InjectMocks
  private SourcingRuleDetailsPersistenceServiceImpl sourcingRuleDetailsPersistenceService;

  @Mock private SourcingRuleDetailsRepository sourcingRuleDetailsRepository;

  @Mock private SourcingRuleDetailsEntityMapper sourcingRuleDetailsEntityMapper;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(
        sourcingRuleDetailsPersistenceService, "repository", sourcingRuleDetailsRepository);
    ReflectionTestUtils.setField(
        sourcingRuleDetailsPersistenceService, "mapper", sourcingRuleDetailsEntityMapper);
  }

  @Test
  void saveSourcingNodesTest() throws PromiseEngineException {
    SourcingRuleDetailsEntity sourcingRuleDetailsEntity = testUtil.getSourcingRuleDetailsEntity();
    SourcingRuleDetailsDomainDto sourcingRuleDetailsDomainDto =
        testUtil.getSourcingRuleDetailsDomainDto();
    when(sourcingRuleDetailsRepository.save(any(SourcingRuleDetailsEntity.class)))
        .thenReturn(sourcingRuleDetailsEntity);
    when(sourcingRuleDetailsEntityMapper.toDomain(any(SourcingRuleDetailsEntity.class)))
        .thenReturn(sourcingRuleDetailsDomainDto);
    when(sourcingRuleDetailsEntityMapper.toEntity(any(SourcingRuleDetailsDomainDto.class)))
        .thenReturn(sourcingRuleDetailsEntity);
    SourcingRuleDetailsDomainDto saved_result =
        sourcingRuleDetailsPersistenceService.saveSourcingNodes(sourcingRuleDetailsDomainDto);
    assertEquals(sourcingRuleDetailsDomainDto, saved_result);
    verify(sourcingRuleDetailsRepository, times(1)).save(any());
  }

  @Test
  void saveSourcingNodesExceptionTest() {
    when(sourcingRuleDetailsRepository.save(any(SourcingRuleDetailsEntity.class)))
        .thenThrow(new RuntimeException("error"));
    SourcingRuleDetailsEntity sourcingRuleDetailsEntity = testUtil.getSourcingRuleDetailsEntity();
    SourcingRuleDetailsDomainDto sourcingRuleDetailsDomainDto =
        testUtil.getSourcingRuleDetailsDomainDto();
    when(sourcingRuleDetailsEntityMapper.toEntity(any(SourcingRuleDetailsDomainDto.class)))
        .thenReturn(sourcingRuleDetailsEntity);
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingRuleDetailsPersistenceService.saveSourcingNodes(sourcingRuleDetailsDomainDto);
            });
    assertEquals("Unable to save sourcing nodes info", ex.getMessage());
  }

  @Test
  void getSourcingRuleByIdandOrgIdTest() throws PromiseEngineException {
    SourcingRuleDetailsEntity sourcingRuleDetailsEntity = testUtil.getSourcingRuleDetailsEntity();
    SourcingRuleDetailsDomainDto sourcingRuleDetailsDomainDto =
        testUtil.getSourcingRuleDetailsDomainDto();
    when(sourcingRuleDetailsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(sourcingRuleDetailsEntity));
    when(sourcingRuleDetailsEntityMapper.toDomain(any(SourcingRuleDetailsEntity.class)))
        .thenReturn(sourcingRuleDetailsDomainDto);
    Optional<SourcingRuleDetailsDomainDto> saved_result =
        sourcingRuleDetailsPersistenceService.getSourcingRuleDetailsByIdAndOrgId(
            TestUtil.SOURCING_RULE_ID, TestUtil.ORG_ID);
    assertEquals(sourcingRuleDetailsDomainDto, saved_result.get());
    verify(sourcingRuleDetailsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getSourcingRuleByIdandOrgIdExceptionTest() {
    when(sourcingRuleDetailsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingRuleDetailsPersistenceService.getSourcingRuleDetailsByIdAndOrgId(
                  TestUtil.SOURCING_RULE_ID, TestUtil.ORG_ID);
            });
    assertEquals("Unable to find sourcing rule details by id and orgId", ex.getMessage());
  }

  @Test
  void fetchBySourcingRuleIdTest() throws PromiseEngineException {
    SourcingRuleDetailsEntity sourcingRuleDetailsEntity = testUtil.getSourcingRuleDetailsEntity();
    SourcingRuleDetailsDomainDto sourcingRuleDetailsDomainDto =
        testUtil.getSourcingRuleDetailsDomainDto();
    when(sourcingRuleDetailsRepository.findByOrgIdAndSourcingRuleId(anyString(), anyLong()))
        .thenReturn(List.of(sourcingRuleDetailsEntity));
    when(sourcingRuleDetailsEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(sourcingRuleDetailsDomainDto));
    List<SourcingRuleDetailsDomainDto> result =
        sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(
            TestUtil.ORG_ID, TestUtil.SOURCING_RULE_ID);

    assertEquals(sourcingRuleDetailsDomainDto, result.get(0));
    verify(sourcingRuleDetailsRepository, times(1))
        .findByOrgIdAndSourcingRuleId(anyString(), anyLong());
  }

  @Test
  void fetchBySourcingRuleIdExceptionTest() {
    when(sourcingRuleDetailsRepository.findByOrgIdAndSourcingRuleId(anyString(), anyLong()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () ->
                sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(
                    TestUtil.ORG_ID, TestUtil.SOURCING_RULE_ID));

    assertEquals("Unable to find sourcing rule details by sourcingRuleId", ex.getMessage());
  }

  @Test
  void deleteMultipleSourcingRuleDetailsTest() throws PromiseEngineException {
    doNothing().when(sourcingRuleDetailsRepository).deleteAll(any());
    sourcingRuleDetailsPersistenceService.deleteMultipleSourcingRuleDetails(
        List.of(testUtil.getSourcingRuleDetailsDomainDto()));
    when(sourcingRuleDetailsEntityMapper.toEntity(anyList()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    verify(sourcingRuleDetailsRepository, times(1)).deleteAll(any());
  }

  @Test
  void deleteMultipleSourcingRuleDetailsExceptionTest() {
    doThrow(new RuntimeException("error")).when(sourcingRuleDetailsRepository).deleteAll(any());
    when(sourcingRuleDetailsEntityMapper.toEntity(anyList()))
        .thenReturn(List.of(testUtil.getSourcingRuleDetailsEntity()));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingRuleDetailsPersistenceService.deleteMultipleSourcingRuleDetails(
                  List.of(testUtil.getSourcingRuleDetailsDomainDto()));
            });
    assertEquals("Unable to delete multiple sourcing rule details", ex.getMessage());
  }

  @Test
  void getAllSourcingRuleByOrgIdTest() throws PromiseEngineException {
    SourcingRuleDetailsEntity sourcingRuleDetailsEntity = testUtil.getSourcingRuleDetailsEntity();
    SourcingRuleDetailsDomainDto sourcingRuleDetailsDomainDto =
        testUtil.getSourcingRuleDetailsDomainDto();
    when(sourcingRuleDetailsEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(sourcingRuleDetailsDomainDto));
    when(sourcingRuleDetailsRepository.findByOrgId(anyString()))
        .thenReturn(List.of(sourcingRuleDetailsEntity));
    List<SourcingRuleDetailsDomainDto> saved_result =
        sourcingRuleDetailsPersistenceService.getAllSourcingRuleByOrgId(TestUtil.ORG_ID);
    assertEquals(sourcingRuleDetailsDomainDto, saved_result.get(0));
    verify(sourcingRuleDetailsRepository, times(1)).findByOrgId(anyString());
  }

  @Test
  void getAllSourcingRuleByOrgIdExceptionTest() {
    when(sourcingRuleDetailsRepository.findByOrgId(anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingRuleDetailsPersistenceService.getAllSourcingRuleByOrgId(TestUtil.ORG_ID);
            });
    assertEquals("Unable to find sourcing rules by orgId", ex.getMessage());
  }
}
