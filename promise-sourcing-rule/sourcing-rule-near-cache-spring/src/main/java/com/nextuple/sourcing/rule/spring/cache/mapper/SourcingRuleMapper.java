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
