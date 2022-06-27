package com.hbc.sourcing.rule.spring.cache.mapper;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.hbc.sourcing.rule.cache.domain.SourcingRuleCacheKey;
import com.hbc.sourcing.rule.cache.domain.SourcingRuleCacheValue;
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
