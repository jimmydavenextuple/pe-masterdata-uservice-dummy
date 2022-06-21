package com.nextuple.sourcing.rule.cache.domain;

import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SourcingRuleCacheValue implements CacheValue {
  FetchPromiseSourcingRuleResponse fetchPromiseSourcingRuleResponse;
}
