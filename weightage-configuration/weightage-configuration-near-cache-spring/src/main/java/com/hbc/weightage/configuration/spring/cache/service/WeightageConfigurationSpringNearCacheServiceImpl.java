package com.hbc.weightage.configuration.spring.cache.service;

import static com.hbc.weightage.configuration.spring.cache.service.WeightageConfigurationSpringNearCacheServiceImpl.WEIGHTAGE_CONFIGURATION;

import com.hbc.core.constants.NearCacheConstants;
import com.hbc.core.registry.NearCacheRegistry;
import com.hbc.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.hbc.weightage.configuration.cache.domain.WeightageConfigurationCacheKey;
import com.hbc.weightage.configuration.cache.domain.WeightageConfigurationCacheValue;
import com.hbc.weightage.configuration.cache.service.WeightageConfigurationNearCacheService;
import javax.annotation.PostConstruct;
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

  public static final String WEIGHTAGE_CONFIGURATION = "weightage configuration";

  @Autowired NearCacheRegistry registry;

  @PostConstruct
  @Override
  public void selfRegister() {
    registry.registerNearCacheEntity(
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
