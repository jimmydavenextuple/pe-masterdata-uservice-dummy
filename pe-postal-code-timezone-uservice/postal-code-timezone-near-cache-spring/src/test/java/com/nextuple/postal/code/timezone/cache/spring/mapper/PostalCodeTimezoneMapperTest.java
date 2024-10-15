/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.cache.spring.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheValue;
import com.nextuple.postal.code.timezone.cache.spring.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostalCodeTimezoneMapperTest {
  @InjectMocks
  private GenericMapper<
          PostalCodeTimezoneCacheKey,
          PostalCodeTimezoneCacheValue,
          String,
          BaseResponse<PostalCodeTimezoneDto>>
      genericMapper = new PostalCodeTimezoneMapper();

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(genericMapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(genericMapper.cacheKeyToRequest(testUtil.getPostalCodeTimezoneCacheKey()));
  }

  @Test
  void responseToCacheValue() {
    PostalCodeTimezoneCacheValue cacheValue = testUtil.getPostalCodeTimezoneCacheValue();
    BaseResponse<PostalCodeTimezoneDto> response =
        testUtil.getBaseResponseOfPostalCodeTimezoneDto();

    assertEquals(
        cacheValue.getPostalCodeTimezoneDto().getOrgId(),
        genericMapper.responseToCacheValue(response).getPostalCodeTimezoneDto().getOrgId());
    assertEquals(
        cacheValue.getPostalCodeTimezoneDto().getZipCodePrefix(),
        genericMapper.responseToCacheValue(response).getPostalCodeTimezoneDto().getZipCodePrefix());
  }

  @Test
  void responseToCacheValueNullTest() {
    PostalCodeTimezoneCacheValue cacheValue =
        PostalCodeTimezoneCacheValue.builder().postalCodeTimezoneDto(null).build();

    BaseResponse<PostalCodeTimezoneDto> response = new BaseResponse<>();
    response.setPayload(null);

    assertEquals(cacheValue, genericMapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    PostalCodeTimezoneCacheValue cacheValue = testUtil.getPostalCodeTimezoneCacheValue();
    assertNull(genericMapper.cacheValueToResponse(cacheValue));
  }
}
