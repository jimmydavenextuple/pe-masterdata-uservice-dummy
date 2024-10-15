/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.configuration.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.configuration.cache.domain.TenantConfigdataCacheKey;
import com.nextuple.configuration.cache.domain.TenantConfigdataCacheValue;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import com.nextuple.configuration.spring.cache.util.TestUtil;
import com.nextuple.core.cache.mapper.GenericMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantConfigdataMapperTest {

  @InjectMocks
  private GenericMapper<
          TenantConfigdataCacheKey,
          TenantConfigdataCacheValue,
          String,
          BaseResponse<TenantConfigdataResponse>>
      genericMapper = new TenantConfigdataMapper();

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(genericMapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(genericMapper.cacheKeyToRequest(new TenantConfigdataCacheKey()));
  }

  @Test
  void responseToCacheValue() {
    TenantConfigdataCacheValue tenantConfigdataCacheValue = testUtil.getTenantConfigCacheValue();
    BaseResponse<TenantConfigdataResponse> response = testUtil.getTenantConfigdataResponse();
    assertEquals(tenantConfigdataCacheValue, genericMapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    TenantConfigdataCacheValue cacheValue = testUtil.getTenantConfigCacheValue();
    assertNull(genericMapper.cacheValueToResponse(cacheValue));
  }
}
