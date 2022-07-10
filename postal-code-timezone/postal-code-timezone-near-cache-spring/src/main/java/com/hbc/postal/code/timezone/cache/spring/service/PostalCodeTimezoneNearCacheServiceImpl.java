package com.hbc.postal.code.timezone.cache.spring.service;

import static com.hbc.postal.code.timezone.cache.spring.service.PostalCodeTimezoneNearCacheServiceImpl.POSTAL_CODE_TIMEZONE_CACHE_NAME;

import com.hbc.core.constants.NearCacheConstants;
import com.hbc.core.registry.NearCacheRegistry;
import com.hbc.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.hbc.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheKey;
import com.hbc.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheValue;
import com.hbc.postal.code.timezone.cache.service.PostalCodeTimezoneNearCacheService;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = POSTAL_CODE_TIMEZONE_CACHE_NAME)
public class PostalCodeTimezoneNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<
        PostalCodeTimezoneCacheKey, PostalCodeTimezoneCacheValue>
    implements PostalCodeTimezoneNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(PostalCodeTimezoneNearCacheServiceImpl.class);

  public static final String POSTAL_CODE_TIMEZONE_CACHE_NAME = "postal code timezone";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.POSTAL_CODE_ENTITY_NAME,
        PostalCodeTimezoneCacheKey.class.getName(),
        "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.POSTAL_CODE_ENTITY_NAME;
  }

  @Override
  public PostalCodeTimezoneCacheValue get(PostalCodeTimezoneCacheKey key) {
    logger.info("Inside get PostalCodeTimezoneCacheValue");
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(PostalCodeTimezoneCacheKey key) {
    super.delete(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", allEntries = true)
  @Override
  public void deleteAll() {
    super.deleteAll();
  }
}
