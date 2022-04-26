package com.nextuple.tenant.spring.cache.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.tenant.domain.TenantDto;
import com.nextuple.tenant.cache.domain.TenantCacheKey;
import com.nextuple.tenant.cache.domain.TenantCacheValue;
import com.nextuple.tenant.spring.cache.feign.service.TenantFeignImpl;
import com.nextuple.tenant.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantFeignClientServiceImplTest {

  @InjectMocks private TenantFeignClientServiceImpl tenantFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<TenantCacheKey, TenantCacheValue, String, BaseResponse<TenantDto>> mapper;

  @Mock private TenantFeignImpl tenantFeign;

  @Test
  void get() {

    TenantCacheKey cacheKey = testUtil.getTenantCacheKey("5dc9ae77718224000c992f92");
    TenantCacheValue cacheValue = testUtil.getTenantCacheValue("NEXTUPLE_GR", "Nextuple");
    BaseResponse<TenantDto> response =
        testUtil.getBaseResponseOfTenantDto("NEXTUPLE_GR", "Nextuple");

    Mockito.when(mapper.cacheKeyToRequest(cacheKey)).thenReturn(cacheKey.getTenantObjectId());
    Mockito.when(tenantFeign.getTenantById(cacheKey.getTenantObjectId())).thenReturn(response);
    Mockito.when(mapper.responseToCacheValue(response)).thenReturn(cacheValue);

    assertEquals(cacheValue, tenantFeignClientService.get(cacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {
    TenantCacheKey invalidCacheKey = testUtil.getTenantCacheKey("invalid_tenantObjectId");

    Mockito.when(mapper.responseToCacheValue(any()))
        .thenThrow(new RuntimeException("Error message"));
    assertNull(tenantFeignClientService.get(invalidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
