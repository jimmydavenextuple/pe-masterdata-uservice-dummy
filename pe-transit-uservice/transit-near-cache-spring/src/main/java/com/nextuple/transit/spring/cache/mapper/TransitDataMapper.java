/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

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
