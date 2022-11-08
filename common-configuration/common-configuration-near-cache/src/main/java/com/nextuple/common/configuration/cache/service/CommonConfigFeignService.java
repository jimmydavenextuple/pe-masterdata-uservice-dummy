package com.nextuple.common.configuration.cache.service;

import com.nextuple.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;

public interface CommonConfigFeignService
    extends GenericFeignService<String, BaseResponse<CommonConfigurationDto>> {}
