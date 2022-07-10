package com.hbc.weightage.configuration.spring.cache.service;

import static com.hbc.weightage.configuration.spring.cache.service.WeightageConfigurationSpringNearCacheServiceImpl.WEIGHTAGE_CONFIGURATION;

import com.hbc.core.constants.NearCacheConstants;
import com.hbc.core.registry.NearCacheRegistry;
import com.hbc.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.hbc.weightage.configuration.cache.domain.WeightageConfigurationCacheKey;
import com.hbc.weightage.configuration.cache.domain.WeightageConfigurationCacheValue;
import com.hbc.weightage.configuration.cache.service.WeightageConfigurationNearCacheService;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = WEIGHTAGE_CONFIGURATION)
public class WeightageConfigurationSpringNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<
        WeightageConfigurationCacheKey, WeightageConfigurationCacheValue>
    implements WeightageConfigurationNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(WeightageConfigurationSpringNearCacheServiceImpl.class);

  public static final String WEIGHTAGE_CONFIGURATION = "weightage configuration";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.WEIGHTAGE_CONFIGURATION_ENTITY_NAME,
        WeightageConfigurationCacheKey.class.getName(),
        "full");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.WEIGHTAGE_CONFIGURATION_ENTITY_NAME;
  }

  @Override
  public WeightageConfigurationCacheValue get(WeightageConfigurationCacheKey key) {
    logger.info("Inside get WeightageConfigurationCacheValue");
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(WeightageConfigurationCacheKey key) {
    super.delete(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", allEntries = true)
  @Override
  public void deleteAll() {
    super.deleteAll();
  }
}
