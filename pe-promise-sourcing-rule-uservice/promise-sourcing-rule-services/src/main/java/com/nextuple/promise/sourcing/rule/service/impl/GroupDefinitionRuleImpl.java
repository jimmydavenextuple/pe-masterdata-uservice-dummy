/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service.impl;

import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.services.RulesRetrievalService;
import com.nextuple.promise.sourcing.rule.persistence.domain.GroupDefinitionDomainDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class GroupDefinitionRuleImpl extends RulesRetrievalService<GroupDefinitionDomainDto> {
  @Override
  public String getRule(
      GroupDefinitionDomainDto ruleInfo,
      SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse) {
    String rule = ruleInfo.getReqAttributesValue();
    if (StringUtils.hasLength(ruleInfo.getOptionalAttributesValue()))
      rule = rule.concat(":").concat(ruleInfo.getOptionalAttributesValue());
    else if (StringUtils.hasLength(sourcingAttributesDefinitionResponse.getOptAttributes())
        && sourcingAttributesDefinitionResponse.getOptAttributes().split(",").length > 0)
      rule =
          String.join(
              "",
              rule,
              ":"
                  .repeat(
                      sourcingAttributesDefinitionResponse.getOptAttributes().split(",").length));
    return rule;
  }
}
