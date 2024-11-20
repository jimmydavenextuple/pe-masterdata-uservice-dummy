/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.rule.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingConstraintsResponse;
import com.nextuple.sourcing.rule.cache.domain.SourcingConstraintCacheKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingConstraintCacheValue;
import com.nextuple.sourcing.rule.spring.cache.feign.SourcingConstraintFeignImpl;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SourcingConstraintFeignClientServiceImplTest {
  @InjectMocks
  private SourcingConstraintFeignClientServiceImpl sourcingConstraintFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          SourcingConstraintCacheKey,
          SourcingConstraintCacheValue,
          String,
          BaseResponse<SourcingConstraintsResponse>>
      mapper;

  @Mock private SourcingConstraintFeignImpl sourcingConstraintFeign;

  @Test
  void get() {

    SourcingConstraintCacheKey cacheKey = testUtil.getSourcingConstraintCacheKey();
    SourcingConstraintCacheValue cacheValue = testUtil.getSourcingConstraintCacheValue();
    BaseResponse<SourcingConstraintsResponse> response = testUtil.getSourcingConstraintResponse();

    Mockito.when(
            sourcingConstraintFeign.fetchSourcingConstraintsList(
                cacheKey.getOrgId(), cacheKey.getGroupId()))
        .thenReturn(response);
    Mockito.when(mapper.responseToCacheValue(response)).thenReturn(cacheValue);
    assertEquals(cacheValue, sourcingConstraintFeignClientService.get(cacheKey));
    assertFalse(sourcingConstraintFeignClientService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {
    SourcingConstraintCacheKey sourcingConstraintCacheKey = new SourcingConstraintCacheKey();

    Mockito.when(mapper.responseToCacheValue(any()))
        .thenThrow(new RuntimeException("Error message"));
    var response = sourcingConstraintFeignClientService.get(sourcingConstraintCacheKey);

    assertNotNull(response);
    assertNull(response.getSourcingConstraintsResponse());
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
