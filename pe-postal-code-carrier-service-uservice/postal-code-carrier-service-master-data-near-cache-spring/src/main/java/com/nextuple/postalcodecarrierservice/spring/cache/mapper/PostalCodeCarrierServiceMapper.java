package com.nextuple.postalcodecarrierservice.spring.cache.mapper;

import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.postalcodecarrierservice.data.cache.domain.PostalCodeCarrierServiceDataCacheKey;
import com.nextuple.postalcodecarrierservice.data.cache.domain.PostalCodeCarrierServiceDataCacheValue;
import com.nextuple.postalcodecarrierservice.domain.outbound.PostalCodeCarrierServiceResponse;
import org.springframework.stereotype.Component;

@Component
public class PostalCodeCarrierServiceMapper
    implements GenericMapper<
        PostalCodeCarrierServiceDataCacheKey,
        PostalCodeCarrierServiceDataCacheValue,
        String,
        PostalCodeCarrierServiceResponse> {

  @Override
  public PostalCodeCarrierServiceDataCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(PostalCodeCarrierServiceDataCacheKey cacheKey) {
    return null;
  }

  @Override
  public PostalCodeCarrierServiceDataCacheValue responseToCacheValue(
      PostalCodeCarrierServiceResponse resp) {
    return PostalCodeCarrierServiceDataCacheValue.builder()
        .postalCodeCarrierServiceResponse(resp)
        .build();
  }

  @Override
  public PostalCodeCarrierServiceResponse cacheValueToResponse(
      PostalCodeCarrierServiceDataCacheValue cacheValue) {
    return null;
  }
}
