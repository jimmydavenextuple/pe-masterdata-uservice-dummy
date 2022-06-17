package com.nextuple.carrier.cache.service;

import com.nextuple.carrier.cache.domain.CarrierCacheKey;
import com.nextuple.carrier.cache.domain.CarrierCacheValue;
import com.nextuple.core.cache.service.GenericNearCacheService;

public interface CarrierNearCacheService
    extends GenericNearCacheService<CarrierCacheKey, CarrierCacheValue> {}
