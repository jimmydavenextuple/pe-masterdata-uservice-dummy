package com.hbc.sourcing.rule.spring.cache.service;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.hbc.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.hbc.sourcing.rule.cache.domain.SourcingRuleCacheKey;
import com.hbc.sourcing.rule.cache.domain.SourcingRuleCacheValue;
import com.hbc.sourcing.rule.spring.cache.feign.SourcingRuleFeignImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SourcingRuleFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        SourcingRuleCacheKey,
        SourcingRuleCacheValue,
        FetchPromiseSourcingRuleRequest,
        BaseResponse<FetchPromiseSourcingRuleResponse>> {

  @Autowired SourcingRuleFeignImpl sourcingRuleFeign;

  @Autowired
  GenericMapper<
          SourcingRuleCacheKey,
          SourcingRuleCacheValue,
          FetchPromiseSourcingRuleRequest,
          BaseResponse<FetchPromiseSourcingRuleResponse>>
      sourcingRuleMapper;

  @Override
  public SourcingRuleCacheValue get(SourcingRuleCacheKey key) {
    try {
      return sourcingRuleMapper.responseToCacheValue(
          sourcingRuleFeign.get(sourcingRuleMapper.cacheKeyToRequest(key)));
    } catch (RuntimeException ex) {
      return null;
    }
  }
}
