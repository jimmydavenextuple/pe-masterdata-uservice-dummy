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
import com.nextuple.configuration.inbound.TenantConfigdataRequest;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import com.nextuple.configuration.service.TenantConfigdataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TenantConfigdataControllerTest {
  @InjectMocks private TenantConfigdataController tenantConfigdataController;

  @Mock private TenantConfigdataService tenantConfigdataService;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void addTenantConfigdataTest() throws PromiseEngineException, CommonServiceException {
    TenantConfigdataResponse tenantConfigdataResponse = testUtil.getTenantConfigdataResponse();
    when(tenantConfigdataService.processAddTenantConfigdata(any(TenantConfigdataRequest.class)))
        .thenReturn(tenantConfigdataResponse);
    ResponseEntity<BaseResponse<TenantConfigdataResponse>> response =
        tenantConfigdataController.addTenantConfigdata(testUtil.getTenantConfigdataRequest());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(tenantConfigdataResponse.getId(), response.getBody().getPayload().getId());
    verify(tenantConfigdataService, times(1)).processAddTenantConfigdata(any());
  }

  @Test
  void addTenantConfigdataExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(tenantConfigdataService.processAddTenantConfigdata(any(TenantConfigdataRequest.class)))
        .thenThrow(new RuntimeException("Failed to process add tenant configuration data request"));
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              tenantConfigdataController.addTenantConfigdata(testUtil.getTenantConfigdataRequest());
            });
    assertEquals("Failed to process add tenant configuration data request", ex.getMessage());
    verify(tenantConfigdataService, times(1)).processAddTenantConfigdata(any());
  }

  @Test
  void getTenantConfigdataByOrgIdAndConfigKeyTest()
      throws PromiseEngineException, CommonServiceException {
    TenantConfigdataResponse tenantConfigdataResponse = testUtil.getTenantConfigdataResponse();
    when(tenantConfigdataService.processGetTenantConfigdataByOrgIdAndConfigKey(
            anyString(), anyString()))
        .thenReturn(tenantConfigdataResponse);
    ResponseEntity<BaseResponse<TenantConfigdataResponse>> response =
        tenantConfigdataController.getTenantConfigdataByOrgIdAndConfigKey(
            TestUtil.ORG_ID, TestUtil.CONFIG_KEY);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(tenantConfigdataResponse.getId(), response.getBody().getPayload().getId());
    verify(tenantConfigdataService, times(1))
        .processGetTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  void getTenantConfigdataByOrgIdAndConfigKeyExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(tenantConfigdataService.processGetTenantConfigdataByOrgIdAndConfigKey(
            anyString(), anyString()))
        .thenThrow(
            new RuntimeException(
                "Failed to process get tenant configuration data by orgId and configKey request"));
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              tenantConfigdataController.getTenantConfigdataByOrgIdAndConfigKey(
                  anyString(), anyString());
            });
    assertEquals(
        "Failed to process get tenant configuration data by orgId and configKey request",
        ex.getMessage());
    verify(tenantConfigdataService, times(1))
        .processGetTenantConfigdataByOrgIdAndConfigKey(anyString(), anyString());
  }

  @Test
  void updateTenantConfigdataTest() throws PromiseEngineException, CommonServiceException {
    TenantConfigdataResponse tenantConfigdataResponse = testUtil.getTenantConfigdataResponse();
    tenantConfigdataResponse.setConfigValue(TestUtil.CONFIG_VALUE_2);
    when(tenantConfigdataService.processUpdateTenantConfigdata(anyString(), anyString(), any()))
        .thenReturn(tenantConfigdataResponse);
    ResponseEntity<BaseResponse<TenantConfigdataResponse>> response =
        tenantConfigdataController.updateTenantConfigdata(
            TestUtil.ORG_ID, TestUtil.CONFIG_KEY, testUtil.getTenantConfigdataUpdateRequest());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(tenantConfigdataResponse.getId(), response.getBody().getPayload().getId());
    assertEquals(
        tenantConfigdataResponse.getConfigValue(),
        response.getBody().getPayload().getConfigValue());
    verify(tenantConfigdataService, times(1))
        .processUpdateTenantConfigdata(anyString(), anyString(), any());
  }

  @Test
  void updateTenantConfigdataExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(tenantConfigdataService.processUpdateTenantConfigdata(anyString(), anyString(), any()))
        .thenThrow(
            new RuntimeException("Failed to process update tenant configuration data request"));
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              tenantConfigdataController.updateTenantConfigdata(
                  TestUtil.ORG_ID,
                  TestUtil.CONFIG_KEY,
                  testUtil.getTenantConfigdataUpdateRequest());
            });
    assertEquals("Failed to process update tenant configuration data request", ex.getMessage());
    verify(tenantConfigdataService, times(1))
        .processUpdateTenantConfigdata(anyString(), anyString(), any());
  }

  @Test
  void deleteTenantConfigdataTest() throws PromiseEngineException, CommonServiceException {
    TenantConfigdataResponse tenantConfigdataResponse = testUtil.getTenantConfigdataResponse();
    when(tenantConfigdataService.processDeleteTenantConfigdata(anyString(), anyString()))
        .thenReturn(tenantConfigdataResponse);
    ResponseEntity<BaseResponse<TenantConfigdataResponse>> response =
        tenantConfigdataController.deleteTenantConfigdata(TestUtil.ORG_ID, TestUtil.CONFIG_KEY);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(tenantConfigdataResponse.getId(), response.getBody().getPayload().getId());
    verify(tenantConfigdataService, times(1))
        .processDeleteTenantConfigdata(anyString(), anyString());
  }

  @Test
  void deleteTenantConfigdataExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(tenantConfigdataService.processDeleteTenantConfigdata(anyString(), anyString()))
        .thenThrow(
            new RuntimeException("Failed to process delete tenant configuration data request"));
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              tenantConfigdataController.deleteTenantConfigdata(
                  TestUtil.ORG_ID, TestUtil.CONFIG_KEY);
            });
    assertEquals("Failed to process delete tenant configuration data request", ex.getMessage());
    verify(tenantConfigdataService, times(1))
        .processDeleteTenantConfigdata(anyString(), anyString());
  }
}
