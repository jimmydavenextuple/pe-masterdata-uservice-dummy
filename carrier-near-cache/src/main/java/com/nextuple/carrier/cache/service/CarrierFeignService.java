package com.nextuple.carrier.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.domain.carrier.CarrierServiceResponse;

public interface CarrierFeignService
    extends GenericFeignService<String, BaseResponse<CarrierServiceResponse>> {}
