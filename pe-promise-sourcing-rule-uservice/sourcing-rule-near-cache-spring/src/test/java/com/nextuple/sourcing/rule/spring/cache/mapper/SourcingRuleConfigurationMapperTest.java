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
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchSourcingRulesResponse;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleConfigurationFetchRulesKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleConfigurationFetchRulesValue;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SourcingRuleConfigurationMapperTest {
  @InjectMocks
  private GenericMapper<
          SourcingRuleConfigurationFetchRulesKey,
          SourcingRuleConfigurationFetchRulesValue,
          String,
          BaseResponse<FetchSourcingRulesResponse>>
      genericMapper = new SourcingRuleConfigurationMapper();

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(genericMapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(genericMapper.cacheKeyToRequest(new SourcingRuleConfigurationFetchRulesKey()));
  }

  @Test
  void responseToCacheValue() {
    SourcingRuleConfigurationFetchRulesValue sourcingRuleConfigurationFetchRulesValue =
        testUtil.getSourcingRuleConfigurationFetchRuleCacheValue();
    BaseResponse<FetchSourcingRulesResponse> response = testUtil.getFetchSourcingRuleResponse();

    Assertions.assertEquals(
        sourcingRuleConfigurationFetchRulesValue, genericMapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    SourcingRuleConfigurationFetchRulesValue cacheValue =
        testUtil.getSourcingRuleConfigurationFetchRuleCacheValue();
    assertNull(genericMapper.cacheValueToResponse(cacheValue));
  }
}
