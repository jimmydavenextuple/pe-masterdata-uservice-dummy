/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.rule.spring.cache.mapper;

import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchSourcingRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.HolidayCutoffRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.RulesConfigurationResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.sourcing.rule.cache.domain.HolidayCutoffValue;
import com.nextuple.sourcing.rule.cache.domain.RulesConfigurationValue;
import com.nextuple.sourcing.rule.cache.domain.SourcingAttributeByIdValue;
import com.nextuple.sourcing.rule.cache.domain.SourcingAttributeDefinitionByActiveStatusValue;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleConfigurationFetchRulesValue;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DataMapper {
  SourcingAttributeDefinitionByActiveStatusValue toSourcingAttributeDefinitionByActiveStatus(
      SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse);

  SourcingRuleConfigurationFetchRulesValue toSourcingRuleConfigurationFetchRules(
      FetchSourcingRulesResponse fetchSourcingRulesResponse);

  SourcingAttributeByIdValue toSourcingAttributeByIdValue(
      SourcingAttributeResponse sourcingAttributeResponse);

  HolidayCutoffValue toHolidayCutoffValue(HolidayCutoffRulesResponse holidayCutoffRulesResponse);

  RulesConfigurationValue toRuleConfigurationValue(
      RulesConfigurationResponse rulesConfigurationResponse);
}
