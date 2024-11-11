/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.rule.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.sourcing.rule.cache.domain.SourcingAttributeByIdKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingAttributeByIdValue;
import com.nextuple.sourcing.rule.spring.cache.feign.SourcingAttributeFeignImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
// Added this
@RequiredArgsConstructor
public class SourcingAttributeFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        SourcingAttributeByIdKey,
        SourcingAttributeByIdValue,
        String,
        BaseResponse<SourcingAttributeResponse>> {
  private final SourcingAttributeFeignImpl sourcingAttributeFeign;

  private final GenericMapper<
          SourcingAttributeByIdKey,
          SourcingAttributeByIdValue,
          String,
          BaseResponse<SourcingAttributeResponse>>
      nodeMapper;

  @Override
  public SourcingAttributeByIdValue get(SourcingAttributeByIdKey key) {
    try {
      return nodeMapper.responseToCacheValue(
          sourcingAttributeFeign.getSourcingAttributeByOrgIdAndId(key.getOrgId(), key.getId()));
    } catch (RuntimeException ex) {
      return SourcingAttributeByIdValue.builder().build();
    }
  }
}
