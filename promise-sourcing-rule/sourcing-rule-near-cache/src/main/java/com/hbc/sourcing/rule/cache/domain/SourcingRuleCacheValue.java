package com.hbc.sourcing.rule.cache.domain;

import com.hbc.core.cache.domain.CacheValue;
import com.hbc.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SourcingRuleCacheValue implements CacheValue {
  FetchPromiseSourcingRuleResponse fetchPromiseSourcingRuleResponse;

  @Override
  public boolean isUndefined() {
    return false;
  }
}
