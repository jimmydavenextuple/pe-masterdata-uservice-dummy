package com.nextuple.carrier.calendar.cache.spring.service;

import com.nextuple.carrier.calendar.cache.domain.CarrierServiceCalendarCacheKey;
import com.nextuple.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.nextuple.carrier.calendar.cache.service.CarrierServiceCalendarNearCacheService;
import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
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
        NearCacheConstants.CARRIER_CALENDAR_ENTITY_NAME,
        CarrierServiceCalendarCacheKey.class.getName(),
        "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.CARRIER_CALENDAR_ENTITY_NAME;
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

  @CacheEvict(cacheManager = "caffeineCacheManager", allEntries = true)
  @Override
  public void deleteAll() {
    super.deleteAll();
  }
}
