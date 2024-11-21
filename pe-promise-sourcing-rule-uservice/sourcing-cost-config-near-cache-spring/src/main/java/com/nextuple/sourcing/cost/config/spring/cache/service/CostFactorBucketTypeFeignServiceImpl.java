/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorBucketTypeCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorBucketTypeCacheValue;
import com.nextuple.sourcing.cost.config.dto.CostFactorBucketTypeDto;
import com.nextuple.sourcing.cost.config.spring.cache.feign.CostFactorBucketTypeFeignImpl;
import com.nextuple.sourcing.cost.config.spring.cache.mapper.CostFactorBucketTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CostFactorBucketTypeFeignServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        CostFactorBucketTypeCacheKey,
        CostFactorBucketTypeCacheValue,
        String,
        BaseResponse<CostFactorBucketTypeDto>> {
  private final CostFactorBucketTypeFeignImpl costFactorBucketTypeFeign;
  private final CostFactorBucketTypeMapper costFactorBucketTypeMapper;

  @Override
  public CostFactorBucketTypeCacheValue get(CostFactorBucketTypeCacheKey key) {
    try {
      return costFactorBucketTypeMapper.responseToCacheValue(
          costFactorBucketTypeFeign.getCostFactorBucket(key.getOrgId(), key.getCostFactor()));
    } catch (RuntimeException ex) {
      return CostFactorBucketTypeCacheValue.builder().build();
    }
  }
}
