package com.hbc.carrier.cache.service;

import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;

public interface CarrierFeignService
    extends GenericFeignService<String, BaseResponse<CarrierServiceResponse>> {}
