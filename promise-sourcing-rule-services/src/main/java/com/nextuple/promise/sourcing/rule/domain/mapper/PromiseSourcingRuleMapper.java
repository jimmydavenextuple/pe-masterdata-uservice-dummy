/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.domain.mapper;

import com.nextuple.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.persistence.domain.PromiseSourcingRuleDomainDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PromiseSourcingRuleMapper {
  PromiseSourcingRuleDomainDto convertToPromiseSourcingRuleEntity(
      PromiseSourcingRuleDto promiseSourcingRuleDto);

  PromiseSourcingRuleDto convertToPromiseSourcingRuleDto(
      PromiseSourcingRuleDomainDto promiseSourcingRuleDomainDto);

  @Mapping(target = "sourceNodes", ignore = true)
  void insertValuesFromUpdatePromiseSourcingRuleRequestToEntity(
      UpdatePromiseSourcingRuleRequest updatePromiseSourcingRuleRequest,
      @MappingTarget PromiseSourcingRuleDomainDto promiseSourcingRuleDomainDto);

  @AfterMapping
  default void insertValuesFromUpdatePromiseSourcingRuleRequestToEntityMappingSourceNodes(
      UpdatePromiseSourcingRuleRequest updatePromiseSourcingRuleRequest,
      @MappingTarget PromiseSourcingRuleDomainDto promiseSourcingRule) {
    promiseSourcingRule.setSourceNodes(updatePromiseSourcingRuleRequest.getSourceNodes());
  }

  PromiseSourcingRuleDomainDto convertFromCreatePromiseSourcingRuleRequestToEntity(
      CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest);
}
