/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.sourcing.cost.config.cache.domain.CostItineraryAndFactorsCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostItineraryAndFactorsCacheValue;
import com.nextuple.sourcing.cost.config.dto.CostItineraryAndFactorsDto;
import com.nextuple.sourcing.cost.config.spring.cache.feign.CostItineraryAndFactorsDtoFeignImpl;
import com.nextuple.sourcing.cost.config.spring.cache.mapper.CostItineraryAndFactorsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
// Added this
@RequiredArgsConstructor
public class CostItineraryAndFactorsFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        CostItineraryAndFactorsCacheKey,
        CostItineraryAndFactorsCacheValue,
        String,
        BaseResponse<CostItineraryAndFactorsDto>> {

  private final CostItineraryAndFactorsDtoFeignImpl costItineraryAndFactorsDtoFeign;

  private final CostItineraryAndFactorsMapper costItineraryAndFactorsMapper;

  @Override
  public CostItineraryAndFactorsCacheValue get(CostItineraryAndFactorsCacheKey key) {
    try {
      return costItineraryAndFactorsMapper.responseToCacheValue(
          costItineraryAndFactorsDtoFeign
              .getCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActive(
                  key.getOrgId(),
                  key.getCostItinerary(),
                  key.getItineraryStatus(),
                  key.getIsActive()));
    } catch (RuntimeException ex) {
      return CostItineraryAndFactorsCacheValue.builder().build();
    }
  }
}
