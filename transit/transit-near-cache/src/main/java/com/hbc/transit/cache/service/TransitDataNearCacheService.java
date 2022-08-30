package com.hbc.transit.cache.service;

import com.hbc.core.cache.service.GenericNearCacheService;
import com.hbc.transit.cache.domain.TransitCacheKey;
import com.hbc.transit.cache.domain.TransitCacheValue;

public interface TransitDataNearCacheService
    extends GenericNearCacheService<TransitCacheKey, TransitCacheValue> {}
