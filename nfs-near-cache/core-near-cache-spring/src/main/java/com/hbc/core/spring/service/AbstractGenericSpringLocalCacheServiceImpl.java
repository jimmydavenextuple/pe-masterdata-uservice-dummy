package com.hbc.core.spring.service;

import com.hbc.core.cache.domain.CacheKey;
import com.hbc.core.cache.domain.CacheValue;
import com.hbc.core.cache.service.GenericFeignCacheService;
import com.hbc.core.cache.service.GenericNearCacheService;
import com.hbc.core.registry.NearCacheRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

public abstract class AbstractGenericSpringLocalCacheServiceImpl<
        K extends CacheKey, V extends CacheValue>
    implements GenericNearCacheService<K, V> {

  @Autowired GenericFeignCacheService<K, V> feignCacheService;

  @Autowired NearCacheRegistry registry;

  @Cacheable(cacheManager = "caffeineCacheManager", unless = "#result == null")
  public V get(K key) {
    return feignCacheService.get(key);
  }

  public void delete(K key) {}

  public void deleteAll() {}
}
