/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.configuration.spring.cache.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.configuration.cache.domain.TenantConfigdataCacheKey;
import com.nextuple.configuration.cache.domain.TenantConfigdataCacheValue;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import com.nextuple.configuration.spring.cache.feign.TenantConfigdataFeignImpl;
import com.nextuple.configuration.spring.cache.util.TestUtil;
import com.nextuple.core.cache.mapper.GenericMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantConfigdataFeignClientServiceImplTest {
  @InjectMocks private TenantConfigdataFeignClientServiceImpl tenantConfigdataFeignClientService;
  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          TenantConfigdataCacheKey,
          TenantConfigdataCacheValue,
          String,
          BaseResponse<TenantConfigdataResponse>>
      mapper;

  @Mock private TenantConfigdataFeignImpl tenantConfigdataFeign;

  @Test
  void get() {
    TenantConfigdataCacheKey cacheKey = testUtil.getTenantConfigdataCacheKey();
    TenantConfigdataCacheValue cacheValue = testUtil.getTenantConfigCacheValue();
    BaseResponse<TenantConfigdataResponse> response = testUtil.getTenantConfigdataResponse();

    when(tenantConfigdataFeign.getTenantConfigdataByOrgIdAndConfigKey(
            cacheKey.getOrgId(), cacheKey.getConfigKey()))
        .thenReturn(response);
    when(mapper.responseToCacheValue(response)).thenReturn(cacheValue);
    assertEquals(cacheValue, tenantConfigdataFeignClientService.get(cacheKey));
    assertFalse(tenantConfigdataFeignClientService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {
    TenantConfigdataCacheKey cacheKey = testUtil.getTenantConfigdataCacheKey();
    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNull(tenantConfigdataFeignClientService.get(cacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
