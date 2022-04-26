package com.nextuple.tenant.spring.cache.service;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.core.tenant.domain.TenantDto;
import com.nextuple.tenant.cache.domain.TenantCacheKey;
import com.nextuple.tenant.cache.domain.TenantCacheValue;
import com.nextuple.tenant.spring.cache.feign.service.TenantFeignImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        TenantCacheKey, TenantCacheValue, String, BaseResponse<TenantDto>> {

  @Autowired TenantFeignImpl tenantFeign;

  @Autowired
  GenericMapper<TenantCacheKey, TenantCacheValue, String, BaseResponse<TenantDto>> tenantMapper;

  @Override
  public TenantCacheValue get(TenantCacheKey key) {
    try {
      return tenantMapper.responseToCacheValue(
          tenantFeign.getTenantById(tenantMapper.cacheKeyToRequest(key)));
    } catch (RuntimeException ex) {
      return null;
    }
  }
}
