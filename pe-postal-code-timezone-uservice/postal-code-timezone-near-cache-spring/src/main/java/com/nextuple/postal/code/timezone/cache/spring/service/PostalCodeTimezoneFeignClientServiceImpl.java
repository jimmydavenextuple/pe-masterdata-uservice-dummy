/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.cache.spring.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheValue;
import com.nextuple.postal.code.timezone.cache.spring.feign.PostalCodeTimezoneFeignImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostalCodeTimezoneFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        PostalCodeTimezoneCacheKey,
        PostalCodeTimezoneCacheValue,
        String,
        BaseResponse<PostalCodeTimezoneDto>> {
  @Autowired PostalCodeTimezoneFeignImpl postalCodeTimezoneFeign;

  @Autowired
  GenericMapper<
          PostalCodeTimezoneCacheKey,
          PostalCodeTimezoneCacheValue,
          String,
          BaseResponse<PostalCodeTimezoneDto>>
      postalCodeTimezoneMapper;

  @Override
  public PostalCodeTimezoneCacheValue get(PostalCodeTimezoneCacheKey key) {
    try {
      return postalCodeTimezoneMapper.responseToCacheValue(
          postalCodeTimezoneFeign.getPostalCodeTimezone(key.getOrgId(), key.getZipCodePrefix()));
    } catch (RuntimeException ex) {
      return null;
    }
  }
}
