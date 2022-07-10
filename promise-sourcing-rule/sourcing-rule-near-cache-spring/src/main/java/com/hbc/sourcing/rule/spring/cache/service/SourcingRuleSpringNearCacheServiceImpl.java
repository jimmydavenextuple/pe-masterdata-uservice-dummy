package com.hbc.sourcing.rule.spring.cache.service;

import static com.hbc.sourcing.rule.spring.cache.service.SourcingRuleSpringNearCacheServiceImpl.SOURCING_RULE_CACHE_NAME;

import com.hbc.core.constants.NearCacheConstants;
import com.hbc.core.registry.NearCacheRegistry;
import com.hbc.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.hbc.sourcing.rule.cache.domain.SourcingRuleCacheKey;
import com.hbc.sourcing.rule.cache.domain.SourcingRuleCacheValue;
import com.hbc.sourcing.rule.cache.service.SourcingRuleNearCacheService;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = SOURCING_RULE_CACHE_NAME)
public class SourcingRuleSpringNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<SourcingRuleCacheKey, SourcingRuleCacheValue>
    implements SourcingRuleNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(SourcingRuleSpringNearCacheServiceImpl.class);

  public static final String SOURCING_RULE_CACHE_NAME = "sourcingRule";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.PROMISE_SOURCING_ENTITY_NAME,
        SourcingRuleCacheKey.class.getName(),
        "full");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.PROMISE_SOURCING_ENTITY_NAME;
  }

  @Override
  public SourcingRuleCacheValue get(SourcingRuleCacheKey key) {
    logger.info("Inside get SourcingRuleCacheValue");
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(SourcingRuleCacheKey key) {
    super.delete(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", allEntries = true)
  @Override
  public void deleteAll() {
    super.deleteAll();
  }
}
