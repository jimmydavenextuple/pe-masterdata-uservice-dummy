package com.nextuple.tenant.spring.cache.mapper;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.tenant.domain.TenantDto;
import com.nextuple.tenant.cache.domain.TenantCacheKey;
import com.nextuple.tenant.cache.domain.TenantCacheValue;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class TenantMapper
    implements GenericMapper<TenantCacheKey, TenantCacheValue, String, BaseResponse<TenantDto>> {

  public static final DataMapper DATA_MAPPER = Mappers.getMapper(DataMapper.class);

  @Override
  public TenantCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(TenantCacheKey cacheKey) {
    return cacheKey.getTenantObjectId();
  }

  @Override
  public TenantCacheValue responseToCacheValue(BaseResponse<TenantDto> response) {
    return TenantCacheValue.builder()
        .tenantDetails(DATA_MAPPER.toTenantCacheValue(response.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<TenantDto> cacheValueToResponse(TenantCacheValue cacheValue) {
    return null;
  }
}
