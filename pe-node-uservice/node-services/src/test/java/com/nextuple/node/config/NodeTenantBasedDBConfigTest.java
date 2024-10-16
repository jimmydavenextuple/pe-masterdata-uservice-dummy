/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.configuration.feign.ConfigurationFeign;
import com.nextuple.node.TestUtil;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class NodeTenantBasedDBConfigTest {

  @InjectMocks NodeTenantBasedDBConfig nodeTenantBasedDBConfig;
  @Mock ConfigurationFeign configurationFeign;
  @InjectMocks TestUtil testUtil;

  @Test
  void getServiceOptionsHappyPathTest() throws CommonServiceException {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(testUtil.getTenantConfigdataBaseResponse());
    Set<String> response = nodeTenantBasedDBConfig.getNodeTypes(TestUtil.ORG_ID);

    assertNotNull(response);
    assertEquals(3, response.size());
  }

  @Test
  void getServiceOptionsExceptionTest() throws CommonServiceException {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenThrow(new RuntimeException("error"));

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> nodeTenantBasedDBConfig.getNodeTypes(TestUtil.ORG_ID));

    assertNotNull(exception);
    assertEquals("No configuration data found for node types", exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
  }
}
