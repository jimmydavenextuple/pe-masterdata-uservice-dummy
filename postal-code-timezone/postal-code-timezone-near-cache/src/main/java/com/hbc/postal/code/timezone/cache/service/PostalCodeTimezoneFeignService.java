package com.hbc.postal.code.timezone.cache.service;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;

public interface PostalCodeTimezoneFeignService
    extends GenericFeignService<String, BaseResponse<PostalCodeTimezoneDto>> {}
