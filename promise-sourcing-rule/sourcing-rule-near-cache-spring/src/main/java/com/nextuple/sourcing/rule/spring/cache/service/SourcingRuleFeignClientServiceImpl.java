package com.nextuple.sourcing.rule.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.sourcing.rule.cache.domain.FetchPromiseSourcingRuleRequest;
import com.nextuple.sourcing.rule.cache.domain.FetchPromiseSourcingRuleResponse;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheValue;
import com.nextuple.sourcing.rule.spring.cache.feign.SourcingRuleFeignImpl;
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
