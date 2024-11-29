/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.rule.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionResponse;
import com.nextuple.sourcing.rule.cache.domain.GroupDefinitionRuleCacheValue;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupDefinitionRuleMapperTest {
  @InjectMocks private GroupDefinitionRuleMapper groupDefinitionRuleMapper;

  @InjectMocks private TestUtil testUtil;

  @Test
  @DisplayName("Request to cache key test")
  void requestToCacheKeyTest() {
    assertNull(groupDefinitionRuleMapper.requestToCacheKey("request"));
  }

  @Test
  @DisplayName("Cache key to request test")
  void cacheKeyToRequestTest() {
    assertNull(
        groupDefinitionRuleMapper.cacheKeyToRequest(testUtil.getGroupDefinitionRuleCacheKey()));
  }

  @Test
  @DisplayName("Response to cache Value test")
  void responseToCacheValueTest() {
    GroupDefinitionRuleCacheValue cacheValue = testUtil.getGroupDefinitionRuleCacheValue();
    BaseResponse<GroupDefinitionResponse> response =
        testUtil.getBaseResponseOfGroupDefinitionResponse();

    Assertions.assertEquals(cacheValue, groupDefinitionRuleMapper.responseToCacheValue(response));
  }

  @Test
  @DisplayName("Cache value to response test")
  void cacheValueToReponseTest() {
    GroupDefinitionRuleCacheValue cacheValue = testUtil.getGroupDefinitionRuleCacheValue();
    Assertions.assertNull(groupDefinitionRuleMapper.cacheValueToResponse(cacheValue));
  }
}
