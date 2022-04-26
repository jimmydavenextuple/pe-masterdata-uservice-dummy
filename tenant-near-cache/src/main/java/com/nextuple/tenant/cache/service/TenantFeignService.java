package com.nextuple.tenant.cache.service;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.core.tenant.domain.TenantDto;

public interface TenantFeignService extends GenericFeignService<String, BaseResponse<TenantDto>> {}
