package com.nextuple.common.cache.redis.mapper;

import com.nextuple.common.cache.dto.key.CacheKey;
import com.nextuple.common.cache.dto.CacheValue;

public interface GenericCacheRedisCacheMapper<K extends CacheKey, V extends CacheValue, RK, RV> {

    RK toRedisCacheKey(K cacheKey);

    RV toRedisCacheValue(K cacheKey, V cacheValue);

    K toCacheKey(RK redisCacheKey);

    V toCacheValue(RK redisCacheKey, RV redisCacheValue);
}
