/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.calendar.cache.spring.service;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheValue;
import com.nextuple.node.calendar.cache.service.NodeCalendarNearCacheService;
import jakarta.annotation.PostConstruct;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = NodeCalendarSpringNearCacheService.NODE_CALENDAR_CACHE_NAME)
@RequiredArgsConstructor
public class NodeCalendarSpringNearCacheService
    extends AbstractGenericSpringLocalCacheServiceImpl<NodeCalendarCacheKey, NodeCalendarCacheValue>
    implements NodeCalendarNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(NodeCalendarSpringNearCacheService.class);
  public static final String NODE_CALENDAR_CACHE_NAME = "node_calendar";

  private final NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.NODE_CALENDAR_ENTITY_NAME,
        NodeCalendarCacheKey.class.getName(),
        "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.NODE_CALENDAR_ENTITY_NAME;
  }

  @Override
  public String getCacheName() {
    return NodeCalendarSpringNearCacheService.NODE_CALENDAR_CACHE_NAME;
  }

  @Override
  public NodeCalendarCacheValue get(NodeCalendarCacheKey key) {
    logger.debug("Inside get NodeCalendarCacheValue");
    return super.get(key);
  }

  @Override
  public void delete(NodeCalendarCacheKey key) {
    super.delete(key);
    CaffeineCacheManager caffeineCacheManager = getCacheManager();
    if (Objects.isNull(caffeineCacheManager)) return;
    Cache cache = caffeineCacheManager.getCache(getCacheName());
    if (Objects.isNull(cache)) return;
    com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
        (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();
    String keyString = key.toString();
    for (Object k : nativeCache.asMap().keySet()) {
      String cacheKey = k.toString();
      if (cacheKey.startsWith(keyString)) cache.evict(cacheKey);
    }
  }
}
