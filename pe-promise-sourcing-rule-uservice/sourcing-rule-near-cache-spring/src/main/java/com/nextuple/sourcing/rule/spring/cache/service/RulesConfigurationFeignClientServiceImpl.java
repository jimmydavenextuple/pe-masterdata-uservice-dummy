/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.rule.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.RulesConfigurationResponse;
import com.nextuple.sourcing.rule.cache.domain.RulesConfigurationKey;
import com.nextuple.sourcing.rule.cache.domain.RulesConfigurationValue;
import com.nextuple.sourcing.rule.spring.cache.feign.RulesConfigurationFeign;
import com.nextuple.sourcing.rule.spring.cache.mapper.RulesConfigurationMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RulesConfigurationFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        RulesConfigurationKey,
        RulesConfigurationValue,
        String,
        BaseResponse<RulesConfigurationResponse>> {

  private final RulesConfigurationFeign ruleConfigurationFeign;

  private final RulesConfigurationMapper rulesConfigurationMapper;

  @Override
  public RulesConfigurationValue get(RulesConfigurationKey key) {
    try {
      return rulesConfigurationMapper.responseToCacheValue(
          ruleConfigurationFeign.fetchRulesConfiguration(key.getFetchRuleConfigurationRequest()));
    } catch (RuntimeException ex) {
      return RulesConfigurationValue.builder().build();
    }
  }
}
