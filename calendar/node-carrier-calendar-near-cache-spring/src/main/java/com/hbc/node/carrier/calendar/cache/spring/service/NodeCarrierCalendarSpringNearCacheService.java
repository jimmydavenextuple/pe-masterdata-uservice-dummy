package com.hbc.node.carrier.calendar.cache.spring.service;

import com.hbc.core.constants.NearCacheConstants;
import com.hbc.core.registry.NearCacheRegistry;
import com.hbc.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheKey;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheValue;
import com.hbc.node.carrier.calendar.cache.service.NodeCarrierCalendarNearCacheService;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(
    cacheNames = NodeCarrierCalendarSpringNearCacheService.NODE_CARRIER_CALENDAR_CACHE_NAME)
public class NodeCarrierCalendarSpringNearCacheService
    extends AbstractGenericSpringLocalCacheServiceImpl<
        NodeCarrierCalendarCacheKey, NodeCarrierCalendarCacheValue>
    implements NodeCarrierCalendarNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(NodeCarrierCalendarSpringNearCacheService.class);
  public static final String NODE_CARRIER_CALENDAR_CACHE_NAME = "node_carrier_calendar";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.NODE_CARRIER_CALENDAR_ENTITY_NAME,
        NodeCarrierCalendarCacheKey.class.getName(),
        "full");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.NODE_CARRIER_CALENDAR_ENTITY_NAME;
  }

  @Override
  public NodeCarrierCalendarCacheValue get(NodeCarrierCalendarCacheKey key) {
    logger.debug("Inside get NodeCarrierCalendarCacheValue");
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(NodeCarrierCalendarCacheKey key) {
    logger.debug("Inside delete method of nodeCarrierCalendarCache");
    super.delete(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", allEntries = true)
  @Override
  public void deleteAll() {
    logger.debug("Inside deleteAll method of nodeCarrierCalendarCache");
    super.deleteAll();
  }
}
