/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.mapper;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.transit.cache.domain.TransferScheduleCacheKey;
import com.nextuple.transit.cache.domain.TransferScheduleCacheValue;
import com.nextuple.transit.domain.outbound.TransferScheduleRangeResponse;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TransferScheduleMapper
    implements GenericMapper<
        TransferScheduleCacheKey,
        TransferScheduleCacheValue,
        String,
        BaseResponse<List<TransferScheduleRangeResponse>>> {
  @Override
  public TransferScheduleCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(TransferScheduleCacheKey cacheKey) {
    return null;
  }

  @Override
  public TransferScheduleCacheValue responseToCacheValue(
          BaseResponse<List<TransferScheduleRangeResponse>> resp) {
    return TransferScheduleCacheValue.builder()
        .transferScheduleResponseList(resp.getPayload())
        .build();
  }

  @Override
  public BaseResponse<List<TransferScheduleRangeResponse>> cacheValueToResponse(
      TransferScheduleCacheValue cacheValue) {
    return null;
  }
}
