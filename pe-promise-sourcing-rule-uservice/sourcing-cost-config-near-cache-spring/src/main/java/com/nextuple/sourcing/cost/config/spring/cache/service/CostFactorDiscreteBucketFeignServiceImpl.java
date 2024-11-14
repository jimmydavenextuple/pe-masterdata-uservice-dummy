/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorDiscreteBucketCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorDiscreteBucketCacheValue;
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketDto;
import com.nextuple.sourcing.cost.config.spring.cache.feign.CostFactorDiscreteBucketFeignImpl;
import com.nextuple.sourcing.cost.config.spring.cache.mapper.CostFactorDiscreteBucketMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CostFactorDiscreteBucketFeignServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        CostFactorDiscreteBucketCacheKey,
        CostFactorDiscreteBucketCacheValue,
        String,
        BaseResponse<List<CostFactorDiscreteBucketDto>>> {
  private final CostFactorDiscreteBucketFeignImpl costFactorDiscreteBucketFeign;
  private final CostFactorDiscreteBucketMapper costFactorDiscreteBucketMapper;

  @Override
  public CostFactorDiscreteBucketCacheValue get(CostFactorDiscreteBucketCacheKey key) {
    try {
      BaseResponse<List<CostFactorDiscreteBucketDto>> response =
          costFactorDiscreteBucketFeign.getCostFactorDiscreteBucket(
              key.getOrgId(), key.getCostFactor());
      if (Objects.isNull(response)
          || Objects.isNull(response.getPayload())
          || response.getPayload().isEmpty()) {
        return CostFactorDiscreteBucketCacheValue.builder().build();
      }
      Map<String, String> discreteBucketMap = new HashMap<>();
      for (CostFactorDiscreteBucketDto costFactorDiscreteBucketDto : response.getPayload()) {
        List<String> discreteValuesList =
            Arrays.stream(costFactorDiscreteBucketDto.getValueList().split(",")).toList();
        discreteValuesList.forEach(
            value -> discreteBucketMap.put(value, costFactorDiscreteBucketDto.getNotation()));
      }
      return CostFactorDiscreteBucketCacheValue.builder()
          .valueToBucketMapping(discreteBucketMap)
          .build();
    } catch (RuntimeException ex) {
      return null;
    }
  }
}
