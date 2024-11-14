/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.configuration.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.configuration.persistence.domain.TenantConfigdataDomainDto;
import com.nextuple.configuration.persistence.entity.TenantConfigdataEntity;
import com.nextuple.configuration.persistence.mapper.TenantConfigdataEntityMapper;
import com.nextuple.configuration.persistence.repository.TenantConfigdataRepository;
import com.nextuple.configuration.persistence.util.TestUtil;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class TenantConfigdataPersistenceServiceImplTest {
  @InjectMocks private TenantConfigdataPersistenceServiceImpl tenantConfigdataPersistenceService;

  @Mock private TenantConfigdataRepository tenantConfigdataRepository;

  @Mock private TenantConfigdataEntityMapper tenantConfigdataEntityMapper;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        tenantConfigdataPersistenceService, "repository", tenantConfigdataRepository);
    ReflectionTestUtils.setField(
        tenantConfigdataPersistenceService, "mapper", tenantConfigdataEntityMapper);
  }

  @Test
  void saveTenantConfigdataTest() throws PromiseEngineException {
    TenantConfigdataEntity tenantConfigdataEntity = testUtil.getTenantConfigdataEntity();
    when(tenantConfigdataEntityMapper.toEntity(any(TenantConfigdataDomainDto.class)))
        .thenReturn(tenantConfigdataEntity);
    when(tenantConfigdataEntityMapper.toDomain(any(TenantConfigdataEntity.class)))
        .thenReturn(testUtil.getTenantConfigdataPersistenceDto());
    when(tenantConfigdataRepository.save(any(TenantConfigdataEntity.class)))
        .thenReturn(tenantConfigdataEntity);
    TenantConfigdataDomainDto savedTenantConfigdataDomainDto =
        tenantConfigdataPersistenceService.saveTenantConfigdata(
            testUtil.getTenantConfigdataPersistenceDto());
    Assertions.assertEquals(tenantConfigdataEntity.getId(), savedTenantConfigdataDomainDto.getId());
    verify(tenantConfigdataRepository, times(1)).save(any());
  }

  @Test
  void saveTenantConfigdataExceptionTest() throws PromiseEngineException {
    TenantConfigdataEntity tenantConfigdataEntity = testUtil.getTenantConfigdataEntity();
    when(tenantConfigdataEntityMapper.toEntity(any(TenantConfigdataDomainDto.class)))
        .thenReturn(tenantConfigdataEntity);
    when(tenantConfigdataEntityMapper.toDomain(any(TenantConfigdataEntity.class)))
        .thenReturn(testUtil.getTenantConfigdataPersistenceDto());
    when(tenantConfigdataRepository.save(any(TenantConfigdataEntity.class)))
        .thenThrow(new RuntimeException("error"));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              tenantConfigdataPersistenceService.saveTenantConfigdata(
                  testUtil.getTenantConfigdataPersistenceDto());
            });
    assertEquals("Unable to save tenant configuration data", ex.getMessage());
    verify(tenantConfigdataRepository, times(1)).save(any());
  }

  @Test
  void fetchTenantConfigdataByOrgIdAndConfigKeyTest() throws PromiseEngineException {
    TenantConfigdataEntity tenantConfigdataEntity = testUtil.getTenantConfigdataEntity();
    when(tenantConfigdataEntityMapper.toEntity(any(TenantConfigdataDomainDto.class)))
        .thenReturn(tenantConfigdataEntity);
    when(tenantConfigdataEntityMapper.toDomain(any(TenantConfigdataEntity.class)))
        .thenReturn(testUtil.getTenantConfigdataPersistenceDto());
    when(tenantConfigdataRepository.findByOrgIdAndConfigKey(anyString(), anyString()))
        .thenReturn(Optional.of(tenantConfigdataEntity));
    Optional<TenantConfigdataDomainDto> response =
        tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
            TestUtil.ORG_ID, TestUtil.CONFIG_KEY);
    assertEquals(tenantConfigdataEntity.getId(), response.get().getId());
    verify(tenantConfigdataRepository, times(1)).findByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  void fetchTenantConfigdataByOrgIdAndConfigKeyExceptionTest() throws PromiseEngineException {
    when(tenantConfigdataRepository.findByOrgIdAndConfigKey(anyString(), anyString()))
        .thenThrow(new RuntimeException("error"));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
                  TestUtil.ORG_ID, TestUtil.CONFIG_KEY);
            });
    assertEquals(
        "Unable to fetch tenant configuration data by orgId and configKey", ex.getMessage());
    verify(tenantConfigdataRepository, times(1)).findByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  void deleteTenantConfigdataTest() throws PromiseEngineException {
    doNothing().when(tenantConfigdataRepository).delete(any(TenantConfigdataEntity.class));
    tenantConfigdataPersistenceService.deleteTenantConfigdata(
        testUtil.getTenantConfigdataPersistenceDto());
    verify(tenantConfigdataRepository, times(1)).delete(any());
  }

  @Test
  void deleteTenantConfigdataExceptionTest() throws PromiseEngineException {
    TenantConfigdataEntity tenantConfigdataEntity = testUtil.getTenantConfigdataEntity();
    when(tenantConfigdataEntityMapper.toEntity(any(TenantConfigdataDomainDto.class)))
        .thenReturn(tenantConfigdataEntity);
    when(tenantConfigdataEntityMapper.toDomain(any(TenantConfigdataEntity.class)))
        .thenReturn(testUtil.getTenantConfigdataPersistenceDto());
    doThrow(new RuntimeException("error"))
        .when(tenantConfigdataRepository)
        .delete(any(TenantConfigdataEntity.class));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              tenantConfigdataPersistenceService.deleteTenantConfigdata(
                  testUtil.getTenantConfigdataPersistenceDto());
            });
    assertEquals("Unable to delete tenant configuration data", ex.getMessage());
    verify(tenantConfigdataRepository, times(1)).delete(any());
  }
}
