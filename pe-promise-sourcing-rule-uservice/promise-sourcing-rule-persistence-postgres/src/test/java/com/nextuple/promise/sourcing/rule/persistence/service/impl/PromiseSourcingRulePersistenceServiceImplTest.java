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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.persistence.domain.PromiseSourcingRuleDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.entity.PromiseSourcingRuleEntity;
import com.nextuple.promise.sourcing.rule.persistence.mapper.PromiseSourcingRuleEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.PromiseSourcingRuleRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class PromiseSourcingRulePersistenceServiceImplTest {
  @InjectMocks
  private PromiseSourcingRulePersistenceServiceImpl promiseSourcingRulePersistenceService;

  @Mock private PromiseSourcingRuleRepository promiseSourcingRuleRepository;

  @Mock private PromiseSourcingRuleEntityMapper promiseSourcingRuleEntityMapper;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(
        promiseSourcingRulePersistenceService, "repository", promiseSourcingRuleRepository);
    ReflectionTestUtils.setField(
        promiseSourcingRulePersistenceService, "mapper", promiseSourcingRuleEntityMapper);
  }

  @Test
  void fetchPromiseSourcingRuleTest() throws PromiseEngineException {
    PromiseSourcingRuleEntity promiseSourcingRule = testUtil.getPromiseSourcingRule();
    PromiseSourcingRuleDomainDto promiseSourcingRuleDomainDto =
        testUtil.getPromiseSourcingRuleDomainDto();
    FetchPromiseSourcingRuleRequest fetchPromiseSourcingRuleRequest =
        testUtil.getFetchPromiseSourcingRuleRequest();
    when(promiseSourcingRuleRepository
            .findByServiceOptionInAndOrgIdAndAllocationRuleIdAndDestinationGeoZone(
                anyList(), anyString(), anyString(), anyString()))
        .thenReturn(Collections.singletonList(promiseSourcingRule));
    when(promiseSourcingRuleEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(promiseSourcingRuleDomainDto));
    List<PromiseSourcingRuleDomainDto> received_entity =
        promiseSourcingRulePersistenceService.fetchSourcingRule(fetchPromiseSourcingRuleRequest);
    assertEquals(fetchPromiseSourcingRuleRequest.getOrgId(), received_entity.get(0).getOrgId());
  }

  @Test
  void fetchPromiseSourcingRuleExceptionTest() {
    FetchPromiseSourcingRuleRequest fetchPromiseSourcingRuleRequest =
        testUtil.getFetchPromiseSourcingRuleRequest();

    when(promiseSourcingRuleRepository
            .findByServiceOptionInAndOrgIdAndAllocationRuleIdAndDestinationGeoZone(
                anyList(), anyString(), anyString(), anyString()))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRulePersistenceService.fetchSourcingRule(fetchPromiseSourcingRuleRequest);
        });
  }

  @Test
  void savePromiseSourcingRuleTest() throws PromiseEngineException {
    PromiseSourcingRuleEntity promiseSourcingRule = testUtil.getPromiseSourcingRule();
    PromiseSourcingRuleDomainDto promiseSourcingRuleDomainDto =
        testUtil.getPromiseSourcingRuleDomainDto();
    when(promiseSourcingRuleEntityMapper.toDomain(any(PromiseSourcingRuleEntity.class)))
        .thenReturn(promiseSourcingRuleDomainDto);
    when(promiseSourcingRuleEntityMapper.toEntity(any(PromiseSourcingRuleDomainDto.class)))
        .thenReturn(promiseSourcingRule);
    when(promiseSourcingRuleRepository.save(any(PromiseSourcingRuleEntity.class)))
        .thenReturn(promiseSourcingRule);
    PromiseSourcingRuleDomainDto received_promiseSourcingRule =
        promiseSourcingRulePersistenceService.savePromiseSourcingRule(promiseSourcingRuleDomainDto);
    assertEquals(promiseSourcingRuleDomainDto, received_promiseSourcingRule);
  }

  @Test
  void savePromiseSourcingRuleExceptionTest() {
    PromiseSourcingRuleEntity promiseSourcingRule = testUtil.getPromiseSourcingRule();
    PromiseSourcingRuleDomainDto promiseSourcingRuleDomainDto =
        testUtil.getPromiseSourcingRuleDomainDto();
    when(promiseSourcingRuleEntityMapper.toEntity(any(PromiseSourcingRuleDomainDto.class)))
        .thenReturn(promiseSourcingRule);
    when(promiseSourcingRuleRepository.save(any(PromiseSourcingRuleEntity.class)))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRulePersistenceService.savePromiseSourcingRule(
              promiseSourcingRuleDomainDto);
        });
  }

  @Test
  void getPromiseSourcingRuleTest() throws PromiseEngineException {
    PromiseSourcingRuleEntity promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRuleRepository
            .findByOrgIdAndServiceOptionAndDestinationGeoZoneAndAllocationRuleIdAndPriority(
                anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(promiseSourcingRule);

    when(promiseSourcingRuleEntityMapper.toDomain(any(PromiseSourcingRuleEntity.class)))
        .thenReturn(testUtil.getPromiseSourcingRuleDomainDto());

    PromiseSourcingRuleDomainDto received_entityResponse =
        promiseSourcingRulePersistenceService.getPromiseSourcingRule(
            TestUtil.ORG_ID,
            TestUtil.SERVICE_OPTION,
            TestUtil.DESTINATION_GEO_ZONE,
            TestUtil.ALLOCATION_RULE_ID,
            TestUtil.PRIORITY);
    assertEquals(TestUtil.ORG_ID, received_entityResponse.getOrgId());
  }

  @Test
  void getPromiseSourcingRuleExceptionTest() {
    when(promiseSourcingRuleRepository
            .findByOrgIdAndServiceOptionAndDestinationGeoZoneAndAllocationRuleIdAndPriority(
                anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenThrow(NullPointerException.class);
    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRulePersistenceService.getPromiseSourcingRule(
              TestUtil.ORG_ID,
              TestUtil.SERVICE_OPTION,
              TestUtil.DESTINATION_GEO_ZONE,
              TestUtil.ALLOCATION_RULE_ID,
              TestUtil.PRIORITY);
        });
  }

  @Test
  void getPromiseSourcingRuleByOrgIdTest() throws PromiseEngineException {
    PromiseSourcingRuleEntity promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRuleRepository.findByOrgId(anyString()))
        .thenReturn(Collections.singletonList(promiseSourcingRule));
    when(promiseSourcingRuleEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(testUtil.getPromiseSourcingRuleDomainDto()));
    List<PromiseSourcingRuleDomainDto> received_entities =
        promiseSourcingRulePersistenceService.getPromiseSourcingRulesByOrgId(TestUtil.ORG_ID);
    assertEquals(TestUtil.ORG_ID, received_entities.get(0).getOrgId());
  }

  @Test
  void getPromiseSourcingRuleByOrgIdExceptionTest() {
    when(promiseSourcingRuleRepository.findByOrgId(anyString()))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRulePersistenceService.getPromiseSourcingRulesByOrgId(TestUtil.ORG_ID);
        });
  }

  @Test
  void getPromiseSourcingRulesByPriorityTest() throws PromiseEngineException {
    PromiseSourcingRuleEntity promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRuleRepository.findByPriority(anyInt()))
        .thenReturn(Collections.singletonList(promiseSourcingRule));
    when(promiseSourcingRuleEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(testUtil.getPromiseSourcingRuleDomainDto()));
    List<PromiseSourcingRuleDomainDto> received_entities =
        promiseSourcingRulePersistenceService.getPromiseSourcingRulesByPriority(TestUtil.PRIORITY);
    assertEquals(TestUtil.PRIORITY, received_entities.get(0).getPriority());
  }

  @Test
  void getPromiseSourcingRuleByPriorityExceptionTest() {
    when(promiseSourcingRuleRepository.findByPriority(anyInt()))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          promiseSourcingRulePersistenceService.getPromiseSourcingRulesByPriority(
              TestUtil.PRIORITY);
        });
  }

  @Test
  void deletePromiseSourcingRuleTest() throws PromiseEngineException {
    PromiseSourcingRuleEntity promiseSourcingRule = testUtil.getPromiseSourcingRule();
    PromiseSourcingRuleDomainDto promiseSourcingRuleDomainDto =
        testUtil.getPromiseSourcingRuleDomainDto();
    when(promiseSourcingRuleRepository
            .findByOrgIdAndServiceOptionAndDestinationGeoZoneAndAllocationRuleIdAndPriority(
                anyString(), anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(promiseSourcingRule);
    when(promiseSourcingRuleEntityMapper.toEntity(any(PromiseSourcingRuleDomainDto.class)))
        .thenReturn(promiseSourcingRule);
    PromiseSourcingRuleDomainDto received_entityResponse =
        promiseSourcingRulePersistenceService.deletePromiseSourcingRule(
            promiseSourcingRuleDomainDto);
    assertEquals(TestUtil.ORG_ID, received_entityResponse.getOrgId());
    verify(promiseSourcingRuleRepository, times(1)).delete(promiseSourcingRule);
  }

  @Test
  void fetchSourcingRuleTest() throws PromiseEngineException {

    PromiseSourcingRuleEntity promiseSourcingRule = testUtil.getPromiseSourcingRule();
    when(promiseSourcingRuleRepository
            .findByServiceOptionAndOrgIdAndAllocationRuleIdAndDestinationGeoZone(
                anyString(), anyString(), anyString(), anyString()))
        .thenReturn(List.of(promiseSourcingRule));
    when(promiseSourcingRuleEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(testUtil.getPromiseSourcingRuleDomainDto()));
    List<PromiseSourcingRuleDomainDto> received_entityResponse =
        promiseSourcingRulePersistenceService.fetchSourcingRule(
            TestUtil.SERVICE_OPTION,
            TestUtil.ORG_ID,
            TestUtil.ALLOCATION_RULE_ID,
            TestUtil.DESTINATION_GEO_ZONE);
    assertEquals(TestUtil.ORG_ID, received_entityResponse.get(0).getOrgId());
    verify(promiseSourcingRuleRepository, times(1))
        .findByServiceOptionAndOrgIdAndAllocationRuleIdAndDestinationGeoZone(
            anyString(), anyString(), anyString(), anyString());
  }
}
