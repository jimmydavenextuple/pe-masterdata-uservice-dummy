/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.rule.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheValue;
import com.nextuple.sourcing.rule.spring.cache.feign.SourcingRuleFeignImpl;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SourcingRuleFeignClientServiceImplTest {

  @InjectMocks private SourcingRuleFeignClientServiceImpl sourcingRuleFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          SourcingRuleCacheKey,
          SourcingRuleCacheValue,
          FetchPromiseSourcingRuleRequest,
          BaseResponse<FetchPromiseSourcingRuleResponse>>
      mapper;

  @Mock private SourcingRuleFeignImpl sourcingRuleFeign;

  @Test
  void getTest() {
    SourcingRuleCacheKey cacheKey = testUtil.getSourcingRuleCacheKey();
    SourcingRuleCacheValue cacheValue = testUtil.getSourcingRuleCacheValue();

    BaseResponse<FetchPromiseSourcingRuleResponse> response =
        testUtil.getBaseResponseOfFetchPromiseSourcingRuleResponse();

    when(mapper.cacheKeyToRequest(cacheKey))
        .thenReturn(cacheKey.getFetchPromiseSourcingRuleRequest());
    when(sourcingRuleFeign.get(cacheKey.getFetchPromiseSourcingRuleRequest())).thenReturn(response);
    when(mapper.responseToCacheValue(response)).thenReturn(cacheValue);

    assertEquals(cacheValue, sourcingRuleFeignClientService.get(cacheKey));
    assertFalse(sourcingRuleFeignClientService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {
    SourcingRuleCacheKey inValidCacheKey = testUtil.getSourcingRuleCacheKey();

    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNull(
        sourcingRuleFeignClientService.get(inValidCacheKey).getFetchPromiseSourcingRuleResponse());
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
