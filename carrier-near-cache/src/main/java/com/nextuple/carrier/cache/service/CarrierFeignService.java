package com.nextuple.carrier.cache.service;

import com.nextuple.carrier.cache.domain.CarrierServiceResponse;
import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;

public interface CarrierFeignService
    extends GenericFeignService<String, BaseResponse<CarrierServiceResponse>> {}
