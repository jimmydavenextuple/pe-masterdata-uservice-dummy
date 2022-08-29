package com.hbc.common.configuration.cache.service;

import com.hbc.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;

public interface CommonConfigFeignService
    extends GenericFeignService<String, BaseResponse<CommonConfigurationDto>> {}
