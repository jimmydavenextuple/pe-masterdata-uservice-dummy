package com.hbc.core.cache.service;

import com.hbc.core.cache.domain.CacheKey;
import com.hbc.core.cache.domain.CacheValue;

public interface GenericNearCacheService<K extends CacheKey, V extends CacheValue> {
  V get(K key);

  void delete(K key);

  void selfRegister();

  String getEntityName();

  void deleteAll();
}
