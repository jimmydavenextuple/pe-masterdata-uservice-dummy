/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.core.spring.impl;

import com.nextuple.core.spring.CacheManagerStrategy;
import com.nextuple.core.spring.config.CacheProperties;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "nearcache.enabled", havingValue = "true")
public class CustomCompositeCacheManager implements CacheManager {
  private final Map<String, CacheManagerStrategy> strategies;
  private final CacheProperties cacheProperties;
  private final boolean isRedisEnabled;
  private final CacheManagerStrategy defaultStrategy;

  public CustomCompositeCacheManager(
      Optional<RedisCacheManagerStrategy> redisCacheStrategy,
      CaffeineCacheManagerStrategy caffeineCacheStrategy,
      CacheProperties cacheProperties,
      @Value("${redis-enabled:false}") boolean isRedisEnabled) {
    this.strategies = new HashMap<>();
    this.cacheProperties = cacheProperties;
    this.isRedisEnabled = isRedisEnabled;
    this.defaultStrategy = caffeineCacheStrategy;

    strategies.put("caffeine", caffeineCacheStrategy);
    redisCacheStrategy.ifPresent(strategy -> strategies.put("redis", strategy));
  }

  @Override
  public Cache getCache(String name) {
    String cacheManagerType = cacheProperties.getCacheManagerMap().getOrDefault(name, "caffeine");

    if (isRedisEnabled && "redis".equalsIgnoreCase(cacheManagerType)) {
      return strategies.get("redis").getCache(name);
    }
    return defaultStrategy.getCache(name);
  }

  @Override
  public Collection<String> getCacheNames() {
    return cacheProperties.getCachemap().keySet();
  }
}
