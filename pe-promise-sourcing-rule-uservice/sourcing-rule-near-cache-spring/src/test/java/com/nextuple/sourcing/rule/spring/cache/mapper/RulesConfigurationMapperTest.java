/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.rule.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.RulesConfigurationResponse;
import com.nextuple.sourcing.rule.cache.domain.RulesConfigurationKey;
import com.nextuple.sourcing.rule.cache.domain.RulesConfigurationValue;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RulesConfigurationMapperTest {
  @InjectMocks private RulesConfigurationMapper rulesConfigurationMapper;

  @InjectMocks private TestUtil testUtil;

  @Test
  @DisplayName("Rules Configuration Mapper: Request to cache key")
  void requestToCacheKeyTest() {
    assertNull(rulesConfigurationMapper.requestToCacheKey("request"));
  }

  @Test
  @DisplayName("Rules Configuration Mapper: Cache key to request")
  void cacheKeyToRequestTest() {
    assertNull(rulesConfigurationMapper.cacheKeyToRequest(new RulesConfigurationKey()));
  }

  @Test
  @DisplayName("Rules Configuration Mapper: Response to cache value")
  void responseToCacheValueTest() {
    RulesConfigurationValue rulesConfigurationValue = testUtil.getRulesConfigurationValue();
    BaseResponse<RulesConfigurationResponse> response = testUtil.getRulesConfigurationResponse();

    Assertions.assertEquals(
        rulesConfigurationValue, rulesConfigurationMapper.responseToCacheValue(response));
  }

  @Test
  @DisplayName("Rules Configuration Mapper: Cache value to response")
  void cacheValueToResponseTest() {
    RulesConfigurationValue rulesConfigurationValue = testUtil.getRulesConfigurationValue();
    assertNull(rulesConfigurationMapper.cacheValueToResponse(rulesConfigurationValue));
  }
}
