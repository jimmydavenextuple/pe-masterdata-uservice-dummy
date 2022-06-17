package com.nextuple.node.calendar.cache.spring.service;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheValue;
import com.nextuple.node.calendar.cache.service.NodeCalendarNearCacheService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = NodeCalendarSpringNearCacheService.NODE_CALENDAR_CACHE_NAME)
public class NodeCalendarSpringNearCacheService
    extends AbstractGenericSpringLocalCacheServiceImpl<NodeCalendarCacheKey, NodeCalendarCacheValue>
    implements NodeCalendarNearCacheService {

  public static final String NODE_CALENDAR_CACHE_NAME = "node_calendar";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.NODE_CALENDAR_ENTITY_NAME, NodeCalendarCacheKey.class.getName());
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.NODE_CALENDAR_ENTITY_NAME;
  }

  @Override
  public NodeCalendarCacheValue get(NodeCalendarCacheKey key) {
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(NodeCalendarCacheKey key) {
    super.delete(key);
  }
}
