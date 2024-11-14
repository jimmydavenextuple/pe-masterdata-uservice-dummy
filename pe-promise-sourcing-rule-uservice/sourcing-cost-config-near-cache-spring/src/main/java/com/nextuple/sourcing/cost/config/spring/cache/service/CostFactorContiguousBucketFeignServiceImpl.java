/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorContiguousBucketCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorContiguousBucketCacheValue;
import com.nextuple.sourcing.cost.config.dto.CostFactorContiguousBucketDto;
import com.nextuple.sourcing.cost.config.spring.cache.feign.CostFactorContiguousBucketFeignImpl;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CostFactorContiguousBucketFeignServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        CostFactorContiguousBucketCacheKey,
        CostFactorContiguousBucketCacheValue,
        String,
        BaseResponse<List<CostFactorContiguousBucketDto>>> {
  private final CostFactorContiguousBucketFeignImpl costFactorContiguousBucketFeign;

  @Override
  public CostFactorContiguousBucketCacheValue get(CostFactorContiguousBucketCacheKey key) {
    try {
      BaseResponse<List<CostFactorContiguousBucketDto>> response =
          costFactorContiguousBucketFeign.getCostFactorContiguousBucket(
              key.getOrgId(), key.getCostFactor());
      if (Objects.isNull(response)
          || Objects.isNull(response.getPayload())
          || response.getPayload().isEmpty()) {
        return CostFactorContiguousBucketCacheValue.builder().build();
      }
      TreeMap<Double, CostFactorContiguousBucketDto> contiguousBucketMap = new TreeMap<>();

      response
          .getPayload()
          .forEach(
              costFactorContiguousBucketDto -> {
                double toValue =
                    Objects.nonNull(costFactorContiguousBucketDto.getToValue())
                        ? costFactorContiguousBucketDto.getToValue()
                        : Double.MAX_VALUE;
                contiguousBucketMap.put(toValue, costFactorContiguousBucketDto);
              });
      return CostFactorContiguousBucketCacheValue.builder()
          .bucketsTreeMap(contiguousBucketMap)
          .build();
    } catch (RuntimeException ex) {
      return CostFactorContiguousBucketCacheValue.builder().build();
    }
  }
}
