package com.hbc.postal.code.timezone.cache.spring.util;

import com.hbc.common.response.BaseResponse;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheKey;
import com.hbc.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheValue;

public class TestUtil {
  public PostalCodeTimezoneDto getPostalCodeTimezoneDto() {
    return PostalCodeTimezoneDto.builder()
        .orgId("ABC")
        .postalCodePrefix("123")
        .country("COUNTRY")
        .state("STATE")
        .city("CITY")
        .latitude("LATITUDE")
        .longitude("LONGITUDE")
        .timeZone("TIME_ZONE")
        .build();
  }

  public PostalCodeTimezoneCacheKey getPostalCodeTimezoneCacheKey() {
    return PostalCodeTimezoneCacheKey.builder().orgId("ABC").postalCodePrefix("123").build();
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
}
