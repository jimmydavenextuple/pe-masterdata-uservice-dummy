/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.cache.spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheValue;
import com.nextuple.postal.code.timezone.cache.spring.feign.PostalCodeTimezoneFeignImpl;
import com.nextuple.postal.code.timezone.cache.spring.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostalCodeTimezoneFeignClientServiceImplTest {

  @InjectMocks
  private PostalCodeTimezoneFeignClientServiceImpl postalCodeTimezoneFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          PostalCodeTimezoneCacheKey,
          PostalCodeTimezoneCacheValue,
          String,
          BaseResponse<PostalCodeTimezoneDto>>
      mapper;

  @Mock private PostalCodeTimezoneFeignImpl postalCodeTimezoneFeign;

  @Test
  void get() {
    PostalCodeTimezoneCacheKey cacheKey = testUtil.getPostalCodeTimezoneCacheKey();
    PostalCodeTimezoneCacheValue cacheValue = testUtil.getPostalCodeTimezoneCacheValue();
    BaseResponse<PostalCodeTimezoneDto> response =
        testUtil.getBaseResponseOfPostalCodeTimezoneDto();

    when(postalCodeTimezoneFeign.getPostalCodeTimezone(
            cacheKey.getOrgId(), cacheKey.getZipCodePrefix()))
        .thenReturn(response);
    when(mapper.responseToCacheValue(response)).thenReturn(cacheValue);

    assertEquals(cacheValue, postalCodeTimezoneFeignClientService.get(cacheKey));
    assertFalse(postalCodeTimezoneFeignClientService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {
    PostalCodeTimezoneCacheKey invalidCacheKey = testUtil.getPostalCodeTimezoneCacheKey();

    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNull(postalCodeTimezoneFeignClientService.get(invalidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
