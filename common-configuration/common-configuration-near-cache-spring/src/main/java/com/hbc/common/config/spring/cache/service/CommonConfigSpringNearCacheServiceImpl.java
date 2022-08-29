package com.hbc.common.config.spring.cache.service;

import com.hbc.common.configuration.cache.domain.CommonConfigCacheKey;
import com.hbc.common.configuration.cache.domain.CommonConfigCacheValue;
import com.hbc.common.configuration.cache.service.CommonConfigNearCacheService;
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
@CacheConfig(cacheNames = CommonConfigSpringNearCacheServiceImpl.COMMON_CONFIG_CACHE_NAME)
public class CommonConfigSpringNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<CommonConfigCacheKey, CommonConfigCacheValue>
    implements CommonConfigNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(CommonConfigSpringNearCacheServiceImpl.class);
  public static final String COMMON_CONFIG_CACHE_NAME = "common_configuration";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.COMMON_CONFIG_ENTITY_NAME, CommonConfigCacheKey.class.getName(), "full");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.COMMON_CONFIG_ENTITY_NAME;
  }

  @Override
  public CommonConfigCacheValue get(CommonConfigCacheKey key) {
    logger.debug("Inside get CommonConfigCacheValue");
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(CommonConfigCacheKey key) {
    logger.debug("Inside delete method of commonConfigurationCache");
    super.delete(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", allEntries = true)
  @Override
  public void deleteAll() {
    logger.debug("Inside deleteAll method of commonConfigurationCache");
    super.deleteAll();
  }
}
