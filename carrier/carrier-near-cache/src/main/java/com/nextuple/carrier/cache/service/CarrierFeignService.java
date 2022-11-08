package com.nextuple.carrier.cache.service;

import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;

public interface CarrierFeignService
    extends GenericFeignService<String, BaseResponse<CarrierServiceResponse>> {}
