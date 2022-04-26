package com.nextuple.core.spring.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class MultipleCacheManagerConfig {

  @Value("${near-cache.duration}")
  private Long duration;

  @Value("${near-cache.timeUnit}")
  private TimeUnit timeUnit;

  @Value("${near-cache.maxSize}")
  private Long maxSize;

  @Value("${remote-cache.ttlInHours}")
  private Long ttlInHours;

  @Value("${remote-cache.cachename}")
  private String redisCacheName;

  @Bean
  @Primary
  public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration config =
        RedisCacheConfiguration.defaultCacheConfig()
            .prefixCacheNameWith(redisCacheName)
            .entryTtl(Duration.ofHours(ttlInHours))
            .disableCachingNullValues();
    return RedisCacheManager.builder(connectionFactory).cacheDefaults(config).build();
  }

  @Bean
  public Caffeine caffeineConfig() {
    return Caffeine.newBuilder().maximumSize(maxSize).expireAfterWrite(duration, timeUnit);
  }

  @Bean
  public CacheManager caffeineCacheManager(Caffeine caffeine) {
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCaffeine(caffeine);
    return caffeineCacheManager;
  }
}
