package com.hbc.core.cache.service;

public interface GenericFeignCacheService<K, V> {

  V get(K request);
}
