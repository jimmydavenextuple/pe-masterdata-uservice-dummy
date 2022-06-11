package com.nextuple.tenant.cache.domain;

import com.nextuple.core.cache.domain.CacheKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TenantCacheKey implements CacheKey {
  String tenantObjectId;
  public TenantCacheKey() {}
}
