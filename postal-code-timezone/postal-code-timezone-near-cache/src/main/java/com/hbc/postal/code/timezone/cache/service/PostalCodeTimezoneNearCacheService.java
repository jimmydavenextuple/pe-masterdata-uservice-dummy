package com.hbc.postal.code.timezone.cache.service;

import com.hbc.core.cache.service.GenericNearCacheService;
import com.hbc.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheKey;
import com.hbc.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheValue;

public interface PostalCodeTimezoneNearCacheService
    extends GenericNearCacheService<PostalCodeTimezoneCacheKey, PostalCodeTimezoneCacheValue> {}
