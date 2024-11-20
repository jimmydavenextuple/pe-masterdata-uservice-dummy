package com.nextuple.core.spring.service;

import com.nextuple.core.cache.domain.CacheKey;
import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.core.cache.domain.CacheValueWithExceptionValue;
import com.nextuple.core.cache.service.GenericFeignCacheService;
import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.core.registry.NearCacheRegistry;
import java.util.Objects;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.caffeine.CaffeineCacheManager;

public abstract class AbstractGenericSpringLocalCacheServiceImpl<
        K extends CacheKey, V extends CacheValue>
    implements GenericNearCacheService<K, V> {

  @Getter
  @Qualifier("customCaffeineCacheManager")
  @Autowired(required = false)
  CaffeineCacheManager cacheManager;

  @Autowired GenericFeignCacheService<K, V> feignCacheService;

  @Autowired NearCacheRegistry registry;

  public V get(K key) {
    if (Objects.isNull(cacheManager)) {
      return feignCacheService.get(key);
    }
    Cache cache = cacheManager.getCache(getCacheName());
    String keyString = key.toString();
    if (Objects.nonNull(cache)) {
      ValueWrapper valueWrapper = cache.get(keyString);
      if (Objects.nonNull(valueWrapper)) {
        V v = (V) valueWrapper.get();
        if (!isExceptionHandlingEnabled()) {
          return v;
        }
        if (v instanceof CacheValueWithExceptionValue
            && ((CacheValueWithExceptionValue) v).getMasterDataException() == null) {
          return v;
        }
      }
    }

    V feignResponse = feignCacheService.get(key);
    if (Objects.nonNull(cache)) {
      if (Objects.nonNull(feignResponse)) cache.put(keyString, feignResponse);
      else if (allowNull()) {
        cache.put(keyString, null);
      }
    }
    return feignResponse;
  }

  public abstract String getCacheName();

  public void delete(K key) {
    if (Objects.isNull(cacheManager)) return;
    Cache cache = cacheManager.getCache(getCacheName());
    String keyString = key.toString();
    if (Objects.nonNull(cache)) {
      cache.evict(keyString);
    }
  }

  public final void deleteAll() {
    if (Objects.isNull(cacheManager)) return;
    Cache cache = cacheManager.getCache(getCacheName());
    if (Objects.nonNull(cache)) cache.clear();
  }

  public boolean allowNull() {
    return false;
  }

  public boolean isExceptionHandlingEnabled() {
    return false;
  }
}
