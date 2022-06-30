package com.hbc.common.cache.redis.mapper;

import com.hbc.common.cache.dto.CacheValue;
import com.hbc.common.cache.dto.key.CacheKey;

public interface GenericCacheRedisCacheMapper<K extends CacheKey, V extends CacheValue, RK, RV> {

  RK toRedisCacheKey(K cacheKey);

  RV toRedisCacheValue(K cacheKey, V cacheValue);

  K toCacheKey(RK redisCacheKey);

  V toCacheValue(RK redisCacheKey, RV redisCacheValue);
}
