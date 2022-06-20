package com.nextuple.weightage.configuration.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import java.util.Map;

public interface WeightageConfigurationFeignService
    extends GenericFeignService<String, BaseResponse<Map<String, Float>>> {}
