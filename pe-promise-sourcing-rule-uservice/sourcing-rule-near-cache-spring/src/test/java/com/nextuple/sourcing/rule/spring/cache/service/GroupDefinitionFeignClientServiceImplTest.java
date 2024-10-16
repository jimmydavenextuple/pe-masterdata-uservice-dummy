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
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionListResponse;
import com.nextuple.sourcing.rule.cache.domain.GroupDefinitionCacheKey;
import com.nextuple.sourcing.rule.cache.domain.GroupDefinitionCacheValue;
import com.nextuple.sourcing.rule.spring.cache.feign.GroupDefinitionFeignImpl;
import com.nextuple.sourcing.rule.spring.cache.mapper.GroupDefinitionMapper;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupDefinitionFeignClientServiceImplTest {
  @InjectMocks private GroupDefinitionFeignClientServiceImpl groupDefinitionFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock private GroupDefinitionMapper groupDefinitionMapper;

  @Mock private GroupDefinitionFeignImpl groupDefinitionFeign;

  @Test
  void get() {

    GroupDefinitionCacheKey cacheKey = testUtil.getGroupDescriptionCacheKey();
    GroupDefinitionCacheValue cacheValue = testUtil.getGroupDefinitionCacheValue();
    BaseResponse<GroupDefinitionListResponse> response = testUtil.getGroupDefinitionResponse();

    Mockito.when(
            groupDefinitionFeign.getGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
                cacheKey.getOrgId(), cacheKey.getSourcingAttributesDefinitionId()))
        .thenReturn(response);
    Mockito.when(groupDefinitionMapper.responseToCacheValue(response)).thenReturn(cacheValue);
    assertEquals(cacheValue, groupDefinitionFeignClientService.get(cacheKey));
    assertFalse(groupDefinitionFeignClientService.get(cacheKey).isUndefined());
    verify(groupDefinitionMapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {
    GroupDefinitionCacheKey groupDefinitionCacheKey = new GroupDefinitionCacheKey();

    Mockito.when(groupDefinitionMapper.responseToCacheValue(any()))
        .thenThrow(new RuntimeException("Error message"));
    var response = groupDefinitionFeignClientService.get(groupDefinitionCacheKey);

    assertNotNull(response);
    assertNull(response.getGroupDefinitionListResponse());
    verify(groupDefinitionMapper, times(1)).responseToCacheValue(any());
  }
}
