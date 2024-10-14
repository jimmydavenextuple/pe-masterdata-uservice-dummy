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
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingConstraintDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingConstraintEntity;
import com.nextuple.promise.sourcing.rule.persistence.mapper.SourcingConstraintEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.SourcingConstraintRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class SourcingConstraintPersistenceServiceImplTest {

  @InjectMocks
  private SourcingConstraintPersistenceServiceImpl sourcingConstraintPersistenceService;

  @Mock private SourcingConstraintRepository sourcingConstraintRepository;

  @Mock private SourcingConstraintEntityMapper sourcingConstraintEntityMapper;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(
        sourcingConstraintPersistenceService, "repository", sourcingConstraintRepository);
    ReflectionTestUtils.setField(
        sourcingConstraintPersistenceService, "mapper", sourcingConstraintEntityMapper);
  }

  @Test
  void saveSourcingConstraintTest() throws PromiseEngineException {
    SourcingConstraintEntity sourcingConstraintEntity = testUtil.getSourcingConstraintEntity();
    SourcingConstraintDomainDto sourcingConstraintDomainDto =
        testUtil.getSourcingConstraintDomainDto();
    when(sourcingConstraintRepository.save(any(SourcingConstraintEntity.class)))
        .thenReturn(sourcingConstraintEntity);
    when(sourcingConstraintEntityMapper.toDomain(any(SourcingConstraintEntity.class)))
        .thenReturn(sourcingConstraintDomainDto);
    when(sourcingConstraintEntityMapper.toEntity(any(SourcingConstraintDomainDto.class)))
        .thenReturn(sourcingConstraintEntity);
    SourcingConstraintDomainDto saved_result =
        sourcingConstraintPersistenceService.saveSourcingConstraintEntity(
            sourcingConstraintDomainDto);
    assertEquals(sourcingConstraintDomainDto, saved_result);
    verify(sourcingConstraintRepository, times(1)).save(any());
  }

  @Test
  void saveSourcingConstraintExceptionTest() throws PromiseEngineException {

    SourcingConstraintEntity sourcingConstraintEntity = testUtil.getSourcingConstraintEntity();
    SourcingConstraintDomainDto sourcingConstraintDomainDto =
        testUtil.getSourcingConstraintDomainDto();
    when(sourcingConstraintEntityMapper.toEntity(any(SourcingConstraintDomainDto.class)))
        .thenReturn(sourcingConstraintEntity);
    when(sourcingConstraintRepository.save(any(SourcingConstraintEntity.class)))
        .thenThrow(new RuntimeException("error"));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingConstraintPersistenceService.saveSourcingConstraintEntity(
                  sourcingConstraintDomainDto);
            });
    assertEquals("Unable to save sourcing constraint entity", ex.getMessage());
  }

  @Test
  void saveSourcingConstraintEntitiesTest() throws PromiseEngineException {
    List<SourcingConstraintEntity> sourcingConstraintEntitiesList =
        List.of(testUtil.getSourcingConstraintEntity());
    List<SourcingConstraintDomainDto> sourcingConstraintDomainDtoList =
        List.of(testUtil.getSourcingConstraintDomainDto());
    when(sourcingConstraintRepository.saveAll(any())).thenReturn(sourcingConstraintEntitiesList);
    when(sourcingConstraintEntityMapper.toDomain(anyList()))
        .thenReturn(sourcingConstraintDomainDtoList);
    when(sourcingConstraintEntityMapper.toEntity(anyList()))
        .thenReturn(sourcingConstraintEntitiesList);
    List<SourcingConstraintDomainDto> saved_result =
        sourcingConstraintPersistenceService.saveSourcingConstraintEntities(
            sourcingConstraintDomainDtoList);
    assertEquals(sourcingConstraintDomainDtoList, saved_result);
    verify(sourcingConstraintRepository, times(1)).saveAll(any());
  }

  @Test
  void saveSourcingConstraintEntitiesExceptionTest() {
    when(sourcingConstraintRepository.saveAll(any())).thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingConstraintPersistenceService.saveSourcingConstraintEntities(
                  List.of(testUtil.getSourcingConstraintDomainDto()));
            });
    assertEquals("Unable to save sourcing constraint entities", ex.getMessage());
  }

  @Test
  void getSourcingConstraintEntityByIdAndOrgIdTest() throws PromiseEngineException {
    SourcingConstraintEntity sourcingConstraintEntity = testUtil.getSourcingConstraintEntity();
    SourcingConstraintDomainDto sourcingConstraintDomainDto =
        testUtil.getSourcingConstraintDomainDto();
    when(sourcingConstraintRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(sourcingConstraintEntity));
    when(sourcingConstraintEntityMapper.toDomain(any(SourcingConstraintEntity.class)))
        .thenReturn(sourcingConstraintDomainDto);
    Optional<SourcingConstraintDomainDto> saved_result =
        sourcingConstraintPersistenceService.getSourcingConstraintEntityByIdAndOrgId(
            TestUtil.ID, TestUtil.ORG_ID);
    assertEquals(sourcingConstraintDomainDto, saved_result.get());
    verify(sourcingConstraintRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getSourcingConstraintEntityByIdAndOrgIdExceptionTest() {
    when(sourcingConstraintRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingConstraintPersistenceService.getSourcingConstraintEntityByIdAndOrgId(
                  TestUtil.ID, TestUtil.ORG_ID);
            });
    assertEquals("Unable to find sourcing constraint entity by id and orgId", ex.getMessage());
  }

  @Test
  void fetchByOrgIdAndGroupIdTest() throws PromiseEngineException {
    SourcingConstraintEntity sourcingConstraintEntity = testUtil.getSourcingConstraintEntity();
    SourcingConstraintDomainDto sourcingConstraintDomainDto =
        testUtil.getSourcingConstraintDomainDto();
    when(sourcingConstraintRepository.findByOrgIdAndGroupId(anyString(), anyString()))
        .thenReturn(List.of(sourcingConstraintEntity));
    when(sourcingConstraintEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(sourcingConstraintDomainDto));
    List<SourcingConstraintDomainDto> saved_result =
        sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(
            TestUtil.ORG_ID, TestUtil.DEFAULT_GROUP_ID);
    assertEquals(sourcingConstraintDomainDto, saved_result.get(0));
    verify(sourcingConstraintRepository, times(1)).findByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void fetchByOrgIdAndGroupIdExceptionTest() throws PromiseEngineException {
    when(sourcingConstraintRepository.findByOrgIdAndGroupId(anyString(), anyString()))
        .thenThrow(new RuntimeException("error"));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(
                  TestUtil.ORG_ID, TestUtil.DEFAULT_GROUP_ID);
            });
    assertEquals(
        "Unable to fetch sourcing constraint entity list by orgId and groupId", ex.getMessage());
    verify(sourcingConstraintRepository, times(1)).findByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void fetchByOrgIdAndGroupIdAndConstraintTest() throws PromiseEngineException {
    SourcingConstraintEntity sourcingConstraintEntity = testUtil.getSourcingConstraintEntity();
    SourcingConstraintDomainDto sourcingConstraintDomainDto =
        testUtil.getSourcingConstraintDomainDto();
    when(sourcingConstraintRepository.findByOrgIdAndGroupIdAndSourcingConstraint(
            anyString(), anyString(), any()))
        .thenReturn(List.of(sourcingConstraintEntity));
    when(sourcingConstraintEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(sourcingConstraintDomainDto));
    List<SourcingConstraintDomainDto> saved_result =
        sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            TestUtil.ORG_ID, TestUtil.DEFAULT_GROUP_ID, TestUtil.SOURCING_CONSTRAINT);
    assertEquals(sourcingConstraintDomainDto, saved_result.get(0));
    verify(sourcingConstraintRepository, times(1))
        .findByOrgIdAndGroupIdAndSourcingConstraint(anyString(), anyString(), any());
  }

  @Test
  void fetchByOrgIdAndGroupIdAndServiceOptionAndConstraintExceptionTest() {
    when(sourcingConstraintRepository.findByOrgIdAndGroupIdAndSourcingConstraint(
            anyString(), anyString(), any()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
                  TestUtil.ORG_ID, TestUtil.DEFAULT_GROUP_ID, TestUtil.SOURCING_CONSTRAINT);
            });

    assertEquals(
        "Unable to fetch sourcing constraint entity list by orgId , groupId and constraint",
        ex.getMessage());
  }

  @Test
  void deleteSourcingConstraintTest() throws PromiseEngineException {
    SourcingConstraintEntity sourcingConstraintEntity = testUtil.getSourcingConstraintEntity();
    SourcingConstraintDomainDto sourcingConstraintDomainDto =
        testUtil.getSourcingConstraintDomainDto();
    doNothing().when(sourcingConstraintRepository).delete(any(SourcingConstraintEntity.class));
    when(sourcingConstraintEntityMapper.toEntity(any(SourcingConstraintDomainDto.class)))
        .thenReturn(sourcingConstraintEntity);
    sourcingConstraintPersistenceService.deleteSourcingConstraint(sourcingConstraintDomainDto);
    verify(sourcingConstraintRepository, times(1)).delete(any());
  }

  @Test
  void deleteSourcingConstraintExceptionTest() {

    doThrow(new RuntimeException("error"))
        .when(sourcingConstraintRepository)
        .delete(any(SourcingConstraintEntity.class));
    SourcingConstraintEntity sourcingConstraintEntity = testUtil.getSourcingConstraintEntity();
    SourcingConstraintDomainDto sourcingConstraintDomainDto =
        testUtil.getSourcingConstraintDomainDto();
    when(sourcingConstraintEntityMapper.toEntity(any(SourcingConstraintDomainDto.class)))
        .thenReturn(sourcingConstraintEntity);
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingConstraintPersistenceService.deleteSourcingConstraint(
                  sourcingConstraintDomainDto);
            });
    assertEquals("Unable to delete sourcing constraint", ex.getMessage());
  }
}
