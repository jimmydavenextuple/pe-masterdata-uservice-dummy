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
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributeDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingAttributeEntity;
import com.nextuple.promise.sourcing.rule.persistence.mapper.SourcingAttributeEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.SourcingAttributeRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class SourcingAttributePersistenceServiceImplTest {

  @InjectMocks private SourcingAttributePersistenceServiceImpl sourcingAttributePersistenceService;
  @Mock private SourcingAttributeRepository sourcingAttributeRepository;

  @Mock private SourcingAttributeEntityMapper sourcingAttributeEntityMapper;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        sourcingAttributePersistenceService, "repository", sourcingAttributeRepository);
    ReflectionTestUtils.setField(
        sourcingAttributePersistenceService, "mapper", sourcingAttributeEntityMapper);
  }

  @Test
  void saveSourcingAttributeTest() throws PromiseEngineException {
    SourcingAttributeEntity sourcingAttributeEntity = testUtil.getSourcingAttributeEntity();
    SourcingAttributeDomainDto sourcingAttributeDomainDto =
        testUtil.getSourcingAttributeDomainDto();
    when(sourcingAttributeEntityMapper.toEntity(any(SourcingAttributeDomainDto.class)))
        .thenReturn(sourcingAttributeEntity);
    when(sourcingAttributeEntityMapper.toDomain(any(SourcingAttributeEntity.class)))
        .thenReturn(sourcingAttributeDomainDto);
    when(sourcingAttributeRepository.save(any(SourcingAttributeEntity.class)))
        .thenReturn(sourcingAttributeEntity);
    SourcingAttributeDomainDto saved_result_response =
        sourcingAttributePersistenceService.saveSourcingAttribute(sourcingAttributeDomainDto);
    assertEquals(sourcingAttributeDomainDto, saved_result_response);
    verify(sourcingAttributeRepository, times(1)).save(any());
  }

  @Test
  void saveSourcingAttributeExceptionTest() throws PromiseEngineException {
    SourcingAttributeEntity sourcingAttributeEntity = testUtil.getSourcingAttributeEntity();
    SourcingAttributeDomainDto sourcingAttributeDomainDto =
        testUtil.getSourcingAttributeDomainDto();
    when(sourcingAttributeEntityMapper.toEntity(any(SourcingAttributeDomainDto.class)))
        .thenReturn(sourcingAttributeEntity);
    when(sourcingAttributeRepository.save(any(SourcingAttributeEntity.class)))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingAttributePersistenceService.saveSourcingAttribute(sourcingAttributeDomainDto);
            });
    assertEquals("Unable to save sourcing attribute", ex.getMessage());
  }

  @Test
  void getSourcingAttributeByIdTest() throws PromiseEngineException {
    ;
    SourcingAttributeDomainDto sourcingAttributeDomainDto =
        testUtil.getSourcingAttributeDomainDto();
    when(sourcingAttributeRepository.findById(anyLong()))
        .thenReturn(Optional.ofNullable(testUtil.getSourcingAttributeEntity()));
    when(sourcingAttributeEntityMapper.toDomain(any(SourcingAttributeEntity.class)))
        .thenReturn(sourcingAttributeDomainDto);
    Optional<SourcingAttributeDomainDto> saved_result_response =
        sourcingAttributePersistenceService.getSourcingAttributeById(
            TestUtil.SOURCING_ATTRIBUTE_ID);
    assertEquals(sourcingAttributeDomainDto, saved_result_response.get());
    verify(sourcingAttributeRepository, times(1)).findById(anyLong());
  }

  @Test
  void getSourcingAttributeByIdExceptionTest() throws PromiseEngineException {
    when(sourcingAttributeRepository.findById(anyLong())).thenThrow(new RuntimeException("error"));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingAttributePersistenceService.getSourcingAttributeById(
                  TestUtil.SOURCING_ATTRIBUTE_ID);
            });
    assertEquals("Unable to find sourcing attribute", ex.getMessage());
    verify(sourcingAttributeRepository, times(1)).findById(anyLong());
  }

  @Test
  void getSourcingAttributeListByOrgIdTest() throws PromiseEngineException {
    SourcingAttributeEntity sourcingAttributeEntity = testUtil.getSourcingAttributeEntity();
    SourcingAttributeDomainDto sourcingAttributeDomainDto =
        testUtil.getSourcingAttributeDomainDto();
    when(sourcingAttributeEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(sourcingAttributeDomainDto));
    when(sourcingAttributeRepository.findByOrgId(anyString()))
        .thenReturn(List.of(sourcingAttributeEntity));
    List<SourcingAttributeDomainDto> saved_result =
        sourcingAttributePersistenceService.getSourcingAttributeListByOrgId(TestUtil.ORG_ID);
    assertEquals(sourcingAttributeDomainDto, saved_result.get(0));
    verify(sourcingAttributeRepository, times(1)).findByOrgId(anyString());
  }

  @Test
  void getSourcingAttributeByOrgIdExceptionTest() throws PromiseEngineException {
    when(sourcingAttributeRepository.findByOrgId(anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingAttributePersistenceService.getSourcingAttributeListByOrgId(TestUtil.ORG_ID);
            });
    assertEquals("Unable to find sourcing attribute list by orgId", ex.getMessage());
  }

  @Test
  void getSourcingAttributeListByOrgIdAndAttributeNameTest() throws PromiseEngineException {
    SourcingAttributeEntity sourcingAttributeEntity = testUtil.getSourcingAttributeEntity();
    SourcingAttributeDomainDto sourcingAttributeDomainDto =
        testUtil.getSourcingAttributeDomainDto();
    when(sourcingAttributeEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(sourcingAttributeDomainDto));
    when(sourcingAttributeRepository.findByOrgIdAndAttributeName(anyString(), anyString()))
        .thenReturn(List.of(sourcingAttributeEntity));

    List<SourcingAttributeDomainDto> saved_result =
        sourcingAttributePersistenceService.getSourcingAttributeListByOrgIdAndAttributeName(
            TestUtil.ORG_ID, TestUtil.ATTRIBUTE_NAME);
    assertEquals(sourcingAttributeDomainDto, saved_result.get(0));
    verify(sourcingAttributeRepository, times(1))
        .findByOrgIdAndAttributeName(anyString(), anyString());
  }

  @Test
  void getSourcingAttributeByOrgIdAndAttributeNameExceptionTest() {
    when(sourcingAttributeRepository.findByOrgIdAndAttributeName(anyString(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingAttributePersistenceService.getSourcingAttributeListByOrgIdAndAttributeName(
                  TestUtil.ORG_ID, TestUtil.ATTRIBUTE_NAME);
            });
    assertEquals(
        "Unable to find sourcing attribute list by orgId and attribute name", ex.getMessage());
  }

  @Test
  void getSourcingAttributeByIdAndOrgIdTest() throws PromiseEngineException {
    SourcingAttributeEntity sourcingAttributeEntity = testUtil.getSourcingAttributeEntity();
    SourcingAttributeDomainDto sourcingAttributeDomainDto =
        testUtil.getSourcingAttributeDomainDto();
    when(sourcingAttributeEntityMapper.toDomain(any(SourcingAttributeEntity.class)))
        .thenReturn(sourcingAttributeDomainDto);
    when(sourcingAttributeRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(sourcingAttributeEntity));
    Optional<SourcingAttributeDomainDto> saved_result =
        sourcingAttributePersistenceService.getSourcingAttributeByIdAndOrgId(
            TestUtil.SOURCING_ATTRIBUTE_ID, TestUtil.ORG_ID);
    assertEquals(sourcingAttributeDomainDto, saved_result.get());
    verify(sourcingAttributeRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getSourcingAttributeByIdAndOrgIdExceptionTest() throws PromiseEngineException {
    when(sourcingAttributeRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingAttributePersistenceService.getSourcingAttributeByIdAndOrgId(
                  TestUtil.SOURCING_ATTRIBUTE_ID, TestUtil.ORG_ID);
            });
    assertEquals("Unable to find sourcing attribute by id and orgId", ex.getMessage());
  }

  @Test
  @DisplayName("Fetch Sourcing Attribute List By OrgId And Attribute Ids: Happy Path")
  void fetchSourcingAttributeListByOrgIdAndAttributeIdsTest() throws PromiseEngineException {
    String orgId = TestUtil.ORG_ID;
    List<Long> ids = Arrays.asList(1L, 2L);
    List<SourcingAttributeEntity> sourcingAttributeEntities =
        Arrays.asList(testUtil.getSourcingAttributeEntity(), testUtil.getSourcingAttributeEntity());
    List<SourcingAttributeDomainDto> expectedDomainDtos =
        Arrays.asList(
            testUtil.getSourcingAttributeDomainDto(), testUtil.getSourcingAttributeDomainDto());
    when(sourcingAttributeRepository.findByOrgIdAndIdIn(orgId, ids))
        .thenReturn(sourcingAttributeEntities);
    when(sourcingAttributeEntityMapper.toDomain(sourcingAttributeEntities))
        .thenReturn(expectedDomainDtos);

    List<SourcingAttributeDomainDto> result =
        sourcingAttributePersistenceService.fetchSourcingAttributeListByOrgIdAndAttributeIds(
            orgId, ids);

    assertEquals(expectedDomainDtos.size(), result.size());
    assertEquals(expectedDomainDtos, result);
    verify(sourcingAttributeRepository, times(1)).findByOrgIdAndIdIn(orgId, ids);
    verify(sourcingAttributeEntityMapper, times(1)).toDomain(sourcingAttributeEntities);
  }

  @Test
  @DisplayName("Fetch Sourcing Attribute List By OrgId And Attribute Ids: Exception")
  void fetchSourcingAttributeListByOrgIdAndAttributeIdsExceptionTest() {
    String orgId = TestUtil.ORG_ID;
    List<Long> ids = Arrays.asList(1L, 2L);
    when(sourcingAttributeRepository.findByOrgIdAndIdIn(orgId, ids))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              sourcingAttributePersistenceService.fetchSourcingAttributeListByOrgIdAndAttributeIds(
                  orgId, ids);
            });
    assertEquals("Unable to find sourcing attribute list by orgId and ids", ex.getMessage());
    verify(sourcingAttributeRepository, times(1)).findByOrgIdAndIdIn(orgId, ids);
  }
}
