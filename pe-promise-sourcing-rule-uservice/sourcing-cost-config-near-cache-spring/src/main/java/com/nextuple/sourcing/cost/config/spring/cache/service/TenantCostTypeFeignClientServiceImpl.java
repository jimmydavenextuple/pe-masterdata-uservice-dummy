/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.sourcing.cost.config.cache.domain.TenantCostTypeCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.TenantCostTypeCacheValue;
import com.nextuple.sourcing.cost.config.outbound.TenantCostTypeResponse;
import com.nextuple.sourcing.cost.config.spring.cache.feign.TenantCostTypeFeignImpl;
import com.nextuple.sourcing.cost.config.spring.cache.mapper.TenantCostTypeMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
// Added this
@RequiredArgsConstructor
public class TenantCostTypeFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        TenantCostTypeCacheKey,
        TenantCostTypeCacheValue,
        String,
        BaseResponse<List<TenantCostTypeResponse>>> {

  private final TenantCostTypeFeignImpl tenantCostTypeFeign;

  private final TenantCostTypeMapper tenantCostTypeMapper;

  @Override
  public TenantCostTypeCacheValue get(TenantCostTypeCacheKey key) {
    try {
      return tenantCostTypeMapper.responseToCacheValue(
          tenantCostTypeFeign.getTenantCostTypeByOrgId(key.getOrgId()));
    } catch (RuntimeException ex) {
      return TenantCostTypeCacheValue.builder().build();
    }
  }
}
