/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.transit.cache.domain.ZoneCacheKey;
import com.nextuple.transit.cache.domain.ZoneCacheValue;
import com.nextuple.transit.domain.outbound.ZoneResponse;
import com.nextuple.transit.spring.cache.feign.ZoneFeignImpl;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// Added this
@AllArgsConstructor
public class ZoneFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        ZoneCacheKey, ZoneCacheValue, String, BaseResponse<List<ZoneResponse>>> {

  @Autowired ZoneFeignImpl zoneFeign;

  @Autowired
  GenericMapper<ZoneCacheKey, ZoneCacheValue, String, BaseResponse<List<ZoneResponse>>> zoneMapper;

  @Override
  public ZoneCacheValue get(ZoneCacheKey key) {
    try {
      BaseResponse<List<ZoneResponse>> response =
          zoneFeign.getZoneDetailsList(key.getOrgId(), key.getDestinationGeozone());
      if (Objects.isNull(response.getPayload()) || response.getPayload().isEmpty()) {
        return ZoneCacheValue.builder().build();
      }
      return zoneMapper.responseToCacheValue(response);
    } catch (RuntimeException ex) {
      return ZoneCacheValue.builder().build();
    }
  }
}
