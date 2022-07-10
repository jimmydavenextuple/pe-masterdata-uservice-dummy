package com.hbc.node.calendar.cache.spring.service;

import com.hbc.core.constants.NearCacheConstants;
import com.hbc.core.registry.NearCacheRegistry;
import com.hbc.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.hbc.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.hbc.node.calendar.cache.domain.NodeCalendarCacheValue;
import com.hbc.node.calendar.cache.service.NodeCalendarNearCacheService;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = NodeCalendarSpringNearCacheService.NODE_CALENDAR_CACHE_NAME)
public class NodeCalendarSpringNearCacheService
    extends AbstractGenericSpringLocalCacheServiceImpl<NodeCalendarCacheKey, NodeCalendarCacheValue>
    implements NodeCalendarNearCacheService {

  private static final Logger logger = LoggerFactory.getLogger(NodeCalendarSpringNearCacheService.class);
  public static final String NODE_CALENDAR_CACHE_NAME = "node_calendar";

  @Autowired NearCacheRegistry nearCacheRegistry;

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
  public NodeCalendarCacheValue get(NodeCalendarCacheKey key) {
    logger.info("Inside get NodeCalendarCacheValue");
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(NodeCalendarCacheKey key) {
    super.delete(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", allEntries = true)
  @Override
  public void deleteAll() {
    super.deleteAll();
  }
}
