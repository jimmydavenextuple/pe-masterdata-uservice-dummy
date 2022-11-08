package com.nextuple.transit.cache.service;

import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.transit.cache.domain.TransitCacheKey;
import com.nextuple.transit.cache.domain.TransitCacheValue;

public interface TransitDataNearCacheService
    extends GenericNearCacheService<TransitCacheKey, TransitCacheValue> {}
