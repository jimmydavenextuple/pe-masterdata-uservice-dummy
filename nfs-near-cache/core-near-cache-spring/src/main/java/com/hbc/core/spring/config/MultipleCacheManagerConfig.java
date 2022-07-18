package com.hbc.core.spring.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@ConditionalOnProperty(value = "near-cache.enabled", havingValue = "true")
@RequiredArgsConstructor
public class MultipleCacheManagerConfig {
  private final CacheProperties cacheProperties;

  @Bean
  public CacheManager caffeineCacheManager() { // NOSONAR
    List<CaffeineCache> caches = new ArrayList<>();
    Map<String, String> map;
    if (cacheProperties.getCacheMap() == null) {
      map = cacheProperties.setCacheDefaults();
    } else {
      map = cacheProperties.getCacheMap();
    }
    for (Map.Entry<String, String> entry : map.entrySet()) {
      String[] properties = entry.getValue().split(",");
      caches.add(
          buildCache(entry.getKey(), Long.parseLong(properties[0]), Long.parseLong(properties[1])));
    }
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(caches);
    return cacheManager;
  }

  private CaffeineCache buildCache(String cacheName, Long maxSize, Long ttl) {
    Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder();
    cacheBuilder.maximumSize(maxSize);
    cacheBuilder.expireAfterWrite(ttl, TimeUnit.SECONDS);
    return new CaffeineCache(cacheName, cacheBuilder.build());
  }
}
