package com.nextuple.core.cache.service;

import com.nextuple.core.cache.domain.CacheKey;
import com.nextuple.core.cache.domain.CacheValue;

public interface GenericNearCacheService<K extends CacheKey, V extends CacheValue> {
  V get(K key);
}
