package com.nextuple.postal.code.timezone.cache.spring.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheValue;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneDto;
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
          postalCodeTimezoneFeign.getPostalCodeTimezone(key.getOrgId(), key.getPostalCodePrefix()));
    } catch (RuntimeException ex) {
      return null;
    }
  }
}
