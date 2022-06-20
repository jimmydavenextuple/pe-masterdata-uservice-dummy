package com.nextuple.postal.code.timezone.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneDto;

public interface PostalCodeTimezoneFeignService
    extends GenericFeignService<String, BaseResponse<PostalCodeTimezoneDto>> {}
