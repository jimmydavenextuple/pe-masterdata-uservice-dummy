/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.spring.cache.service;

import com.nextuple.carrier.cache.domain.CarrierCacheKey;
import com.nextuple.carrier.cache.domain.CarrierCacheValue;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.carrier.spring.cache.feign.CarrierFeignImpl;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarrierFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        CarrierCacheKey, CarrierCacheValue, String, BaseResponse<CarrierServiceResponse>> {

  private final CarrierFeignImpl carrierFeign;

  private final GenericMapper<
          CarrierCacheKey, CarrierCacheValue, String, BaseResponse<CarrierServiceResponse>>
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
