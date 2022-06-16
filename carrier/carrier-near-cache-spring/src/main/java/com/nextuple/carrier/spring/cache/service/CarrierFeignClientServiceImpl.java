package com.nextuple.carrier.spring.cache.service;

import com.nextuple.carrier.cache.domain.CarrierCacheKey;
import com.nextuple.carrier.cache.domain.CarrierCacheValue;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.carrier.spring.cache.feign.CarrierFeignImpl;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarrierFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        CarrierCacheKey, CarrierCacheValue, String, BaseResponse<CarrierServiceResponse>> {

  @Autowired CarrierFeignImpl carrierFeign;

  @Autowired
  GenericMapper<CarrierCacheKey, CarrierCacheValue, String, BaseResponse<CarrierServiceResponse>>
      carrierMapper;

  @Override
  public CarrierCacheValue get(CarrierCacheKey key) {
    try {
      return carrierMapper.responseToCacheValue(
          carrierFeign.getCarrier(key.getCarrierId(), key.getServiceId(), key.getOrgId()));
    } catch (RuntimeException e) {
      return null;
    }
  }
}
