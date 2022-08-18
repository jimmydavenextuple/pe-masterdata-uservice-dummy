package java.com.hbc.global.configuration.cache.service;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;

import java.util.Map;

public interface WeightageConfigurationFeignService
    extends GenericFeignService<String, BaseResponse<Map<String, Float>>> {}
