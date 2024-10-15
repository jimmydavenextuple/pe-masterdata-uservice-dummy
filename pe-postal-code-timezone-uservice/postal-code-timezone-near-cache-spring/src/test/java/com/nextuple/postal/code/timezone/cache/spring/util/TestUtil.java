/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.cache.spring.util;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.postal.code.timezone.cache.domain.CustomRegionCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.CustomRegionCacheValue;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeCacheValue;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheValue;
import java.util.Arrays;

public class TestUtil {
  public PostalCodeTimezoneDto getPostalCodeTimezoneDto() {
    return PostalCodeTimezoneDto.builder()
        .orgId("ABC")
        .zipCodePrefix("123")
        .country("COUNTRY")
        .state("STATE")
        .city("CITY")
        .latitude("LATITUDE")
        .longitude("LONGITUDE")
        .timeZone("TIME_ZONE")
        .build();
  }

  public PostalCodeTimezoneCacheKey getPostalCodeTimezoneCacheKey() {
    return PostalCodeTimezoneCacheKey.builder().orgId("ABC").zipCodePrefix("123").build();
  }

  public PostalCodeTimezoneCacheValue getPostalCodeTimezoneCacheValue() {
    return PostalCodeTimezoneCacheValue.builder()
        .postalCodeTimezoneDto(getPostalCodeTimezoneDto())
        .build();
  }

  public BaseResponse<PostalCodeTimezoneDto> getBaseResponseOfPostalCodeTimezoneDto() {
    BaseResponse<PostalCodeTimezoneDto> response = new BaseResponse<>();
    response.setPayload(getPostalCodeTimezoneDto());
    return response;
  }

  public PostalCodeCacheValue getPostalCodeCacheValue() {
    return PostalCodeCacheValue.builder().postalCodeResponse(getPostalCodeResponse()).build();
  }

  private PostalCodeResponse getPostalCodeResponse() {
    return PostalCodeResponse.builder()
        .orgId("NXT")
        .zipCodePrefix("123")
        .country("COUNTRY")
        .state("STATE")
        .city("CITY")
        .timeZone("TIME_ZONE")
        .customRegion("REG1")
        .build();
  }

  public BaseResponse<PostalCodeResponse> getPostalCodeBaseResponse() {
    BaseResponse<PostalCodeResponse> response = new BaseResponse<>();
    response.setPayload(getPostalCodeResponse());
    return response;
  }

  public CustomRegionCacheValue getCustomRegionCacheValue() {
    return CustomRegionCacheValue.builder().customRegionResponse(getCustomRegionResponse()).build();
  }

  private CustomRegionResponse getCustomRegionResponse() {
    return CustomRegionResponse.builder()
        .id("id1")
        .orgId("NXT")
        .codes(Arrays.asList("123", "456"))
        .customRegionName("name")
        .customRegionDescription("description")
        .build();
  }

  public BaseResponse<CustomRegionResponse> getCustomRegionBaseResponse() {
    BaseResponse<CustomRegionResponse> response = new BaseResponse<>();
    response.setPayload(getCustomRegionResponse());
    return response;
  }

  public CustomRegionCacheKey getCustomRegionCacheKey() {
    return CustomRegionCacheKey.builder().orgId("NXT").zipCode("123").build();
  }

  public PostalCodeCacheKey getPostalCodeCacheKey() {
    return PostalCodeCacheKey.builder().orgId("NXT").zipCode("123").build();
  }
}
