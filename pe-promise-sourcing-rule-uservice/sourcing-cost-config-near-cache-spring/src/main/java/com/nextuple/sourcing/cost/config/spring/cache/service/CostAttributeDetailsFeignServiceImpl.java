/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.sourcing.cost.config.cache.domain.CostAttributeDetailsCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostAttributeDetailsCacheValue;
import com.nextuple.sourcing.cost.config.dto.CostAttributeDto;
import com.nextuple.sourcing.cost.config.spring.cache.feign.CostAttributeDetailsFeignImpl;
import com.nextuple.sourcing.cost.config.spring.cache.mapper.CostAttributeDetailsMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// Added this
@AllArgsConstructor
public class CostAttributeDetailsFeignServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        CostAttributeDetailsCacheKey,
        CostAttributeDetailsCacheValue,
        String,
        BaseResponse<CostAttributeDto>> {

  @Autowired CostAttributeDetailsFeignImpl costAttributeDetailsFeign;

  @Autowired CostAttributeDetailsMapper costAttributeDetailsMapper;

  @Override
  public CostAttributeDetailsCacheValue get(CostAttributeDetailsCacheKey key) {
    try {
      return costAttributeDetailsMapper.responseToCacheValue(
          costAttributeDetailsFeign.getCostAttributeDetailsByAttributeName(key.getAttributeName()));
    } catch (RuntimeException ex) {
      return CostAttributeDetailsCacheValue.builder().build();
    }
  }
}
