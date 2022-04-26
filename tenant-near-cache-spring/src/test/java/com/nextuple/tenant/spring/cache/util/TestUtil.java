package com.nextuple.tenant.spring.cache.util;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.tenant.domain.TenantDto;
import com.nextuple.core.tenant.enums.TenantStatusEnum;
import com.nextuple.tenant.cache.domain.TenantCacheKey;
import com.nextuple.tenant.cache.domain.TenantCacheValue;
import com.nextuple.tenant.cache.domain.TenantDetails;

public class TestUtil {

  public TenantCacheKey getTenantCacheKey(String tenantObjectId) {
    TenantCacheKey tenantCacheKey = TenantCacheKey.builder().build();
    tenantCacheKey.setTenantObjectId(tenantObjectId);
    return tenantCacheKey;
  }

  private TenantDetails getTenantDetails(String tenantId, String tenantName) {
    TenantDetails tenantDetails = TenantDetails.builder().build();
    tenantDetails.setTenantId(tenantId);
    tenantDetails.setTenantName(tenantName);
    tenantDetails.setStatus(TenantStatusEnum.ACTIVATED);
    return tenantDetails;
  }

  public TenantCacheValue getTenantCacheValue(String tenantId, String tenantName) {
    TenantCacheValue tenantCacheValue = TenantCacheValue.builder().build();
    tenantCacheValue.setTenantDetails(getTenantDetails(tenantId, tenantName));
    return tenantCacheValue;
  }

  private TenantDto getTenantDto(String tenantId, String tenantName) {
    TenantDto tenantDto = new TenantDto();
    tenantDto.setTenantId(tenantId);
    tenantDto.setTenantName(tenantName);
    tenantDto.setStatus("ACTIVATED");
    return tenantDto;
  }

  public BaseResponse<TenantDto> getBaseResponseOfTenantDto(String tenantId, String tenantName) {
    BaseResponse<TenantDto> response = new BaseResponse<>();
    response.setMessage("Tenant details fetched successfully");
    response.setPayload(getTenantDto(tenantId, tenantName));
    return response;
  }
}
