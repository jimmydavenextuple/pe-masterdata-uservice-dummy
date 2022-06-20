package com.nextuple.postal.code.timezone.cache.service;

import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheValue;

public interface PostalCodeTimezoneNearCacheService
    extends GenericNearCacheService<PostalCodeTimezoneCacheKey, PostalCodeTimezoneCacheValue> {}
