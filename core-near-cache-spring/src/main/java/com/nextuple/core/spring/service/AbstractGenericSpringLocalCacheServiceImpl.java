package com.nextuple.core.spring.service;

import com.nextuple.core.cache.domain.CacheKey;
import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.core.cache.service.GenericFeignCacheService;
import com.nextuple.core.cache.service.GenericNearCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import com.nextuple.core.NearCacheRegistry;

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
}
