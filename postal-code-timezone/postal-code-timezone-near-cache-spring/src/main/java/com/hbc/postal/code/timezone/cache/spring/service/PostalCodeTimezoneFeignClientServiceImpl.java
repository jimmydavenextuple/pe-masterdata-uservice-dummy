package com.hbc.postal.code.timezone.cache.spring.service;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheKey;
import com.hbc.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheValue;
import com.hbc.postal.code.timezone.cache.spring.feign.PostalCodeTimezoneFeignImpl;
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
          postalCodeTimezoneFeign.getPostalCodeTimezone(key.getOrgId(), key.getPostalCodePrefix()));
    } catch (RuntimeException ex) {
      return null;
    }
  }
}
