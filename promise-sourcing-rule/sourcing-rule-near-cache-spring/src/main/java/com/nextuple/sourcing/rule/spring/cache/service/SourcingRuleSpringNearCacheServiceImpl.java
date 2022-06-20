package com.nextuple.sourcing.rule.spring.cache.service;

import static com.nextuple.sourcing.rule.spring.cache.service.SourcingRuleSpringNearCacheServiceImpl.SOURCING_RULE_CACHE_NAME;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheValue;
import com.nextuple.sourcing.rule.cache.service.SourcingRuleNearCacheService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = SOURCING_RULE_CACHE_NAME)
public class SourcingRuleSpringNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<SourcingRuleCacheKey, SourcingRuleCacheValue>
    implements SourcingRuleNearCacheService {

  public static final String SOURCING_RULE_CACHE_NAME = "sourcingRule";

  @Autowired NearCacheRegistry registry;

  @PostConstruct
  @Override
  public void selfRegister() {
    registry.registerNearCacheEntity(
        NearCacheConstants.PROMISE_SOURCING_ENTITY_NAME, SourcingRuleCacheKey.class.getName());
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.PROMISE_SOURCING_ENTITY_NAME;
  }

  @Override
  public SourcingRuleCacheValue get(SourcingRuleCacheKey key) {
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(SourcingRuleCacheKey key) {
    super.delete(key);
  }
}
