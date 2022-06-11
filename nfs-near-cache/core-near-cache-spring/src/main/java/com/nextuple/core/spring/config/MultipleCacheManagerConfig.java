package com.nextuple.core.spring.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@ConditionalOnProperty(value = "near-cache.enabled", havingValue = "true")
public class MultipleCacheManagerConfig {

  @Value("${near-cache.duration:60}")
  private Long duration;

  @Value("${near-cache.timeUnit:SECONDS}")
  private TimeUnit timeUnit;

  @Value("${near-cache.maxSize:100}")
  private Long maxSize;

  @Value("${remote-cache.ttlInHours:4}")
  private Long ttlInHours;

  @Value("${remote-cache.cachename:bag_list_associated_with_last_four_digits}")
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
