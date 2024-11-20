/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.rule.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionListResponse;
import com.nextuple.sourcing.rule.cache.domain.GroupDefinitionCacheKey;
import com.nextuple.sourcing.rule.cache.domain.GroupDefinitionCacheValue;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupDefinitionMapperTest {
  @InjectMocks
  private GenericMapper<
          GroupDefinitionCacheKey,
          GroupDefinitionCacheValue,
          String,
          BaseResponse<GroupDefinitionListResponse>>
      genericMapper = new GroupDefinitionMapper();

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(genericMapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(genericMapper.cacheKeyToRequest(new GroupDefinitionCacheKey()));
  }

  @Test
  void responseToCacheValue() {
    GroupDefinitionCacheValue groupDefinitionCacheValue = testUtil.getGroupDefinitionCacheValue();
    BaseResponse<GroupDefinitionListResponse> response = testUtil.getGroupDefinitionResponse();

    Assertions.assertEquals(
        groupDefinitionCacheValue, genericMapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    GroupDefinitionCacheValue cacheValue = testUtil.getGroupDefinitionCacheValue();
    assertNull(genericMapper.cacheValueToResponse(cacheValue));
  }
}
