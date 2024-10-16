/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.sourcing.cost.config.cache.domain.SelectorAndCostItineraryCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.SelectorAndCostItineraryCacheValue;
import com.nextuple.sourcing.cost.config.outbound.SelectorAndCostItineraryMappingResponse;
import com.nextuple.sourcing.cost.config.spring.cache.feign.SelectorAndCostItineraryFeignImpl;
import com.nextuple.sourcing.cost.config.spring.cache.mapper.SelectorAndCostItineraryMapper;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// Added this
@AllArgsConstructor
public class SelectorAndCostItineraryFeignServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        SelectorAndCostItineraryCacheKey,
        SelectorAndCostItineraryCacheValue,
        String,
        BaseResponse<List<SelectorAndCostItineraryMappingResponse>>> {

  @Autowired SelectorAndCostItineraryFeignImpl selectorAndCostItineraryFeign;

  @Autowired SelectorAndCostItineraryMapper selectorAndCostItineraryMapper;

  @Override
  public SelectorAndCostItineraryCacheValue get(SelectorAndCostItineraryCacheKey key) {
    try {
      return selectorAndCostItineraryMapper.responseToCacheValue(
          selectorAndCostItineraryFeign
              .getSelectorAndCostItineraryMappingByOrgIdSelectorCfAndCostType(
                  key.getOrgId(), key.getCostType(), key.getSelectorCf()));
    } catch (RuntimeException ex) {
      return SelectorAndCostItineraryCacheValue.builder().build();
    }
  }
}
