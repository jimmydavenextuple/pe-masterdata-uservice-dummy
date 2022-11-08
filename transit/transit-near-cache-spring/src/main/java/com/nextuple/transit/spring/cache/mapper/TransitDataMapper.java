package com.nextuple.transit.spring.cache.mapper;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.transit.cache.domain.TransitCacheKey;
import com.nextuple.transit.cache.domain.TransitCacheValue;
import com.nextuple.transit.domain.outbound.TransitResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TransitDataMapper
    implements GenericMapper<
        TransitCacheKey, TransitCacheValue, String, BaseResponse<List<TransitResponse>>> {
  @Override
  public TransitCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(TransitCacheKey cacheKey) {
    return null;
  }

  @Override
  public TransitCacheValue responseToCacheValue(BaseResponse<List<TransitResponse>> resp) {
    return TransitCacheValue.builder().transitResponseList(resp.getPayload()).build();
  }

  @Override
  public BaseResponse<List<TransitResponse>> cacheValueToResponse(TransitCacheValue cacheValue) {
    return null;
  }
}
