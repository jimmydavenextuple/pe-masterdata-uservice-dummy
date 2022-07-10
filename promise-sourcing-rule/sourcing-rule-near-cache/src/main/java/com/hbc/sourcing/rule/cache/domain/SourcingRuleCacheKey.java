package com.hbc.sourcing.rule.cache.domain;

import com.hbc.core.cache.domain.CacheKey;
import com.hbc.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SourcingRuleCacheKey implements CacheKey {
  FetchPromiseSourcingRuleRequest fetchPromiseSourcingRuleRequest;
  SourcingRuleCacheKey() {}
}
