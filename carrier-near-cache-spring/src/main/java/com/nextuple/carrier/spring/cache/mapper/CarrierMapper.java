package com.nextuple.carrier.spring.cache.mapper;

import com.nextuple.carrier.cache.domain.CarrierCacheKey;
import com.nextuple.carrier.cache.domain.CarrierCacheValue;
import com.nextuple.carrier.cache.domain.CarrierServiceResponse;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
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
