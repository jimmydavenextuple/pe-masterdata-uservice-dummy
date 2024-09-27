/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.configuration.domain;

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
import com.nextuple.configuration.TestUtil;
import com.nextuple.configuration.domain.entity.TenantConfigdataEntity;
import com.nextuple.configuration.repository.TenantConfigdataRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TenantConfigdataDomainTest {
  @InjectMocks private TenantConfigdataDomain tenantConfigdataDomain;

  @Mock private TenantConfigdataRepository tenantConfigdataRepository;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveTenantConfigdataTest() throws PromiseEngineException {
    TenantConfigdataEntity tenantConfigdataEntity = testUtil.getTenantConfigdataEntity();
    when(tenantConfigdataRepository.save(any(TenantConfigdataEntity.class)))
        .thenReturn(tenantConfigdataEntity);
    TenantConfigdataEntity savedTenantConfigdataEntity =
        tenantConfigdataDomain.saveTenantConfigdata(tenantConfigdataEntity);
    assertEquals(tenantConfigdataEntity, savedTenantConfigdataEntity);
    verify(tenantConfigdataRepository, times(1)).save(any());
  }

  @Test
  void saveTenantConfigdataExceptionTest() throws PromiseEngineException {
    TenantConfigdataEntity tenantConfigdataEntity = testUtil.getTenantConfigdataEntity();
    when(tenantConfigdataRepository.save(any(TenantConfigdataEntity.class)))
        .thenThrow(new RuntimeException("error"));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              tenantConfigdataDomain.saveTenantConfigdata(tenantConfigdataEntity);
            });
    assertEquals("Unable to save tenant configuration data", ex.getMessage());
    verify(tenantConfigdataRepository, times(1)).save(any());
  }

  @Test
  void fetchTenantConfigdataByOrgIdAndConfigKeyTest() throws PromiseEngineException {
    TenantConfigdataEntity tenantConfigdataEntity = testUtil.getTenantConfigdataEntity();
    when(tenantConfigdataRepository.findByOrgIdAndConfigKey(anyString(), anyString()))
        .thenReturn(Optional.of(tenantConfigdataEntity));
    Optional<TenantConfigdataEntity> response =
        tenantConfigdataDomain.fetchTenantConfigdataByOrgIdAndConfigKey(
            TestUtil.ORG_ID, TestUtil.CONFIG_KEY);
    assertEquals(tenantConfigdataEntity, response.get());
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
              tenantConfigdataDomain.fetchTenantConfigdataByOrgIdAndConfigKey(
                  TestUtil.ORG_ID, TestUtil.CONFIG_KEY);
            });
    assertEquals(
        "Unable to fetch tenant configuration data by orgId and configKey", ex.getMessage());
    verify(tenantConfigdataRepository, times(1)).findByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  void deleteTenantConfigdataTest() throws PromiseEngineException {
    TenantConfigdataEntity tenantConfigdataEntity = testUtil.getTenantConfigdataEntity();
    doNothing().when(tenantConfigdataRepository).delete(any(TenantConfigdataEntity.class));

    tenantConfigdataDomain.deleteTenantConfigdata(tenantConfigdataEntity);

    verify(tenantConfigdataRepository, times(1)).delete(any());
  }

  @Test
  void deleteTenantConfigdataExceptionTest() throws PromiseEngineException {
    TenantConfigdataEntity tenantConfigdataEntity = testUtil.getTenantConfigdataEntity();
    doThrow(new RuntimeException("error"))
        .when(tenantConfigdataRepository)
        .delete(any(TenantConfigdataEntity.class));
    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              tenantConfigdataDomain.deleteTenantConfigdata(tenantConfigdataEntity);
            });
    assertEquals("Unable to delete tenant configuration data", ex.getMessage());
    verify(tenantConfigdataRepository, times(1)).delete(any());
  }
}
