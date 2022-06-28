package com.hbc.sourcing.rule.cache.service;

import com.hbc.core.cache.service.GenericNearCacheService;
import com.hbc.sourcing.rule.cache.domain.SourcingRuleCacheKey;
import com.hbc.sourcing.rule.cache.domain.SourcingRuleCacheValue;

public interface SourcingRuleNearCacheService
    extends GenericNearCacheService<SourcingRuleCacheKey, SourcingRuleCacheValue> {}
