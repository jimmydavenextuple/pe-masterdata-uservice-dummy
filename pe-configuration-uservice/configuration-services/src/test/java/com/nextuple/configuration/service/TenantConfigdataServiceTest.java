/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.configuration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.configuration.TestUtil;
import com.nextuple.configuration.domain.ConfigMetadataDomain;
import com.nextuple.configuration.domain.TenantConfigdataDomain;
import com.nextuple.configuration.domain.entity.ConfigMetadataEntity;
import com.nextuple.configuration.domain.entity.TenantConfigdataEntity;
import com.nextuple.configuration.inbound.TenantConfigdataRequest;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TenantConfigdataServiceTest {
  @InjectMocks private TenantConfigdataService tenantConfigdataService;

  @Mock private TenantConfigdataDomain tenantConfigdataDomain;
  @Mock private ConfigMetadataDomain configMetadataDomain;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processAddTenantConfigdataTest() throws PromiseEngineException, CommonServiceException {
    TenantConfigdataResponse tenantConfigdataResponse = testUtil.getTenantConfigdataResponse();
    when(tenantConfigdataDomain.fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(tenantConfigdataDomain.saveTenantConfigdata(any(TenantConfigdataEntity.class)))
        .thenReturn(testUtil.getTenantConfigdataEntity());

    TenantConfigdataResponse response =
        tenantConfigdataService.processAddTenantConfigdata(testUtil.getTenantConfigdataRequest());
    assertEquals(tenantConfigdataResponse.getId(), response.getId());
    verify(tenantConfigdataDomain, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
    verify(tenantConfigdataDomain, times(1)).saveTenantConfigdata(any());
  }

  @Test
  void processAddTenantConfigdataInvalidConfigKeyExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    TenantConfigdataRequest tenantConfigdataRequest = testUtil.getTenantConfigdataRequest();
    tenantConfigdataRequest.setConfigKey("Custom-Key1");
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              tenantConfigdataService.processAddTenantConfigdata(tenantConfigdataRequest);
            });
    assertEquals(
        "Invalid format! Only letters and hyphens are allowed in configKey", ex.getMessage());
  }

  @Test
  void processAddTenantConfigdataExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(tenantConfigdataDomain.fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantConfigdataEntity()));
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              tenantConfigdataService.processAddTenantConfigdata(
                  testUtil.getTenantConfigdataRequest());
            });
    assertEquals(
        "Tenant configuration data already associated for given orgId and configKey",
        ex.getMessage());
    verify(tenantConfigdataDomain, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  void processGetTenantConfigdataByOrgIdAndConfigKeyTest()
      throws PromiseEngineException, CommonServiceException {
    TenantConfigdataResponse tenantConfigdataResponse = testUtil.getTenantConfigdataResponse();
    when(tenantConfigdataDomain.fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantConfigdataEntity()));
    TenantConfigdataResponse response =
        tenantConfigdataService.processGetTenantConfigdataByOrgIdAndConfigKey(
            TestUtil.ORG_ID, TestUtil.CONFIG_KEY);
    assertEquals(tenantConfigdataResponse.getId(), response.getId());
    verify(tenantConfigdataDomain, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  void processGetTenantConfigdataByOrgIdAndConfigKeyDefaultValueTest()
      throws PromiseEngineException, CommonServiceException {
    ConfigMetadataEntity configMetadataEntity = testUtil.getConfigMetadataEntity();
    when(tenantConfigdataDomain.fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(configMetadataDomain.fetchConfigMetadataByConfigKey(anyString()))
        .thenReturn(Optional.of(configMetadataEntity));
    TenantConfigdataResponse response =
        tenantConfigdataService.processGetTenantConfigdataByOrgIdAndConfigKey(
            TestUtil.ORG_ID_2, TestUtil.CONFIG_KEY);
    assertEquals(configMetadataEntity.getDefaultConfigValue(), response.getConfigValue());
    verify(tenantConfigdataDomain, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
    verify(configMetadataDomain, times(1)).fetchConfigMetadataByConfigKey(anyString());
  }

  @Test
  void processGetTenantConfigdataByOrgIdAndConfigKeyExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(tenantConfigdataDomain.fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(configMetadataDomain.fetchConfigMetadataByConfigKey(anyString()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              tenantConfigdataService.processGetTenantConfigdataByOrgIdAndConfigKey(
                  TestUtil.ORG_ID, TestUtil.CONFIG_KEY);
            });
    assertEquals("Tenant configuration data not found", ex.getMessage());
    verify(tenantConfigdataDomain, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
    verify(configMetadataDomain, times(1)).fetchConfigMetadataByConfigKey(anyString());
  }

  @Test
  void processUpdateTenantConfigdataTest() throws PromiseEngineException, CommonServiceException {
    TenantConfigdataResponse tenantConfigdataResponse = testUtil.getTenantConfigdataResponse();
    tenantConfigdataResponse.setConfigValue(TestUtil.CONFIG_VALUE_2);
    when(tenantConfigdataDomain.fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantConfigdataEntity()));
    when(tenantConfigdataDomain.saveTenantConfigdata(any(TenantConfigdataEntity.class)))
        .thenReturn(testUtil.getUpdatedConfigdataEntity());
    TenantConfigdataResponse response =
        tenantConfigdataService.processUpdateTenantConfigdata(
            TestUtil.ORG_ID, TestUtil.CONFIG_KEY, testUtil.getTenantConfigdataUpdateRequest());
    assertEquals(tenantConfigdataResponse.getId(), response.getId());
    assertEquals(tenantConfigdataResponse.getConfigKey(), response.getConfigKey());
    assertEquals(tenantConfigdataResponse.getConfigValue(), response.getConfigValue());
    verify(tenantConfigdataDomain, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  void processUpdateTenantConfigdataExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(tenantConfigdataDomain.fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              tenantConfigdataService.processUpdateTenantConfigdata(
                  TestUtil.ORG_ID,
                  TestUtil.CONFIG_KEY,
                  testUtil.getTenantConfigdataUpdateRequest());
            });
    assertEquals("Tenant configuration data not found", ex.getMessage());
    verify(tenantConfigdataDomain, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  void processDeleteTenantConfigdataExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    TenantConfigdataResponse tenantConfigdataResponse = testUtil.getTenantConfigdataResponse();
    when(tenantConfigdataDomain.fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantConfigdataEntity()));
    TenantConfigdataResponse response =
        tenantConfigdataService.processDeleteTenantConfigdata(TestUtil.ORG_ID, TestUtil.CONFIG_KEY);
    assertEquals(tenantConfigdataResponse.getId(), response.getId());
    verify(tenantConfigdataDomain, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  void processDeleteTenantConfigdataTest() throws PromiseEngineException, CommonServiceException {
    when(tenantConfigdataDomain.fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              tenantConfigdataService.processDeleteTenantConfigdata(
                  TestUtil.ORG_ID, TestUtil.CONFIG_KEY);
            });
    assertEquals("Tenant configuration data not found", ex.getMessage());
    verify(tenantConfigdataDomain, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
  }
}
