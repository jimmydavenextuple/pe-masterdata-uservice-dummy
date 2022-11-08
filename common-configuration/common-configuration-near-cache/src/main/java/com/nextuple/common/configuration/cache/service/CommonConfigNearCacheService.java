package com.nextuple.common.configuration.cache.service;

import com.nextuple.common.configuration.cache.domain.CommonConfigCacheKey;
import com.nextuple.common.configuration.cache.domain.CommonConfigCacheValue;
import com.nextuple.core.cache.service.GenericNearCacheService;

public interface CommonConfigNearCacheService
    extends GenericNearCacheService<CommonConfigCacheKey, CommonConfigCacheValue> {}
