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
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.sourcing.rule.cache.domain.SourcingAttributeDefinitionByActiveStatusKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingAttributeDefinitionByActiveStatusValue;
import com.nextuple.sourcing.rule.spring.cache.feign.SourcingAttributeDefinitionFeignImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
// Added this
@RequiredArgsConstructor
public class SourcingAttributesDefinitionFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        SourcingAttributeDefinitionByActiveStatusKey,
        SourcingAttributeDefinitionByActiveStatusValue,
        String,
        BaseResponse<SourcingAttributesDefinitionResponse>> {
  private final SourcingAttributeDefinitionFeignImpl sourcingAttributeDefinitionFeign;

  private final GenericMapper<
          SourcingAttributeDefinitionByActiveStatusKey,
          SourcingAttributeDefinitionByActiveStatusValue,
          String,
          BaseResponse<SourcingAttributesDefinitionResponse>>
      nodeMapper;

  @Override
  public SourcingAttributeDefinitionByActiveStatusValue get(
      SourcingAttributeDefinitionByActiveStatusKey key) {
    try {
      return nodeMapper.responseToCacheValue(
          sourcingAttributeDefinitionFeign.getSourcingAttributesDefinitionInActiveStatus(
              key.getOrgId(), SourcingAttributesDefinitionScopeEnum.valueOf(key.getScope())));
    } catch (RuntimeException ex) {
      return SourcingAttributeDefinitionByActiveStatusValue.builder().build();
    }
  }
}
