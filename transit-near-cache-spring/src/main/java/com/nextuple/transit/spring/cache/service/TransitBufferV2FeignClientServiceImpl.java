/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.transit.cache.domain.TransitBufferV2CacheKey;
import com.nextuple.transit.cache.domain.TransitBufferV2CacheValue;
import com.nextuple.transit.domain.outbound.TransitBufferDetailsResponse;
import com.nextuple.transit.spring.cache.feign.TransitBufferV2FeignImpl;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// Added this
@AllArgsConstructor
public class TransitBufferV2FeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        TransitBufferV2CacheKey,
        TransitBufferV2CacheValue,
        String,
        BaseResponse<List<TransitBufferDetailsResponse>>> {
  @Autowired TransitBufferV2FeignImpl transitBufferV2Feign;

  @Autowired
  GenericMapper<
          TransitBufferV2CacheKey,
          TransitBufferV2CacheValue,
          String,
          BaseResponse<List<TransitBufferDetailsResponse>>>
      transitBufferV2Mapper;

  @Override
  public TransitBufferV2CacheValue get(TransitBufferV2CacheKey key) {
    try {
      BaseResponse<List<TransitBufferDetailsResponse>> response =
          transitBufferV2Feign.getTransitBuffersByOrgIdAndDestinationGeozone(
              key.getOrgId(),
              key.getDestinationGeozone(),
              key.getRequestDate(),
              key.getHorizonDays());
      if (Objects.isNull(response.getPayload()) || response.getPayload().isEmpty()) {
        return TransitBufferV2CacheValue.builder().build();
      }
      return transitBufferV2Mapper.responseToCacheValue(response);
    } catch (RuntimeException ex) {
      return TransitBufferV2CacheValue.builder().build();
    }
  }
}
