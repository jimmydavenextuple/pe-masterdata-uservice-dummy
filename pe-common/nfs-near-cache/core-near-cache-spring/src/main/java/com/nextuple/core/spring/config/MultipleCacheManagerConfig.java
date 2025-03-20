package com.nextuple.core.spring.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.nextuple.core.spring.generator.StringCacheKeyGenerator;
import com.nextuple.core.spring.impl.CustomCompositeCacheManager;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@ConditionalOnProperty(value = "nearcache.enabled", havingValue = "true")
@RequiredArgsConstructor
public class MultipleCacheManagerConfig implements CachingConfigurer {
  private final CacheProperties cacheProperties;

  @Autowired(required = false)
  RedissonClient redissonClient;

  @Bean("caffeineCacheManager")
  public CacheManager cacheManager() { // NOSONAR
    var cacheManager = new CaffeineCacheManager();
    Map<String, String> map;
    if (cacheProperties.getCachemap() == null) {
      map = cacheProperties.setCacheDefaults();
    } else {
      map = cacheProperties.getCachemap();
    }
    for (Map.Entry<String, String> entry : map.entrySet()) {
      String[] properties = entry.getValue().split(",");

      Caffeine<Object, Object> cacheBuilder =
          buildCacheBuilders(Long.parseLong(properties[0]), Long.parseLong(properties[1]));
      cacheManager.registerCustomCache(entry.getKey(), cacheBuilder.build());
    }
    return cacheManager;
  }

  @Bean("customCaffeineCacheManager")
  public CaffeineCacheManager caffeineCacheManager() {
    return (CaffeineCacheManager) cacheManager();
  }

  @Override
  @Bean("stringCacheKeyGenerator")
  public KeyGenerator keyGenerator() {
    return new StringCacheKeyGenerator();
  }

  private Caffeine<Object, Object> buildCacheBuilders(Long maxSize, Long ttl) {
    Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder();
    cacheBuilder.maximumSize(maxSize);
    cacheBuilder.expireAfterWrite(ttl, TimeUnit.HOURS);
    cacheBuilder.recordStats();
    return cacheBuilder;
  }

  @ConditionalOnProperty(name = "nearcache.redis-enabled", havingValue = "true")
  @Bean("redisCacheManager")
  public CacheManager redisCacheManager() {
    Map<String, org.redisson.spring.cache.CacheConfig> cacheConfigMap = new HashMap<>();
    Map<String, String> map =
        cacheProperties.getCachemap() == null
            ? cacheProperties.setCacheDefaults()
            : cacheProperties.getCachemap();

    for (Map.Entry<String, String> entry : map.entrySet()) {
      String[] properties = entry.getValue().split(",");
      long ttl = Long.parseLong(properties[1]);
      cacheConfigMap.put(
          entry.getKey(),
          new org.redisson.spring.cache.CacheConfig(Duration.ofHours(ttl).toMillis(), 0));
    }

    return new RedissonSpringCacheManager(redissonClient, cacheConfigMap);
  }

  @Bean("compositeCacheManager")
  public CacheManager compositeCacheManager(
      CustomCompositeCacheManager customCompositeCacheManager) {
    return customCompositeCacheManager;
  }
}
