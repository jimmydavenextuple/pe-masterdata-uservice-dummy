package com.nextuple.core.spring.service;

import com.nextuple.core.cache.domain.CacheKey;
import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.cache.service.GenericFeignCacheService;
import com.nextuple.core.cache.service.GenericFeignService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractGenericFeignClientServiceImpl<
        K extends CacheKey, V extends CacheValue, I, O>
    implements GenericFeignCacheService<K, V> {

  @Override
  public V get(K key) {
    return mapper.responseToCacheValue(feignService.get((mapper.cacheKeyToRequest(key))));
  }

  @Autowired GenericMapper<K, V, I, O> mapper;

  @Autowired GenericFeignService<I, O> feignService;
}
