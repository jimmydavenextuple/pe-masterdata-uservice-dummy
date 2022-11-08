package com.nextuple.transit.spring.cache.service;

import static com.nextuple.transit.spring.cache.service.TransitDataNearCacheServiceImpl.TRANSIT_CACHE_NAME;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.transit.cache.domain.TransitCacheKey;
import com.nextuple.transit.cache.domain.TransitCacheValue;
import com.nextuple.transit.cache.service.TransitDataNearCacheService;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = TRANSIT_CACHE_NAME)
public class TransitDataNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<TransitCacheKey, TransitCacheValue>
    implements TransitDataNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(TransitDataNearCacheServiceImpl.class);

  public static final String TRANSIT_CACHE_NAME = "transit";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.TRANSIT_ENTITY_NAME, TransitCacheKey.class.getName(), "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.TRANSIT_ENTITY_NAME;
  }

  @Override
  public TransitCacheValue get(TransitCacheKey key) {
    logger.debug("Inside get TransitCacheValue");
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(TransitCacheKey key) {
    super.delete(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", allEntries = true)
  @Override
  public void deleteAll() {
    super.deleteAll();
  }
}
