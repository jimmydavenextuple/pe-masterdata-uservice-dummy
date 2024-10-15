/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.cache.spring.mapper;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheValue;
import org.springframework.stereotype.Component;

@Component
public class PostalCodeTimezoneMapper
    implements GenericMapper<
        PostalCodeTimezoneCacheKey,
        PostalCodeTimezoneCacheValue,
        String,
        BaseResponse<PostalCodeTimezoneDto>> {
  @Override
  public PostalCodeTimezoneCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(PostalCodeTimezoneCacheKey cacheKey) {
    return null;
  }

  @Override
  public PostalCodeTimezoneCacheValue responseToCacheValue(
      BaseResponse<PostalCodeTimezoneDto> response) {
    return PostalCodeTimezoneCacheValue.builder()
        .postalCodeTimezoneDto(response.getPayload())
        .build();
  }

  @Override
  public BaseResponse<PostalCodeTimezoneDto> cacheValueToResponse(
      PostalCodeTimezoneCacheValue cacheValue) {
    return null;
  }
}
