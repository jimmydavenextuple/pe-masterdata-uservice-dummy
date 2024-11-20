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
import com.nextuple.common.userexit.domain.dto.UserExitMetaDataDto;
import com.nextuple.pe.userexit.cache.domain.UEMetaDataCacheKey;
import com.nextuple.pe.userexit.cache.domain.UEMetaDataCacheValue;
import com.nextuple.pe.userexit.spring.cache.feign.UEMetaDataFeignImpl;
import com.nextuple.pe.userexit.spring.cache.mapper.UEMetaDataMapper;
import com.nextuple.pe.userexit.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UEMetaDataFeignServiceImplTest {
  @InjectMocks private UEMetaDataFeignServiceImpl ueMetaDataFeignService;

  @InjectMocks private TestUtil testUtil;

  @Mock private UEMetaDataMapper ueMetaDataMapper;

  @Mock private UEMetaDataFeignImpl ueMetaDataFeign;

  @Test
  void getTest() {
    UEMetaDataCacheKey cacheKey = testUtil.getUEMetaDataCacheKey();
    UEMetaDataCacheValue cacheValue = testUtil.getUEMetaDataCacheValue();
    BaseResponse<UserExitMetaDataDto> response = testUtil.getUEMetaDataBaseResponse();

    Mockito.when(
            ueMetaDataFeign.fetchMetaData(
                cacheKey.getAppName(), cacheKey.getServiceName(), cacheKey.getName()))
        .thenReturn(response);
    Mockito.when(ueMetaDataMapper.responseToCacheValue(response)).thenReturn(cacheValue);
    assertEquals(cacheValue, ueMetaDataFeignService.get(cacheKey));
    assertFalse(ueMetaDataFeignService.get(cacheKey).isUndefined());
    verify(ueMetaDataMapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getTestNullCheck() {
    UEMetaDataCacheKey cacheKey = testUtil.getUEMetaDataCacheKey();

    BaseResponse<UserExitMetaDataDto> baseResponse = testUtil.getUEMetaDataBaseResponse();
    baseResponse.setPayload(null);
    Mockito.when(
            ueMetaDataFeign.fetchMetaData(
                cacheKey.getAppName(), cacheKey.getServiceName(), cacheKey.getName()))
        .thenReturn(baseResponse);
    var response = ueMetaDataFeignService.get(cacheKey);

    assertNotNull(response);
    assertNull(response.getUserExitMetaDataDto());
    verify(ueMetaDataMapper, times(0)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {
    UEMetaDataCacheKey cacheKey = testUtil.getUEMetaDataCacheKey();

    var response = ueMetaDataFeignService.get(cacheKey);
    assertNotNull(response);
    assertNull(response.getUserExitMetaDataDto());
    verify(ueMetaDataMapper, times(0)).responseToCacheValue(any());
  }
}
