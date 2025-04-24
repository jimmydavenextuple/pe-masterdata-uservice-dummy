/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.configuration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.configuration.TestUtil;
import com.nextuple.configuration.inbound.ConfigMetadataBaseRequest;
import com.nextuple.configuration.inbound.ConfigMetadataRequest;
import com.nextuple.configuration.outbound.ConfigMetadataResponse;
import com.nextuple.configuration.persistence.domain.ConfigMetadataDomainDto;
import com.nextuple.configuration.persistence.service.ConfigMetadataPersistenceService;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ConfigMetadataServiceTest {
  @InjectMocks private ConfigMetadataService configMetadataService;

  @Mock private ConfigMetadataPersistenceService configMetadataPersistenceService;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processAddConfigMetadataTest() throws PromiseEngineException, CommonServiceException {
    ConfigMetadataResponse configMetadataResponse = testUtil.getConfigMetadataResponse();
    when(configMetadataPersistenceService.fetchConfigMetadataByConfigKey(anyString()))
        .thenReturn(Optional.empty());
    when(configMetadataPersistenceService.saveConfigMetadata(any(ConfigMetadataDomainDto.class)))
        .thenReturn(testUtil.getConfigMetadataDomainDto());
    ConfigMetadataResponse response =
        configMetadataService.processAddConfigMetadata(testUtil.getConfigMetadataRequest());
    assertEquals(configMetadataResponse.getId(), response.getId());
    verify(configMetadataPersistenceService, times(1)).fetchConfigMetadataByConfigKey(anyString());
    verify(configMetadataPersistenceService, times(1)).saveConfigMetadata(any());
  }

  @Test
  void processAddConfigMetadataInvalidConfigKeyExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    ConfigMetadataRequest configMetadataRequest = new ConfigMetadataRequest();
    configMetadataRequest.setConfigKey("custom--Key");
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              configMetadataService.processAddConfigMetadata(configMetadataRequest);
            });
    assertEquals(
        "Invalid format! Only letters and hyphens are allowed in configKey", ex.getMessage());
  }

  @Test
  void processAddConfigMetadataExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(configMetadataPersistenceService.fetchConfigMetadataByConfigKey(anyString()))
        .thenReturn(Optional.of(testUtil.getConfigMetadataDomainDto()));
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              configMetadataService.processAddConfigMetadata(testUtil.getConfigMetadataRequest());
            });
    assertEquals("Configuration metadata already associated for given configKey", ex.getMessage());
    verify(configMetadataPersistenceService, times(1)).fetchConfigMetadataByConfigKey(anyString());
  }

  @Test
  void getConfigMetadataByConfigKeyTest() throws PromiseEngineException, CommonServiceException {
    ConfigMetadataResponse configMetadataResponse = testUtil.getConfigMetadataResponse();
    when(configMetadataPersistenceService.fetchConfigMetadataByConfigKey(anyString()))
        .thenReturn(Optional.of(testUtil.getConfigMetadataDomainDto()));
    ConfigMetadataResponse response =
        configMetadataService.getConfigMetadataByConfigKey(TestUtil.CONFIG_KEY);
    assertEquals(configMetadataResponse.getId(), response.getId());
    verify(configMetadataPersistenceService, times(1)).fetchConfigMetadataByConfigKey(anyString());
  }

  @Test
  void getConfigMetadataByConfigKeyExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(configMetadataPersistenceService.fetchConfigMetadataByConfigKey(anyString()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              configMetadataService.getConfigMetadataByConfigKey(TestUtil.CONFIG_KEY);
            });
    assertEquals("Configuration metadata not found for given configKey", ex.getMessage());
    verify(configMetadataPersistenceService, times(1)).fetchConfigMetadataByConfigKey(anyString());
  }

  @Test
  void processUpdateConfigMetadataTest() throws PromiseEngineException, CommonServiceException {
    ConfigMetadataResponse configMetadataResponse = testUtil.getConfigMetadataResponse();
    configMetadataResponse.setDefaultConfigValue(TestUtil.DEFAULT_CONFIG_VALUE_2);
    when(configMetadataPersistenceService.fetchConfigMetadataByConfigKey(anyString()))
        .thenReturn(Optional.of(testUtil.getConfigMetadataDomainDto()));
    when(configMetadataPersistenceService.saveConfigMetadata(any(ConfigMetadataDomainDto.class)))
        .thenReturn(testUtil.getUpdatedConfigMetadataDomainDto());
    ConfigMetadataResponse response =
        configMetadataService.processUpdateConfigMetadata(
            TestUtil.CONFIG_KEY, testUtil.getConfigMetadataUpdateRequest());
    assertEquals(configMetadataResponse.getId(), response.getId());
    assertEquals(configMetadataResponse.getDefaultConfigValue(), response.getDefaultConfigValue());
    verify(configMetadataPersistenceService, times(1)).fetchConfigMetadataByConfigKey(anyString());
  }

  @Test
  void processUpdateConfigMetadataExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(configMetadataPersistenceService.fetchConfigMetadataByConfigKey(anyString()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              configMetadataService.processUpdateConfigMetadata(
                  TestUtil.CONFIG_KEY, testUtil.getConfigMetadataUpdateRequest());
            });
    assertEquals("Configuration metadata not found for given configKey", ex.getMessage());
    verify(configMetadataPersistenceService, times(1)).fetchConfigMetadataByConfigKey(anyString());
  }

  @Test
  void processDeleteConfigMetadataTest() throws PromiseEngineException, CommonServiceException {
    ConfigMetadataResponse configMetadataResponse = testUtil.getConfigMetadataResponse();
    when(configMetadataPersistenceService.fetchConfigMetadataByConfigKey(anyString()))
        .thenReturn(Optional.of(testUtil.getConfigMetadataDomainDto()));
    doNothing()
        .when(configMetadataPersistenceService)
        .deleteConfigMetadata(any(ConfigMetadataDomainDto.class));
    ConfigMetadataResponse response =
        configMetadataService.processDeleteConfigMetadata(TestUtil.CONFIG_KEY);
    assertEquals(configMetadataResponse.getId(), response.getId());
    verify(configMetadataPersistenceService, times(1)).fetchConfigMetadataByConfigKey(anyString());
    verify(configMetadataPersistenceService, times(1))
        .deleteConfigMetadata(any(ConfigMetadataDomainDto.class));
  }

  @Test
  void processDeleteConfigMetadataExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(configMetadataPersistenceService.fetchConfigMetadataByConfigKey(anyString()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              configMetadataService.processDeleteConfigMetadata(TestUtil.CONFIG_KEY);
            });
    assertEquals("Configuration metadata not found for given configKey", ex.getMessage());
    verify(configMetadataPersistenceService, times(1)).fetchConfigMetadataByConfigKey(anyString());
  }

  @Test
  void serializableClassTest() {
    ConfigMetadataBaseRequest request = new ConfigMetadataBaseRequest();
    assertTrue(request instanceof Serializable);
  }

  @Test
  void serialVersionUIDTest() throws NoSuchFieldException, IllegalAccessException {
    Field field = ConfigMetadataBaseRequest.class.getDeclaredField("serialVersionUID");
    field.setAccessible(true);
    assertNotNull(field);
    assertEquals(-8804756792804347818L, field.getLong(null));
  }

  @Test
  @DisplayName("Should update config metadata when configKey already exists")
  void upsertConfigMetadata_UpdateFlow_Success()
      throws PromiseEngineException, CommonServiceException {
    ConfigMetadataRequest request = testUtil.getConfigMetadataRequest();
    ConfigMetadataDomainDto existingDto = testUtil.getConfigMetadataDomainDto();
    ConfigMetadataDomainDto updatedDto = testUtil.getUpdatedConfigMetadataDomainDto();
    ConfigMetadataResponse expectedResponse = testUtil.getConfigMetadataResponse();

    when(configMetadataPersistenceService.fetchConfigMetadataByConfigKey(request.getConfigKey()))
        .thenReturn(Optional.of(existingDto));

    when(configMetadataPersistenceService.saveConfigMetadata(any(ConfigMetadataDomainDto.class)))
        .thenReturn(updatedDto);

    ConfigMetadataResponse actualResponse = configMetadataService.upsertConfigMetadata(request);

    assertNotNull(actualResponse);
    assertEquals(expectedResponse.getId(), actualResponse.getId());

    verify(configMetadataPersistenceService, times(2))
        .fetchConfigMetadataByConfigKey(request.getConfigKey());
    verify(configMetadataPersistenceService, times(1))
        .saveConfigMetadata(any(ConfigMetadataDomainDto.class));
  }

  @Test
  @DisplayName("Should throw exception for invalid configKey format in upsert")
  void upsertConfigMetadata_InvalidKey_ThrowsException() {
    ConfigMetadataRequest request = new ConfigMetadataRequest();
    request.setConfigKey("invalid--key");

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> configMetadataService.upsertConfigMetadata(request));

    assertEquals(
        "Invalid format! Only letters and hyphens are allowed in configKey",
        exception.getMessage());
  }
}
