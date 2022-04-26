package com.nextuple.tenant.cache.service;

import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.tenant.cache.domain.TenantCacheKey;
import com.nextuple.tenant.cache.domain.TenantCacheValue;

public interface TenantNearCacheService
    extends GenericNearCacheService<TenantCacheKey, TenantCacheValue> {}
