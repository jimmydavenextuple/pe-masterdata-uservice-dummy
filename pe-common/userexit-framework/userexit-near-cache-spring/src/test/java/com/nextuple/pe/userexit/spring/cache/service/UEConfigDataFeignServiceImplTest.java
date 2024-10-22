/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.userexit.spring.cache.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import com.nextuple.pe.userexit.cache.domain.UEConfigDataCacheKey;
import com.nextuple.pe.userexit.cache.domain.UEConfigDataCacheValue;
import com.nextuple.pe.userexit.spring.cache.feign.UEConfigDataFeignImpl;
import com.nextuple.pe.userexit.spring.cache.mapper.UEConfigDataMapper;
import com.nextuple.pe.userexit.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UEConfigDataFeignServiceImplTest {

  @InjectMocks private UEConfigDataFeignServiceImpl ueConfigDataFeignService;

  @InjectMocks private TestUtil testUtil;

  @Mock private UEConfigDataMapper ueConfigDataMapper;

  @Mock private UEConfigDataFeignImpl ueConfigDataFeign;

  @Test
  void getTest() {
    UEConfigDataCacheKey cacheKey = testUtil.getUEConfigDataCacheKey();
    UEConfigDataCacheValue cacheValue = testUtil.getUEConfigDataCacheValue();
    BaseResponse<UserExitConfigDataDto> response = testUtil.getUEConfigDataBaseResponse();

    Mockito.when(
            ueConfigDataFeign.fetchConfigData(
                cacheKey.getOrgId(),
                cacheKey.getAppName(),
                cacheKey.getServiceName(),
                cacheKey.getUserExitName()))
        .thenReturn(response);
    Mockito.when(ueConfigDataMapper.responseToCacheValue(response)).thenReturn(cacheValue);
    assertEquals(cacheValue, ueConfigDataFeignService.get(cacheKey));
    assertFalse(ueConfigDataFeignService.get(cacheKey).isUndefined());
    verify(ueConfigDataMapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getTestNullCheck() {
    UEConfigDataCacheKey cacheKey = testUtil.getUEConfigDataCacheKey();

    BaseResponse<UserExitConfigDataDto> baseResponse = testUtil.getUEConfigDataBaseResponse();
    baseResponse.setPayload(null);
    Mockito.when(
            ueConfigDataFeign.fetchConfigData(
                cacheKey.getOrgId(),
                cacheKey.getAppName(),
                cacheKey.getServiceName(),
                cacheKey.getUserExitName()))
        .thenReturn(baseResponse);
    var response = ueConfigDataFeignService.get(cacheKey);

    assertNotNull(response);
    assertNull(response.getUserExitConfigDataDto());
    verify(ueConfigDataMapper, times(0)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {
    UEConfigDataCacheKey cacheKey = testUtil.getUEConfigDataCacheKey();

    var response = ueConfigDataFeignService.get(cacheKey);
    assertNotNull(response);
    assertNull(response.getUserExitConfigDataDto());
    verify(ueConfigDataMapper, times(0)).responseToCacheValue(any());
  }
}
