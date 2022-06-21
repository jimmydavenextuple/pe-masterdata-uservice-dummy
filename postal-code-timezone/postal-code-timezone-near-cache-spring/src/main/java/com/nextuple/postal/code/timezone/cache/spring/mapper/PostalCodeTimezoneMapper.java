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
