/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.configuration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.configuration.TestUtil;
import com.nextuple.configuration.inbound.ConfigMetadataRequest;
import com.nextuple.configuration.inbound.ConfigMetadataUpdateRequest;
import com.nextuple.configuration.outbound.ConfigMetadataResponse;
import com.nextuple.configuration.service.ConfigMetadataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ConfigMetadataControllerTest {

  @InjectMocks private ConfigMetadataController configMetadataController;

  @Mock private ConfigMetadataService configMetadataService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void addConfigMetadataTest() throws PromiseEngineException, CommonServiceException {
    ConfigMetadataResponse configMetadataResponse = testUtil.getConfigMetadataResponse();
    when(configMetadataService.processAddConfigMetadata(any(ConfigMetadataRequest.class)))
        .thenReturn(configMetadataResponse);
    ResponseEntity<BaseResponse<ConfigMetadataResponse>> response =
        configMetadataController.addConfigMetadata(testUtil.getConfigMetadataRequest());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(configMetadataResponse.getId(), response.getBody().getPayload().getId());
    verify(configMetadataService, times(1))
        .processAddConfigMetadata(any(ConfigMetadataRequest.class));
  }

  @Test
  void addConfigMetadataExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(configMetadataService.processAddConfigMetadata(any(ConfigMetadataRequest.class)))
        .thenThrow(new RuntimeException("Failed to process update configuration metadata request"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              configMetadataController.addConfigMetadata(testUtil.getConfigMetadataRequest());
            });
    assertEquals("Failed to process update configuration metadata request", ex.getMessage());
    verify(configMetadataService, times(1))
        .processAddConfigMetadata(any(ConfigMetadataRequest.class));
  }

  @Test
  void getConfigMetadataByConfigKeyTest() throws PromiseEngineException, CommonServiceException {
    ConfigMetadataResponse configMetadataResponse = testUtil.getConfigMetadataResponse();
    when(configMetadataService.getConfigMetadataByConfigKey(anyString()))
        .thenReturn(configMetadataResponse);

    ResponseEntity<BaseResponse<ConfigMetadataResponse>> response =
        configMetadataController.getConfigMetadataByConfigKey(TestUtil.CONFIG_KEY);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(configMetadataResponse.getId(), response.getBody().getPayload().getId());
    verify(configMetadataService, times(1)).getConfigMetadataByConfigKey(anyString());
  }

  @Test
  void getConfigMetadataByConfigKeyExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(configMetadataService.getConfigMetadataByConfigKey(anyString()))
        .thenThrow(
            new RuntimeException(
                "Failed to process get configuration metadata by configKey request"));
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              configMetadataController.getConfigMetadataByConfigKey(TestUtil.CONFIG_KEY);
            });
    assertEquals(
        "Failed to process get configuration metadata by configKey request", ex.getMessage());
    verify(configMetadataService, times(1)).getConfigMetadataByConfigKey(anyString());
  }

  @Test
  void updateConfigMetadataTest() throws PromiseEngineException, CommonServiceException {
    ConfigMetadataResponse configMetadataResponse = testUtil.getConfigMetadataResponse();
    configMetadataResponse.setDefaultConfigValue(TestUtil.DEFAULT_CONFIG_VALUE_2);
    when(configMetadataService.processUpdateConfigMetadata(
            anyString(), any(ConfigMetadataUpdateRequest.class)))
        .thenReturn(configMetadataResponse);
    ResponseEntity<BaseResponse<ConfigMetadataResponse>> response =
        configMetadataController.updateConfigMetadata(
            TestUtil.CONFIG_KEY, testUtil.getConfigMetadataUpdateRequest());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(configMetadataResponse.getId(), response.getBody().getPayload().getId());
    assertEquals(
        configMetadataResponse.getDefaultConfigValue(),
        response.getBody().getPayload().getDefaultConfigValue());
    verify(configMetadataService, times(1)).processUpdateConfigMetadata(anyString(), any());
  }

  @Test
  void updateConfigMetadataExceptionTest() throws PromiseEngineException, CommonServiceException {
    ConfigMetadataResponse configMetadataResponse = testUtil.getConfigMetadataResponse();
    configMetadataResponse.setDefaultConfigValue(TestUtil.DEFAULT_CONFIG_VALUE_2);
    when(configMetadataService.processUpdateConfigMetadata(
            anyString(), any(ConfigMetadataUpdateRequest.class)))
        .thenThrow(new RuntimeException("Failed to process update configuration metadata request"));
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              configMetadataController.updateConfigMetadata(
                  TestUtil.CONFIG_KEY, testUtil.getConfigMetadataUpdateRequest());
            });
    assertEquals("Failed to process update configuration metadata request", ex.getMessage());
    verify(configMetadataService, times(1)).processUpdateConfigMetadata(anyString(), any());
  }

  @Test
  void deleteConfigMetadataTest() throws PromiseEngineException, CommonServiceException {
    ConfigMetadataResponse configMetadataResponse = testUtil.getConfigMetadataResponse();
    when(configMetadataService.processDeleteConfigMetadata(anyString()))
        .thenReturn(configMetadataResponse);
    ResponseEntity<BaseResponse<ConfigMetadataResponse>> response =
        configMetadataController.deleteConfigMetadata(TestUtil.CONFIG_KEY);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(configMetadataResponse.getId(), response.getBody().getPayload().getId());
    verify(configMetadataService, times(1)).processDeleteConfigMetadata(anyString());
  }

  @Test
  void deleteConfigMetadataExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(configMetadataService.processDeleteConfigMetadata(anyString()))
        .thenThrow(new RuntimeException("Failed to process delete configuration metadata request"));
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              configMetadataController.deleteConfigMetadata(TestUtil.CONFIG_KEY);
            });
    assertEquals("Failed to process delete configuration metadata request", ex.getMessage());
    verify(configMetadataService, times(1)).processDeleteConfigMetadata(anyString());
  }
}
