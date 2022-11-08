package com.nextuple.transit.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.transit.domain.outbound.TransitResponse;
import java.util.List;

public interface TransitDataFeignService
    extends GenericFeignService<String, BaseResponse<List<TransitResponse>>> {}
