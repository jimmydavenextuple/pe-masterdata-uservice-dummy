package com.nextuple.sourcing.rule.cache.service;

import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheValue;

public interface SourcingRuleNearCacheService
    extends GenericNearCacheService<SourcingRuleCacheKey, SourcingRuleCacheValue> {}
