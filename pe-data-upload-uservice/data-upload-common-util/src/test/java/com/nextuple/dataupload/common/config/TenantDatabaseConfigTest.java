/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.common.config;

import static com.nextuple.common.constants.ConfigKeyConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.configuration.feign.ConfigurationFeign;
import com.nextuple.dataupload.common.TestUtil;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class TenantDatabaseConfigTest {

  @InjectMocks TenantDatabaseConfig tenantDatabaseConfig;
  @Mock ConfigurationFeign configurationFeign;
  @InjectMocks TestUtil testUtil;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @DisplayName("Test tenant service options with no org id parameter: success path")
  @Test
  void getCurrentTenantServiceOptionsWithoutPassingTenant() throws CommonServiceException {
    CurrentThreadContext.getLogContext().setTenantId(TestUtil.ORG_ID);
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(testUtil.getTenantConfigdataBaseResponse());
    String[] serviceOptions = tenantDatabaseConfig.getCurrentTenantServiceOptions();
    Assertions.assertEquals(TestUtil.tenantServiceOptionExpected, Arrays.asList(serviceOptions));
    verify(configurationFeign, times(1))
        .getTenantConfigdataByOrgIdAndConfigKey(TestUtil.ORG_ID, SERVICE_OPTIONS_CONFIG_KEY);
  }

  @DisplayName("Test tenant service options with org id parameter: success path")
  @Test
  void getCurrentTenantServiceOptionsTest() throws CommonServiceException {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(testUtil.getTenantConfigdataBaseResponse());
    String[] serviceOptions = tenantDatabaseConfig.getCurrentTenantServiceOptions(TestUtil.ORG_ID);
    Assertions.assertEquals(TestUtil.tenantServiceOptionExpected, Arrays.asList(serviceOptions));
    verify(configurationFeign, times(1))
        .getTenantConfigdataByOrgIdAndConfigKey(TestUtil.ORG_ID, SERVICE_OPTIONS_CONFIG_KEY);
  }

  @DisplayName("Test tenant custom attribute: success path")
  @Test
  void getCurrentTenantCustomAttributesTest() throws CommonServiceException {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(testUtil.getTenantCustomAttributeConfigdataBaseResponse());
    Map<String, String> customAttributes =
        tenantDatabaseConfig.getCurrentTenantCustomAttributes(TestUtil.ORG_ID);
    Assertions.assertEquals(TestUtil.CUSTOM_ATTRIBUTES_EXPECTED_CONFIG_MAP, customAttributes);
    verify(configurationFeign, times(1))
        .getTenantConfigdataByOrgIdAndConfigKey(TestUtil.ORG_ID, CUSTOM_ATTRIBUTES_CONFIG_KEY);
  }

  @DisplayName("Test tenant lines custom attribute: success path")
  @Test
  void getCurrentTenantLinesCustomAttributesTest() throws CommonServiceException {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(testUtil.getTenantLinesCustomAttributeConfigdataBaseResponse());
    Map<String, String> linesCustomAttributes =
        tenantDatabaseConfig.getCurrentTenantLinesCustomAttributes(TestUtil.ORG_ID);
    Assertions.assertEquals(
        TestUtil.LINES_CUSTOM_ATTRIBUTES_EXPECTED_CONFIG_MAP, linesCustomAttributes);
    verify(configurationFeign, times(1))
        .getTenantConfigdataByOrgIdAndConfigKey(
            TestUtil.ORG_ID, LINES_CUSTOM_ATTRIBUTES_CONFIG_KEY);
  }

  @DisplayName("Test tenant custom attribute when db not found: success path")
  @Test
  void fetchLinesCustomAttributesNotFoundExceptionTest() throws CommonServiceException {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenThrow(new RuntimeException("Tenant configuration data not found"));
    Map<String, String> linesCustomAttributes =
        tenantDatabaseConfig.getCurrentTenantLinesCustomAttributes(TestUtil.ORG_ID);
    Assertions.assertEquals(Map.of(), linesCustomAttributes);
    verify(configurationFeign, times(1))
        .getTenantConfigdataByOrgIdAndConfigKey(
            TestUtil.ORG_ID, LINES_CUSTOM_ATTRIBUTES_CONFIG_KEY);
  }

  @DisplayName("Test tenant custom attribute with server side exception: failure path")
  @Test
  void fetchLinesCustomAttributesRuntimeExceptionTest() {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenThrow(new RuntimeException("error"));
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> tenantDatabaseConfig.getCurrentTenantLinesCustomAttributes(any()));
    assertEquals("Error while fetching lines-custom-attributes", exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
  }

  @DisplayName("Test fetch db for service options: success path")
  @Test
  void fetchServiceOptionsTest() throws CommonServiceException {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(testUtil.getTenantConfigdataBaseResponse());
    String serviceOptions = tenantDatabaseConfig.fetchServiceOptions(TestUtil.ORG_ID);
    Assertions.assertEquals(TestUtil.tenantServiceOption, serviceOptions);
    verify(configurationFeign, times(1))
        .getTenantConfigdataByOrgIdAndConfigKey(TestUtil.ORG_ID, SERVICE_OPTIONS_CONFIG_KEY);
  }

  @DisplayName("Test fetch db for service options when server exception: failure path")
  @Test
  void fetchServiceOptionsExceptionTest() {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenThrow(new RuntimeException("error"));
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class, () -> tenantDatabaseConfig.fetchServiceOptions(any()));

    assertEquals("No configuration data found for service options", exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
  }
}
