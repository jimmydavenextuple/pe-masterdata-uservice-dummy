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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.persistence.domain.NamedOptimizationStrategyDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.entity.NamedOptimizationStrategyEntity;
import com.nextuple.promise.sourcing.rule.persistence.mapper.NamedOptimizationStrategyEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.NamedOptimizationStrategyRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

class NamedOptimizationStrategyPersistenceServiceImplTest {
  @InjectMocks
  private NamedOptimizationStrategyPersistenceServiceImpl
      namedOptimizationStrategyPersistenceService;

  @Mock private NamedOptimizationStrategyRepository namedOptimizationStrategyRepository;

  @Mock private NamedOptimizationStrategyEntityMapper namedOptimizationStrategyEntityMapper;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        namedOptimizationStrategyPersistenceService,
        "repository",
        namedOptimizationStrategyRepository);
    ReflectionTestUtils.setField(
        namedOptimizationStrategyPersistenceService,
        "mapper",
        namedOptimizationStrategyEntityMapper);
  }

  @Test
  void saveOptimizationStrategyEntityTest() throws PromiseEngineException {
    NamedOptimizationStrategyEntity namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyDomainDto =
        testUtil.getNamedOptimizationStrategyDomainDto();
    when(namedOptimizationStrategyRepository.save(any(NamedOptimizationStrategyEntity.class)))
        .thenReturn(namedOptimizationStrategyEntity);
    when(namedOptimizationStrategyEntityMapper.toDomain(any(NamedOptimizationStrategyEntity.class)))
        .thenReturn(namedOptimizationStrategyDomainDto);
    when(namedOptimizationStrategyEntityMapper.toEntity(
            any(NamedOptimizationStrategyDomainDto.class)))
        .thenReturn(namedOptimizationStrategyEntity);

    NamedOptimizationStrategyDomainDto saved_entity =
        namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
            namedOptimizationStrategyDomainDto);
    assertEquals(namedOptimizationStrategyDomainDto, saved_entity);
    verify(namedOptimizationStrategyRepository, times(1)).save(any());
  }

  @Test
  void saveOptimizationStrategyEntityExceptionTest() {
    when(namedOptimizationStrategyEntityMapper.toEntity(
            any(NamedOptimizationStrategyDomainDto.class)))
        .thenReturn(testUtil.getNamedOptimizationStrategyEntity());
    when(namedOptimizationStrategyRepository.save(any(NamedOptimizationStrategyEntity.class)))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
                  testUtil.getNamedOptimizationStrategyDomainDto());
            });

    assertEquals("Unable to save optimization strategy", exception.getMessage());
  }

  @Test
  void fetchOptimizationStrategyByIdTest() throws PromiseEngineException {
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyDomainDto =
        testUtil.getNamedOptimizationStrategyDomainDto();
    when(namedOptimizationStrategyRepository.findById(anyLong()))
        .thenReturn(Optional.of(testUtil.getNamedOptimizationStrategyEntity()));
    when(namedOptimizationStrategyEntityMapper.toDomain(any(NamedOptimizationStrategyEntity.class)))
        .thenReturn(namedOptimizationStrategyDomainDto);
    Optional<NamedOptimizationStrategyDomainDto> received_entity =
        namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyById(
            TestUtil.OPTIMIZATION_STRATEGY_ID);
    assertEquals(namedOptimizationStrategyDomainDto, received_entity.get());
    verify(namedOptimizationStrategyRepository, times(1)).findById(anyLong());
  }

  @Test
  void fetchOptimizationStrategyByIdExceptionTest() {
    when(namedOptimizationStrategyRepository.findById(anyLong()))
        .thenThrow(new RuntimeException("error"));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyById(
                  TestUtil.OPTIMIZATION_STRATEGY_ID);
            });
    assertEquals("Unable to find optimization strategy by id", exception.getMessage());

    verify(namedOptimizationStrategyRepository, times(1)).findById(anyLong());
  }

  @Test
  void fetchOptimizationStrategyByOrgIdAndGroupIdTest() throws PromiseEngineException {
    List<NamedOptimizationStrategyEntity> namedOptimizationStrategyEntityList =
        testUtil.getNamedOptimizationStrategyEntityList();
    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyDomainDtoList =
        testUtil.getNamedOptimizationStrategyDomainDtoList();
    when(namedOptimizationStrategyRepository.findByOrgIdAndGroupId(anyString(), anyString()))
        .thenReturn(namedOptimizationStrategyEntityList);
    when(namedOptimizationStrategyEntityMapper.toDomain(anyList()))
        .thenReturn(namedOptimizationStrategyDomainDtoList);
    List<NamedOptimizationStrategyDomainDto> groupDefinitionEntityListResponse =
        namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            TestUtil.ORG_ID, TestUtil.GROUP_ID);
    assertEquals(
        namedOptimizationStrategyDomainDtoList.get(0), groupDefinitionEntityListResponse.get(0));
    verify(namedOptimizationStrategyRepository, times(1))
        .findByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void fetchOptimizationStrategyByOrgIdAndGroupIdExceptionTest() {
    when(namedOptimizationStrategyRepository.findByOrgIdAndGroupId(anyString(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              namedOptimizationStrategyPersistenceService
                  .fetchOptimizationStrategyByOrgIdAndGroupId(TestUtil.ORG_ID, TestUtil.GROUP_ID);
            });
    assertEquals(
        "Unable to fetch optimization strategy by orgId and groupId", exception.getMessage());

    verify(namedOptimizationStrategyRepository, times(1))
        .findByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void fetchOptimizationStrategyByOrgIdAndStrategyNameTest() throws PromiseEngineException {
    List<NamedOptimizationStrategyEntity> namedOptimizationStrategyEntityList =
        testUtil.getNamedOptimizationStrategyEntityList();
    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyDomainDtoList =
        testUtil.getNamedOptimizationStrategyDomainDtoList();
    when(namedOptimizationStrategyRepository.findByOrgIdAndOptimizationStrategyName(
            anyString(), anyString()))
        .thenReturn(namedOptimizationStrategyEntityList);
    when(namedOptimizationStrategyEntityMapper.toDomain(anyList()))
        .thenReturn(namedOptimizationStrategyDomainDtoList);
    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyEntityListResponse =
        namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndStrategyName(
            TestUtil.ORG_ID, TestUtil.OPTIMIZATION_STRATEGY_NAME);
    assertEquals(
        namedOptimizationStrategyDomainDtoList.get(0),
        namedOptimizationStrategyEntityListResponse.get(0));
    verify(namedOptimizationStrategyRepository, times(1))
        .findByOrgIdAndOptimizationStrategyName(anyString(), anyString());
  }

  @Test
  void fetchOptimizationStrategyByOrgIdAndStrategyNameExceptionTest()
      throws PromiseEngineException {
    when(namedOptimizationStrategyRepository.findByOrgIdAndOptimizationStrategyName(
            anyString(), anyString()))
        .thenThrow(new RuntimeException("error"));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              namedOptimizationStrategyPersistenceService
                  .fetchOptimizationStrategyByOrgIdAndStrategyName(
                      TestUtil.ORG_ID, TestUtil.OPTIMIZATION_STRATEGY_NAME);
            });
    assertEquals(
        "Unable to fetch optimization strategy by orgId and optimizationStrategyName",
        exception.getMessage());
    verify(namedOptimizationStrategyRepository, times(1))
        .findByOrgIdAndOptimizationStrategyName(anyString(), anyString());
  }

  @Test
  void deleteOptimizationStrategyTest() throws PromiseEngineException {
    NamedOptimizationStrategyEntity namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    doNothing()
        .when(namedOptimizationStrategyRepository)
        .delete(any(NamedOptimizationStrategyEntity.class));
    when(namedOptimizationStrategyEntityMapper.toEntity(
            any(NamedOptimizationStrategyDomainDto.class)))
        .thenReturn(namedOptimizationStrategyEntity);
    namedOptimizationStrategyPersistenceService.deleteOptimizationStrategy(
        testUtil.getNamedOptimizationStrategyDomainDto());
    verify(namedOptimizationStrategyRepository, times(1)).delete(any());
  }

  @Test
  void deleteOptimizationStrategyExceptionTest() {
    when(namedOptimizationStrategyEntityMapper.toEntity(
            any(NamedOptimizationStrategyDomainDto.class)))
        .thenReturn(testUtil.getUpdatedNamedOptimizationStrategyEntity());
    doThrow(new RuntimeException("error"))
        .when(namedOptimizationStrategyRepository)
        .delete(any(NamedOptimizationStrategyEntity.class));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              namedOptimizationStrategyPersistenceService.deleteOptimizationStrategy(
                  testUtil.getNamedOptimizationStrategyDomainDto());
            });
    assertEquals("Unable to delete optimization strategy", ex.getMessage());
  }

  @Test
  void fetchOptimizationStrategyByOrgIdAndGroupIdHappyPathTest() throws PromiseEngineException {
    NamedOptimizationStrategyEntity namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    List<NamedOptimizationStrategyEntity> namedOptimizationStrategyEntityList =
        List.of(namedOptimizationStrategyEntity);

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).descending());

    Page<NamedOptimizationStrategyEntity> namedOptimizationStrategyEntityPage =
        new PageImpl<>(
            namedOptimizationStrategyEntityList,
            pageable,
            namedOptimizationStrategyEntityList.size());
    Page<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyDomainDtoPage =
        new PageImpl<>(
            testUtil.getNamedOptimizationStrategyDomainDtoList(),
            pageable,
            testUtil.getNamedOptimizationStrategyDomainDtoList().size());
    when(namedOptimizationStrategyRepository.findByOrgIdAndGroupId(
            anyString(), anyString(), any(Pageable.class)))
        .thenReturn(namedOptimizationStrategyEntityPage);
    when(namedOptimizationStrategyEntityMapper.toDomain(any(NamedOptimizationStrategyEntity.class)))
        .thenReturn(testUtil.getNamedOptimizationStrategyDomainDto());
    Page<NamedOptimizationStrategyDomainDto> response =
        namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            TestUtil.ORG_ID, TestUtil.GROUP_ID, pageable);
    assertEquals(namedOptimizationStrategyDomainDtoPage, response);
    verify(namedOptimizationStrategyRepository, times(1))
        .findByOrgIdAndGroupId(anyString(), anyString(), any(Pageable.class));
  }

  @Test
  void fetchOptimizationStrategyByOrgIdAndGroupIdPageableExceptionTest()
      throws PromiseEngineException {
    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).descending());

    when(namedOptimizationStrategyRepository.findByOrgIdAndGroupId(anyString(), anyString(), any()))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              namedOptimizationStrategyPersistenceService
                  .fetchOptimizationStrategyByOrgIdAndGroupId(
                      TestUtil.ORG_ID, TestUtil.GROUP_ID, pageable);
            });

    assertEquals(
        "Unable to fetch optimization strategies by orgId and groupId", exception.getMessage());
    verify(namedOptimizationStrategyRepository, times(1))
        .findByOrgIdAndGroupId(anyString(), anyString(), any());
  }

  @Test
  void fetchOptimizationStrategyByIdAndOrgIdTest() throws PromiseEngineException {
    NamedOptimizationStrategyEntity namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyDomainDto =
        testUtil.getNamedOptimizationStrategyDomainDto();
    when(namedOptimizationStrategyEntityMapper.toDomain(any(NamedOptimizationStrategyEntity.class)))
        .thenReturn(namedOptimizationStrategyDomainDto);
    when(namedOptimizationStrategyRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(namedOptimizationStrategyEntity));

    Optional<NamedOptimizationStrategyDomainDto> received_entity =
        namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByIdAndOrgId(
            TestUtil.OPTIMIZATION_STRATEGY_ID, TestUtil.ORG_ID);
    assertEquals(namedOptimizationStrategyDomainDto, received_entity.get());
    verify(namedOptimizationStrategyRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void fetchOptimizationStrategyByIdAndOrgIdExceptionTest() {
    when(namedOptimizationStrategyRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByIdAndOrgId(
                  TestUtil.OPTIMIZATION_STRATEGY_ID, TestUtil.ORG_ID);
            });
    assertEquals("Unable to find optimization strategy by id and orgId", exception.getMessage());

    verify(namedOptimizationStrategyRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void deleteByIdInTest() throws PromiseEngineException {
    doNothing().when(namedOptimizationStrategyRepository).deleteByIdIn(anyList());
    namedOptimizationStrategyPersistenceService.deleteByIdIn(List.of(1L));
    verify(namedOptimizationStrategyRepository, times(1)).deleteByIdIn(anyList());
  }

  @Test
  void deleteByIdInExceptionTest() {
    doThrow(new RuntimeException("error"))
        .when(namedOptimizationStrategyRepository)
        .deleteByIdIn(anyList());

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              namedOptimizationStrategyPersistenceService.deleteByIdIn(List.of(1L));
            });
    assertEquals("Unable to delete optimization rules", ex.getMessage());
  }

  @Test
  @DisplayName(
      "Fetch Optimization Strategy By Org ID and Optimization Strategy Details: Happy Path")
  void fetchOptimizationStrategyByOrgIdAndOptimizationStrategyDetailsTest()
      throws PromiseEngineException {
    NamedOptimizationStrategyEntity namedOptimizationStrategyEntity =
        testUtil.getNamedOptimizationStrategyEntity();
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyDomainDto =
        testUtil.getNamedOptimizationStrategyDomainDto();
    when(namedOptimizationStrategyEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(namedOptimizationStrategyDomainDto));
    when(namedOptimizationStrategyRepository.findByOrgIdAndOptimizationStrategyDetails(
            anyString(), anyString()))
        .thenReturn(List.of(namedOptimizationStrategyEntity));

    List<NamedOptimizationStrategyDomainDto> received_entity =
        namedOptimizationStrategyPersistenceService
            .fetchOptimizationStrategyByOrgIdAndOptimizationStrategyDetails(
                TestUtil.ORG_ID, TestUtil.OPTIMIZATION_STRATEGY_DETAILS);
    System.out.println(received_entity);
    assertEquals(namedOptimizationStrategyDomainDto, received_entity.getFirst());
    verify(namedOptimizationStrategyRepository, times(1))
        .findByOrgIdAndOptimizationStrategyDetails(anyString(), anyString());
  }

  @Test
  @DisplayName(
      "Fetch Optimization Strategy By Org ID and Optimization Strategy Details: Exception Path")
  void fetchOptimizationStrategyByOrgIdAndOptimizationStrategyDetailsExceptionTest() {
    when(namedOptimizationStrategyRepository.findByOrgIdAndOptimizationStrategyDetails(
            anyString(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              namedOptimizationStrategyPersistenceService
                  .fetchOptimizationStrategyByOrgIdAndOptimizationStrategyDetails(
                      TestUtil.ORG_ID, TestUtil.OPTIMIZATION_STRATEGY_DETAILS);
            });
    assertEquals(
        "Unable to fetch optimization strategy by orgId and optimizationStrategyDetails",
        exception.getMessage());

    verify(namedOptimizationStrategyRepository, times(1))
        .findByOrgIdAndOptimizationStrategyDetails(anyString(), anyString());
  }
}
