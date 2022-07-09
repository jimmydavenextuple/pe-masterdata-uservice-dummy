package com.hbc.carrier.spring.cache.service;

import static com.hbc.carrier.spring.cache.service.CarrierSpringNearCacheServiceImpl.CARRIER_CACHE_NAME;

import com.hbc.carrier.cache.domain.CarrierCacheKey;
import com.hbc.carrier.cache.domain.CarrierCacheValue;
import com.hbc.carrier.cache.service.CarrierNearCacheService;
import com.hbc.core.constants.NearCacheConstants;
import com.hbc.core.registry.NearCacheRegistry;
import com.hbc.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = CARRIER_CACHE_NAME)
public class CarrierSpringNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<CarrierCacheKey, CarrierCacheValue>
    implements CarrierNearCacheService {

  public static final String CARRIER_CACHE_NAME = "carrier";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.CARRIER_ENTITY_NAME, CarrierCacheKey.class.getName(), "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.CARRIER_ENTITY_NAME;
  }

  @Override
  public CarrierCacheValue get(CarrierCacheKey key) {
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(CarrierCacheKey key) {
    super.delete(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", allEntries = true)
  @Override
  public void deleteAll() {
    super.deleteAll();
  }
}
