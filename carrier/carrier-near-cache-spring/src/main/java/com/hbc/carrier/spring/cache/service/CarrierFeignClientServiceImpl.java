package com.hbc.carrier.spring.cache.service;

import com.hbc.carrier.cache.domain.CarrierCacheKey;
import com.hbc.carrier.cache.domain.CarrierCacheValue;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.carrier.spring.cache.feign.CarrierFeignImpl;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
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
          carrierFeign.getCarrier(key.getCarrierId(), key.getCarrierServiceId(), key.getOrgId()));
    } catch (RuntimeException e) {
      return null;
    }
  }
}
