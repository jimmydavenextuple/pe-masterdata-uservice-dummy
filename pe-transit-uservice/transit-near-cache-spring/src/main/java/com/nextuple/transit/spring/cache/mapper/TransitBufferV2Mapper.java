/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.mapper;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.transit.cache.domain.TransitBufferV2CacheKey;
import com.nextuple.transit.cache.domain.TransitBufferV2CacheValue;
import com.nextuple.transit.domain.outbound.TransitBufferDetailsResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TransitBufferV2Mapper
    implements GenericMapper<
        TransitBufferV2CacheKey,
        TransitBufferV2CacheValue,
        String,
        BaseResponse<List<TransitBufferDetailsResponse>>> {
  @Override
  public TransitBufferV2CacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(TransitBufferV2CacheKey cacheKey) {
    return null;
  }

  @Override
  public TransitBufferV2CacheValue responseToCacheValue(
      BaseResponse<List<TransitBufferDetailsResponse>> resp) {
    return TransitBufferV2CacheValue.builder()
        .transitBufferDetailsResponseList(resp.getPayload())
        .build();
  }

  @Override
  public BaseResponse<List<TransitBufferDetailsResponse>> cacheValueToResponse(
      TransitBufferV2CacheValue cacheValue) {
    return null;
  }
}
