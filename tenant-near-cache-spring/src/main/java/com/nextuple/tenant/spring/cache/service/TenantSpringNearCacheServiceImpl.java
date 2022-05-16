package com.nextuple.tenant.spring.cache.service;

import static com.nextuple.tenant.spring.cache.service.TenantSpringNearCacheServiceImpl.TENANT_CACHE_NAME;

import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.tenant.cache.domain.TenantCacheKey;
import com.nextuple.tenant.cache.domain.TenantCacheValue;
import com.nextuple.tenant.cache.service.TenantNearCacheService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = TENANT_CACHE_NAME)
public class TenantSpringNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<TenantCacheKey, TenantCacheValue>
    implements TenantNearCacheService {

  public static final String TENANT_CACHE_NAME = "tenant";

  @Override
  public TenantCacheValue get(TenantCacheKey key) {
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(TenantCacheKey key) {
    super.delete(key);
  }
}
