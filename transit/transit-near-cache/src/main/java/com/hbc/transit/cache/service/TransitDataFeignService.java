package com.hbc.transit.cache.service;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;
import com.hbc.transit.domain.outbound.TransitResponse;
import java.util.List;

public interface TransitDataFeignService
    extends GenericFeignService<String, BaseResponse<List<TransitResponse>>> {}
