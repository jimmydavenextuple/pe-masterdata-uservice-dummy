/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.rule.spring.cache.mapper;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheValue;
import org.springframework.stereotype.Component;

@Component
public class SourcingRuleMapper
    implements GenericMapper<
        SourcingRuleCacheKey,
        SourcingRuleCacheValue,
        FetchPromiseSourcingRuleRequest,
        BaseResponse<FetchPromiseSourcingRuleResponse>> {

  @Override
  public SourcingRuleCacheKey requestToCacheKey(FetchPromiseSourcingRuleRequest request) {
    return null;
  }

  @Override
  public FetchPromiseSourcingRuleRequest cacheKeyToRequest(SourcingRuleCacheKey cacheKey) {
    return cacheKey.getFetchPromiseSourcingRuleRequest();
  }

  @Override
  public SourcingRuleCacheValue responseToCacheValue(
      BaseResponse<FetchPromiseSourcingRuleResponse> response) {
    return SourcingRuleCacheValue.builder()
        .fetchPromiseSourcingRuleResponse(response.getPayload())
        .build();
  }

  @Override
  public BaseResponse<FetchPromiseSourcingRuleResponse> cacheValueToResponse(
      SourcingRuleCacheValue cacheValue) {
    return null;
  }
}
