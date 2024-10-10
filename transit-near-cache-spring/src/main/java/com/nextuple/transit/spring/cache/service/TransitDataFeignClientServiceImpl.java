/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.transit.cache.domain.TransitCacheKey;
import com.nextuple.transit.cache.domain.TransitCacheValue;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.nextuple.transit.spring.cache.feign.TransitDataFeignImpl;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// Added this
@AllArgsConstructor
public class TransitDataFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        TransitCacheKey, TransitCacheValue, String, BaseResponse<List<TransitResponse>>> {

  @Autowired TransitDataFeignImpl transitFeign;

  private static Logger logger = LoggerFactory.getLogger(TransitDataFeignClientServiceImpl.class);

  @Autowired
  GenericMapper<TransitCacheKey, TransitCacheValue, String, BaseResponse<List<TransitResponse>>>
      transitMapper;

  @Override
  public TransitCacheValue get(TransitCacheKey key) {
    try {
      return transitMapper.responseToCacheValue(
          transitFeign.getListOfTransitDetailsForDestinationGeoZone(
              key.getOrgId(), key.getDestinationGeozone()));
    } catch (RuntimeException ex) {
      logger.info("exception in transit near cache service: {}", ex);
      return null;
    }
  }
}
