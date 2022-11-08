package com.nextuple.core.cache.mapper;

import com.nextuple.core.cache.domain.CacheKey;
import com.nextuple.core.cache.domain.CacheValue;

public interface GenericMapper<K extends CacheKey, V extends CacheValue, I, O> {

  K requestToCacheKey(I request);

  I cacheKeyToRequest(K cacheKey);

  V responseToCacheValue(O resp);

  O cacheValueToResponse(V cacheValue);
}
