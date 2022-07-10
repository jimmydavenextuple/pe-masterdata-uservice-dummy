package com.hbc.core.spring.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@ConditionalOnProperty(value = "near-cache.enabled", havingValue = "true")
public class MultipleCacheManagerConfig {

  @Value("${near-cache.duration:24}")
  private Long duration;

  @Value("${near-cache.timeUnit:HOURS}")
  private TimeUnit timeUnit;

  @Value("${near-cache.maxSize:5000}")
  private Long maxSize;

  @Bean
  public Caffeine caffeineConfig() { // NOSONAR
    return Caffeine.newBuilder().maximumSize(maxSize).expireAfterWrite(duration, timeUnit);
  }

  @Bean
  public CacheManager caffeineCacheManager(Caffeine caffeine) { // NOSONAR
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCaffeine(caffeine);
    return caffeineCacheManager;
  }
}
