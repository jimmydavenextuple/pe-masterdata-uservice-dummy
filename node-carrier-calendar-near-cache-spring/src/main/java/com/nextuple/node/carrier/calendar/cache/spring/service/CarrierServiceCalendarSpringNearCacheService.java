package com.nextuple.node.carrier.calendar.cache.spring.service;

import com.nextuple.core.NearCacheConstants;
import com.nextuple.core.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.node.calendar.domain.CarrierServiceCalendarCacheKey;
import com.nextuple.node.calendar.domain.CarrierServiceCalendarCacheValue;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = CarrierServiceCalendarSpringNearCacheService.NODE_CALENDAR_CACHE_NAME)
public class CarrierServiceCalendarSpringNearCacheService
    extends AbstractGenericSpringLocalCacheServiceImpl<
    CarrierServiceCalendarCacheKey, CarrierServiceCalendarCacheValue>
    implements CarrierServiceCalendarNearCacheService {

  public static final String NODE_CALENDAR_CACHE_NAME = "node_calendar";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.NODE_ENTITY_NAME, CarrierServiceCalendarCacheKey.class.getName());
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.NODE_ENTITY_NAME;
  }

  @Override
  public CarrierServiceCalendarCacheValue get(CarrierServiceCalendarCacheKey key) {
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(CarrierServiceCalendarCacheKey key) {
    super.delete(key);
  }
}
