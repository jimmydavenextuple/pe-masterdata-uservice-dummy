/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.rule.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.DetailedOptimizationStrategyResponse;
import com.nextuple.sourcing.rule.cache.domain.OptimizationStrategyCacheKey;
import com.nextuple.sourcing.rule.cache.domain.OptimizationStrategyCacheValue;
import com.nextuple.sourcing.rule.spring.cache.feign.OptimizationStrategyFeignImpl;
import com.nextuple.sourcing.rule.spring.cache.mapper.OptimizationStrategyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OptimizationStrategyFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        OptimizationStrategyCacheKey,
        OptimizationStrategyCacheValue,
        String,
        BaseResponse<DetailedOptimizationStrategyResponse>> {

  private final OptimizationStrategyFeignImpl optimizationStrategyFeign;

  private final OptimizationStrategyMapper optimizationStrategyMapper;

  @Override
  public OptimizationStrategyCacheValue get(OptimizationStrategyCacheKey key) {
    try {
      return optimizationStrategyMapper.responseToCacheValue(
          optimizationStrategyFeign.getOptimizationStrategyByOrgIdAndGroupId(
              key.getOrgId(), key.getGroupId()));
    } catch (RuntimeException ex) {
      return OptimizationStrategyCacheValue.builder().build();
    }
  }
}
