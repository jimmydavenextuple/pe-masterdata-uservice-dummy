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
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.sourcing.rule.cache.domain.SourcingAttributeDefinitionByActiveStatusKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingAttributeDefinitionByActiveStatusValue;
import com.nextuple.sourcing.rule.spring.cache.feign.SourcingAttributeDefinitionFeignImpl;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SourcingAttributesDefinitionFeignClientServiceImplTest {
  @InjectMocks
  private SourcingAttributesDefinitionFeignClientServiceImpl
      sourcingAttributesDefinitionFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          SourcingAttributeDefinitionByActiveStatusKey,
          SourcingAttributeDefinitionByActiveStatusValue,
          String,
          BaseResponse<SourcingAttributesDefinitionResponse>>
      mapper;

  @Mock private SourcingAttributeDefinitionFeignImpl sourcingAttributeDefinitionFeign;

  @Test
  void get() {

    SourcingAttributeDefinitionByActiveStatusKey cacheKey =
        testUtil.getSourcingAttributeDefinitionByActiveStatusCacheKey();
    SourcingAttributeDefinitionByActiveStatusValue cacheValue =
        testUtil.getSourcingAttributeDefinitionByIdActiveStatusValue();
    BaseResponse<SourcingAttributesDefinitionResponse> response =
        testUtil.getSourcingAttributeDefinitionResponse();

    Mockito.when(
            sourcingAttributeDefinitionFeign.getSourcingAttributesDefinitionInActiveStatus(
                any(), any()))
        .thenReturn(response);
    Mockito.when(mapper.responseToCacheValue(response)).thenReturn(cacheValue);

    assertEquals(cacheValue, sourcingAttributesDefinitionFeignClientService.get(cacheKey));
    assertFalse(sourcingAttributesDefinitionFeignClientService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {
    SourcingAttributeDefinitionByActiveStatusKey sourcingAttributeDefinitionByActiveStatusKey =
        testUtil.getSourcingAttributeDefinitionByActiveStatusCacheKey();

    Mockito.when(mapper.responseToCacheValue(any()))
        .thenThrow(new RuntimeException("Error message"));
    var response =
        sourcingAttributesDefinitionFeignClientService.get(
            sourcingAttributeDefinitionByActiveStatusKey);

    assertNotNull(response);
    assertNull(response.getId());

    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
