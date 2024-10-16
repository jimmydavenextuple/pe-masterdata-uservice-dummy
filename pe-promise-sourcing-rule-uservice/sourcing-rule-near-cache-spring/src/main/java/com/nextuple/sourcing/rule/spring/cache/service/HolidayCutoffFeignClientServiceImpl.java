/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.rule.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.HolidayCutoffRulesResponse;
import com.nextuple.sourcing.rule.cache.domain.HolidayCutoffKey;
import com.nextuple.sourcing.rule.cache.domain.HolidayCutoffValue;
import com.nextuple.sourcing.rule.spring.cache.feign.HolidayCutoffFeignImpl;
import com.nextuple.sourcing.rule.spring.cache.mapper.HolidayCutoffMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// Added this
@AllArgsConstructor
public class HolidayCutoffFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        HolidayCutoffKey, HolidayCutoffValue, String, BaseResponse<HolidayCutoffRulesResponse>> {
  @Autowired HolidayCutoffFeignImpl holidayCutoffFeign;

  @Autowired HolidayCutoffMapper holidayCutoffMapper;

  @Override
  public HolidayCutoffValue get(HolidayCutoffKey key) {
    try {
      return holidayCutoffMapper.responseToCacheValue(
          holidayCutoffFeign.fetchHolidayCutoffRules(key.getHolidayCutoffRulesRequest()));
    } catch (RuntimeException ex) {
      return HolidayCutoffValue.builder().build();
    }
  }
}
