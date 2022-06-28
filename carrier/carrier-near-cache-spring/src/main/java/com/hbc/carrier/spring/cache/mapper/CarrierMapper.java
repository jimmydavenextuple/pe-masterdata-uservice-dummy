package com.hbc.carrier.spring.cache.mapper;

import com.hbc.carrier.cache.domain.CarrierCacheKey;
import com.hbc.carrier.cache.domain.CarrierCacheValue;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class CarrierMapper
    implements GenericMapper<
        CarrierCacheKey, CarrierCacheValue, String, BaseResponse<CarrierServiceResponse>> {

  public static final DataMapper DATA_MAPPER = Mappers.getMapper(DataMapper.class);

  @Override
  public CarrierCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(CarrierCacheKey cacheKey) {
    return null;
  }

  @Override
  public CarrierCacheValue responseToCacheValue(BaseResponse<CarrierServiceResponse> resp) {
    return CarrierCacheValue.builder()
        .carrierDetails(DATA_MAPPER.convertToCarrierCacheValue(resp.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<CarrierServiceResponse> cacheValueToResponse(CarrierCacheValue cacheValue) {
    return null;
  }
}
