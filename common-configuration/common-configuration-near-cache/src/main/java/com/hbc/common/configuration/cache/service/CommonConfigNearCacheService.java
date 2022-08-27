package com.hbc.common.configuration.cache.service;

import com.hbc.common.configuration.cache.domain.CommonConfigCacheKey;
import com.hbc.common.configuration.cache.domain.CommonConfigCacheValue;
import com.hbc.core.cache.service.GenericNearCacheService;

public interface CommonConfigNearCacheService
    extends GenericNearCacheService<CommonConfigCacheKey, CommonConfigCacheValue> {}
