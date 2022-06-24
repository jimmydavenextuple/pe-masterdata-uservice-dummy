package com.nextuple.sourcing.rule.cache.domain;

import com.nextuple.core.cache.domain.CacheKey;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
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
