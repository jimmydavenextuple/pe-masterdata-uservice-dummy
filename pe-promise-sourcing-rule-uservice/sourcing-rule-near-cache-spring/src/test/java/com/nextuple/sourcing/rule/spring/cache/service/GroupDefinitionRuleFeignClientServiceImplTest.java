/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.rule.spring.cache.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionResponse;
import com.nextuple.sourcing.rule.cache.domain.GroupDefinitionRuleCacheKey;
import com.nextuple.sourcing.rule.cache.domain.GroupDefinitionRuleCacheValue;
import com.nextuple.sourcing.rule.spring.cache.feign.GroupDefinitionRuleFeignImpl;
import com.nextuple.sourcing.rule.spring.cache.mapper.GroupDefinitionRuleMapper;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupDefinitionRuleFeignClientServiceImplTest {
  @InjectMocks
  private GroupDefinitionRuleFeignClientServiceImpl groupDefinitionRuleFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock private GroupDefinitionRuleMapper groupDefinitionRuleMapper;
  @Mock private GroupDefinitionRuleFeignImpl groupDefinitionRuleFeign;

  @Test
  @DisplayName("Get Cache test")
  void get() {
    GroupDefinitionRuleCacheKey groupDefinitionRuleCacheKey =
        testUtil.getGroupDefinitionRuleCacheKey();
    GroupDefinitionRuleCacheValue groupDefinitionRuleCacheValue =
        testUtil.getGroupDefinitionRuleCacheValue();

    BaseResponse<GroupDefinitionResponse> response =
        BaseResponse.builder().payload(testUtil.getGroupDefinitionResponse()).build();

    when(groupDefinitionRuleFeign.fetchGroupDefinitionByScoring(any())).thenReturn(response);
    when(groupDefinitionRuleMapper.responseToCacheValue(any()))
        .thenReturn(groupDefinitionRuleCacheValue);
    GroupDefinitionRuleCacheValue result =
        groupDefinitionRuleFeignClientService.get(groupDefinitionRuleCacheKey);
    Assertions.assertEquals(groupDefinitionRuleCacheValue, result);
    Assertions.assertFalse(result.isUndefined());
    verify(groupDefinitionRuleMapper, times(1)).responseToCacheValue(any());
  }

  @Test
  @DisplayName("Get Cache exception test")
  void getCacheExceptionTest() {
    GroupDefinitionRuleCacheKey groupDefinitionRuleCacheKey =
        testUtil.getGroupDefinitionRuleCacheKey();
    when(groupDefinitionRuleMapper.responseToCacheValue(any()))
        .thenThrow(new RuntimeException("Error occurred"));
    var response = groupDefinitionRuleFeignClientService.get(groupDefinitionRuleCacheKey);
    Assertions.assertNotNull(response);
    Assertions.assertNull(response.getGroupDefinitionResponse());
    verify(groupDefinitionRuleMapper, times(1)).responseToCacheValue(any());
  }
}
