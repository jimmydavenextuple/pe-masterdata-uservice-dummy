/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.configuration.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.configuration.cache.domain.TenantConfigdataCacheKey;
import com.nextuple.configuration.cache.domain.TenantConfigdataCacheValue;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import com.nextuple.configuration.spring.cache.feign.TenantConfigdataFeignImpl;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// Added this
@AllArgsConstructor
public class TenantConfigdataFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        TenantConfigdataCacheKey,
        TenantConfigdataCacheValue,
        String,
        BaseResponse<TenantConfigdataResponse>> {
  @Autowired TenantConfigdataFeignImpl tenantConfigdataFeign;

  @Autowired
  GenericMapper<
          TenantConfigdataCacheKey,
          TenantConfigdataCacheValue,
          String,
          BaseResponse<TenantConfigdataResponse>>
      tenantConfigdataMapper;

  @Override
  public TenantConfigdataCacheValue get(TenantConfigdataCacheKey key) {
    try {
      return tenantConfigdataMapper.responseToCacheValue(
          tenantConfigdataFeign.getTenantConfigdataByOrgIdAndConfigKey(
              key.getOrgId(), key.getConfigKey()));
    } catch (RuntimeException e) {
      return null;
    }
  }
}
