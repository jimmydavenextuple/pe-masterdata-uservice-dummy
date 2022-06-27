package com.hbc.carrier.cache.service;

import com.hbc.carrier.cache.domain.CarrierCacheKey;
import com.hbc.carrier.cache.domain.CarrierCacheValue;
import com.hbc.core.cache.service.GenericNearCacheService;

public interface CarrierNearCacheService
    extends GenericNearCacheService<CarrierCacheKey, CarrierCacheValue> {}
