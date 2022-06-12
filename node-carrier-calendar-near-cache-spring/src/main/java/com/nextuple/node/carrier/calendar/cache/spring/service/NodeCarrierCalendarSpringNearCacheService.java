package com.nextuple.node.carrier.calendar.cache.spring.service;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheKey;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheValue;
import com.nextuple.node.carrier.calendar.cache.service.NodeCarrierCalendarNearCacheService;
import javax.annotation.PostConstruct;
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

  public static final String NODE_CARRIER_CALENDAR_CACHE_NAME = "node_carrier_calendar";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.NODE_CARRIER_CALENDAR_ENTITY_NAME,
        NodeCarrierCalendarCacheKey.class.getName());
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.NODE_CARRIER_CALENDAR_ENTITY_NAME;
  }

  @Override
  public NodeCarrierCalendarCacheValue get(NodeCarrierCalendarCacheKey key) {
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(NodeCarrierCalendarCacheKey key) {
    super.delete(key);
  }
}
