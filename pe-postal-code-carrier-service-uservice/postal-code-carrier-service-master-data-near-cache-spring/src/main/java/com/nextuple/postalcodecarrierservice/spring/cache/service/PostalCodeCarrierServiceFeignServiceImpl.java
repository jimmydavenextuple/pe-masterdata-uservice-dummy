package com.nextuple.postalcodecarrierservice.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.postalcodecarrierservice.data.cache.domain.PostalCodeCarrierServiceDataCacheKey;
import com.nextuple.postalcodecarrierservice.data.cache.domain.PostalCodeCarrierServiceDataCacheValue;
import com.nextuple.postalcodecarrierservice.domain.outbound.PostalCodeCarrierServiceResponse;
import com.nextuple.postalcodecarrierservice.spring.cache.feign.PostalCodeCarrierServiceFeignImpl;
import com.nextuple.postalcodecarrierservice.spring.cache.mapper.PostalCodeCarrierServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostalCodeCarrierServiceFeignServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        PostalCodeCarrierServiceDataCacheKey,
        PostalCodeCarrierServiceDataCacheValue,
        String,
        BaseResponse<PostalCodeCarrierServiceResponse>> {
  private final PostalCodeCarrierServiceFeignImpl postalCodeCarrierServiceFeign;
  private final PostalCodeCarrierServiceMapper postalCodeCarrierMapper;

  @Override
  public PostalCodeCarrierServiceDataCacheValue get(PostalCodeCarrierServiceDataCacheKey key) {
    try {
      return postalCodeCarrierMapper.responseToCacheValue(
          postalCodeCarrierServiceFeign.getPostalCodeCarrierService(
              key.getZipcode(), key.getCarrierServiceId()));
    } catch (RuntimeException e) {
      return PostalCodeCarrierServiceDataCacheValue.builder().build();
    }
  }
}
