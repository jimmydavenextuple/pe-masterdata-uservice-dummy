package com.hbc.carrier.calendar.cache.spring.service;

import com.hbc.carrier.calendar.cache.domain.CarrierServiceCalendarCacheKey;
import com.hbc.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.hbc.carrier.calendar.cache.service.CarrierServiceCalendarNearCacheService;
import com.hbc.core.constants.NearCacheConstants;
import com.hbc.core.registry.NearCacheRegistry;
import com.hbc.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = CarrierServiceCalendarSpringNearCacheService.CARRIER_CALENDAR_CACHE_NAME)
public class CarrierServiceCalendarSpringNearCacheService
    extends AbstractGenericSpringLocalCacheServiceImpl<
        CarrierServiceCalendarCacheKey, CarrierServiceCalendarCacheValue>
    implements CarrierServiceCalendarNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(CarrierServiceCalendarSpringNearCacheService.class);

  public static final String CARRIER_CALENDAR_CACHE_NAME = "carrier_calendar";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.CARRIER_CALENDAR_ENTITY_NAME,
        CarrierServiceCalendarCacheKey.class.getName(),
        "full");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.CARRIER_CALENDAR_ENTITY_NAME;
  }

  @Override
  public CarrierServiceCalendarCacheValue get(CarrierServiceCalendarCacheKey key) {
    logger.debug("Inside get CarrierServiceCalendarCacheValue");
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(CarrierServiceCalendarCacheKey key) {
    logger.debug("Inside delete method of carrierCalendarCache");
    super.delete(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", allEntries = true)
  @Override
  public void deleteAll() {
    logger.debug("Inside deleteAll method of carrierCalendarCache");
    super.deleteAll();
  }
}
