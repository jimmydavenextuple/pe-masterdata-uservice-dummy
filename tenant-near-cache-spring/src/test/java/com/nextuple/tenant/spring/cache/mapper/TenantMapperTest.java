package com.nextuple.tenant.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.tenant.domain.TenantDto;
import com.nextuple.tenant.cache.domain.TenantCacheKey;
import com.nextuple.tenant.cache.domain.TenantCacheValue;
import com.nextuple.tenant.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantMapperTest {

  @InjectMocks
  private GenericMapper<TenantCacheKey, TenantCacheValue, String, BaseResponse<TenantDto>>
      genericMapper = new TenantMapper();

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(genericMapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    TenantCacheKey cacheKey = testUtil.getTenantCacheKey("5dc9ae77718224000c992f92");
    assertEquals(cacheKey.getTenantObjectId(), genericMapper.cacheKeyToRequest(cacheKey));
  }

  @Test
  void responseToCacheValue() {
    TenantCacheValue cacheValue = testUtil.getTenantCacheValue("NEXTUPLE_GR", "Nextuple");
    BaseResponse<TenantDto> response =
        testUtil.getBaseResponseOfTenantDto("NEXTUPLE_GR", "Nextuple");

    assertEquals(cacheValue, genericMapper.responseToCacheValue(response));
  }

  @Test
  void responseToCacheValueNullTest() {
    TenantCacheValue cacheValue = TenantCacheValue.builder().tenantDetails(null).build();

    BaseResponse<TenantDto> response = new BaseResponse<>();
    response.setMessage("Tenant details fetched");
    response.setPayload(null);

    assertEquals(cacheValue, genericMapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    TenantCacheValue cacheValue = testUtil.getTenantCacheValue("NEXTUPLE_GR", "Nextuple");
    assertNull(genericMapper.cacheValueToResponse(cacheValue));
  }
}
