/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.cache.spring.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.cache.domain.CustomRegionCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.CustomRegionCacheValue;
import com.nextuple.postal.code.timezone.cache.spring.feign.CustomRegionFeignImpl;
import com.nextuple.postal.code.timezone.cache.spring.mapper.CustomRegionMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// Added this
@AllArgsConstructor
public class CustomRegionFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        CustomRegionCacheKey, CustomRegionCacheValue, String, BaseResponse<CustomRegionResponse>> {

  @Autowired CustomRegionFeignImpl customRegionFeign;

  @Autowired CustomRegionMapper customRegionMapper;

  @Override
  public CustomRegionCacheValue get(CustomRegionCacheKey key) {
    try {
      return customRegionMapper.responseToCacheValue(
          customRegionFeign.fetchCustomRegionIdByPostalCode(key.getOrgId(), key.getZipCode()));
    } catch (RuntimeException ex) {
      return null;
    }
  }
}
