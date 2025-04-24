/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
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
import com.nextuple.configuration.inbound.TenantConfigdataRequest;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import com.nextuple.configuration.persistence.domain.ConfigMetadataDomainDto;
import com.nextuple.configuration.persistence.domain.TenantConfigdataDomainDto;
import com.nextuple.configuration.persistence.service.ConfigMetadataPersistenceService;
import com.nextuple.configuration.persistence.service.TenantConfigdataPersistenceService;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TenantConfigdataServiceTest {

  @InjectMocks private TenantConfigdataService tenantConfigdataService;

  @Mock private TenantConfigdataPersistenceService tenantConfigdataPersistenceService;

  @Mock private ConfigMetadataPersistenceService configMetadataPersistenceService;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processAddTenantConfigdataTest() throws PromiseEngineException, CommonServiceException {
    TenantConfigdataResponse tenantConfigdataResponse = testUtil.getTenantConfigdataResponse();
    when(tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
            anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(tenantConfigdataPersistenceService.saveTenantConfigdata(
            any(TenantConfigdataDomainDto.class)))
        .thenReturn(testUtil.getTenantConfigdataDomainDto());

    TenantConfigdataResponse response =
        tenantConfigdataService.processAddTenantConfigdata(testUtil.getTenantConfigdataRequest());
    assertEquals(tenantConfigdataResponse.getId(), response.getId());
    verify(tenantConfigdataPersistenceService, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
    verify(tenantConfigdataPersistenceService, times(1)).saveTenantConfigdata(any());
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
    when(tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
            anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantConfigdataDomainDto()));
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
    verify(tenantConfigdataPersistenceService, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  void processGetTenantConfigdataByOrgIdAndConfigKeyTest()
      throws PromiseEngineException, CommonServiceException {
    TenantConfigdataResponse tenantConfigdataResponse = testUtil.getTenantConfigdataResponse();
    when(tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
            anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantConfigdataDomainDto()));
    TenantConfigdataResponse response =
        tenantConfigdataService.processGetTenantConfigdataByOrgIdAndConfigKey(
            TestUtil.ORG_ID, TestUtil.CONFIG_KEY);
    assertEquals(tenantConfigdataResponse.getId(), response.getId());
    verify(tenantConfigdataPersistenceService, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  void processGetTenantConfigdataByOrgIdAndConfigKeyDefaultValueTest()
      throws PromiseEngineException, CommonServiceException {
    ConfigMetadataDomainDto configMetadataEntity = testUtil.getConfigMetadataDomainDto();
    when(tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
            anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(configMetadataPersistenceService.fetchConfigMetadataByConfigKey(anyString()))
        .thenReturn(Optional.of(configMetadataEntity));
    TenantConfigdataResponse response =
        tenantConfigdataService.processGetTenantConfigdataByOrgIdAndConfigKey(
            TestUtil.ORG_ID_2, TestUtil.CONFIG_KEY);
    assertEquals(configMetadataEntity.getDefaultConfigValue(), response.getConfigValue());
    verify(tenantConfigdataPersistenceService, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
    verify(configMetadataPersistenceService, times(1)).fetchConfigMetadataByConfigKey(anyString());
  }

  @Test
  void processGetTenantConfigdataByOrgIdAndConfigKeyExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
            anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(configMetadataPersistenceService.fetchConfigMetadataByConfigKey(anyString()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              tenantConfigdataService.processGetTenantConfigdataByOrgIdAndConfigKey(
                  TestUtil.ORG_ID, TestUtil.CONFIG_KEY);
            });
    assertEquals("Tenant configuration data not found", ex.getMessage());
    verify(tenantConfigdataPersistenceService, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
    verify(configMetadataPersistenceService, times(1)).fetchConfigMetadataByConfigKey(anyString());
  }

  @Test
  void processUpdateTenantConfigdataTest() throws PromiseEngineException, CommonServiceException {
    TenantConfigdataResponse tenantConfigdataResponse = testUtil.getTenantConfigdataResponse();
    tenantConfigdataResponse.setConfigValue(TestUtil.CONFIG_VALUE_2);
    when(tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
            anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantConfigdataDomainDto()));
    when(tenantConfigdataPersistenceService.saveTenantConfigdata(
            any(TenantConfigdataDomainDto.class)))
        .thenReturn(testUtil.getUpdatedTenantConfigdataDomainDto());
    TenantConfigdataResponse response =
        tenantConfigdataService.processUpdateTenantConfigdata(
            TestUtil.ORG_ID, TestUtil.CONFIG_KEY, testUtil.getTenantConfigdataUpdateRequest());
    assertEquals(tenantConfigdataResponse.getId(), response.getId());
    assertEquals(tenantConfigdataResponse.getConfigKey(), response.getConfigKey());
    assertEquals(tenantConfigdataResponse.getConfigValue(), response.getConfigValue());
    verify(tenantConfigdataPersistenceService, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  void processUpdateTenantConfigdataExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
            anyString(), anyString()))
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
    verify(tenantConfigdataPersistenceService, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  void processDeleteTenantConfigdataExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    TenantConfigdataResponse tenantConfigdataResponse = testUtil.getTenantConfigdataResponse();
    when(tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
            anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantConfigdataDomainDto()));
    TenantConfigdataResponse response =
        tenantConfigdataService.processDeleteTenantConfigdata(TestUtil.ORG_ID, TestUtil.CONFIG_KEY);
    assertEquals(tenantConfigdataResponse.getId(), response.getId());
    verify(tenantConfigdataPersistenceService, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  void processDeleteTenantConfigdataTest() throws PromiseEngineException, CommonServiceException {
    when(tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
            anyString(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              tenantConfigdataService.processDeleteTenantConfigdata(
                  TestUtil.ORG_ID, TestUtil.CONFIG_KEY);
            });
    assertEquals("Tenant configuration data not found", ex.getMessage());
    verify(tenantConfigdataPersistenceService, times(1))
        .fetchTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  @DisplayName("Test to check the ConfigValue format")
  void validateConfigValueFormatTest() throws CommonServiceException {
    TenantConfigdataRequest tenantConfigdataRequest =
        TenantConfigdataRequest.builder()
            .configKey("target-gross-profit-margins-selected-attribute")
            .configValue("item,itemCategory")
            .build();
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> tenantConfigdataService.validateConfigValueFormat(tenantConfigdataRequest));
    tenantConfigdataRequest.setConfigKey("service-option");
    Assertions.assertDoesNotThrow(
        () -> tenantConfigdataService.validateConfigValueFormat(tenantConfigdataRequest));
  }

  @Test
  @DisplayName("Should call update method when existing config data is found")
  void upsertTenantConfigdata_existingData_shouldCallUpdate()
      throws PromiseEngineException, CommonServiceException {

    TenantConfigdataRequest request = testUtil.getTenantConfigdataRequest();
    TenantConfigdataDomainDto existingDto = testUtil.getTenantConfigdataDomainDto();
    TenantConfigdataResponse expectedResponse = testUtil.getTenantConfigdataResponse();

    when(tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
            request.getOrgId(), request.getConfigKey()))
        .thenReturn(Optional.of(existingDto));

    when(tenantConfigdataPersistenceService.saveTenantConfigdata(any()))
        .thenReturn(testUtil.getTenantConfigdataDomainDto());

    TenantConfigdataResponse response = tenantConfigdataService.upsertTenantConfigdata(request);

    assertEquals(expectedResponse.getId(), response.getId());
    verify(tenantConfigdataPersistenceService, times(2))
        .fetchTenantConfigdataByOrgIdAndConfigKey(request.getOrgId(), request.getConfigKey());
  }

  @Test
  @DisplayName("Should call add method when no existing config data is found")
  void upsertTenantConfigdata_noExistingData_shouldCallAdd()
      throws PromiseEngineException, CommonServiceException {

    TenantConfigdataRequest request = testUtil.getTenantConfigdataRequest();
    TenantConfigdataResponse expectedResponse = testUtil.getTenantConfigdataResponse();

    when(tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
            request.getOrgId(), request.getConfigKey()))
        .thenReturn(Optional.empty());

    when(tenantConfigdataPersistenceService.saveTenantConfigdata(any()))
        .thenReturn(testUtil.getTenantConfigdataDomainDto());

    TenantConfigdataResponse response = tenantConfigdataService.upsertTenantConfigdata(request);

    assertEquals(expectedResponse.getId(), response.getId());
    verify(tenantConfigdataPersistenceService, times(2))
        .fetchTenantConfigdataByOrgIdAndConfigKey(request.getOrgId(), request.getConfigKey());
    verify(tenantConfigdataPersistenceService, times(1)).saveTenantConfigdata(any());
  }

  @Test
  @DisplayName("Should throw exception for invalid configKey format")
  void upsertTenantConfigdata_invalidConfigKey_shouldThrowException() {
    TenantConfigdataRequest invalidRequest = testUtil.getTenantConfigdataRequest();
    invalidRequest.setConfigKey("invalid,key");

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () -> tenantConfigdataService.upsertTenantConfigdata(invalidRequest));

    assertEquals(
        "Invalid format! Only letters and hyphens are allowed in configKey",
        exception.getMessage());
  }
}
