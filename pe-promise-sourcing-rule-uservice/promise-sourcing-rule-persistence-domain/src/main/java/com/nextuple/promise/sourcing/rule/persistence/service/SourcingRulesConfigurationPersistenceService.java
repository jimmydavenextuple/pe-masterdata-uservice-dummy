/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingRulesConfigurationDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.SourcingRulesConfigurationDomainKey;
import java.util.List;
import java.util.Optional;

public interface SourcingRulesConfigurationPersistenceService
    extends DomainPersistenceService<
        SourcingRulesConfigurationDomainDto, SourcingRulesConfigurationDomainKey> {

  SourcingRulesConfigurationDomainDto saveSourcingRule(
      SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDto)
      throws PromiseEngineException;

  Optional<SourcingRulesConfigurationDomainDto> getSourcingRuleById(Long id)
      throws PromiseEngineException;

  Optional<SourcingRulesConfigurationDomainDto> getSourcingRuleByIdAndOrgId(Long id, String orgId)
      throws PromiseEngineException;

  Optional<List<SourcingRulesConfigurationDomainDto>> getSourcingRuleByOrgIdAndSourcingRule(
      String orgId, String sourcingRule) throws PromiseEngineException;

  List<SourcingRulesConfigurationDomainDto>
      getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
          String orgId, Long sourcingAttributesDefinitionId, String sourcingRule)
          throws PromiseEngineException;

  Optional<SourcingRulesConfigurationDomainDto>
      getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
          String orgId, Long sourcingAttributesDefinitionId, String sourcingRule)
          throws PromiseEngineException;

  SourcingRulesConfigurationDomainDto getSourcingRulesByOrgIdAndSourcingRuleName(
      String orgId, String sourcingRuleName) throws PromiseEngineException;

  SourcingRulesConfigurationDomainDto
      getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
          String orgId, Long sourcingAttributesDefinitionId, String sourcingRuleName)
          throws PromiseEngineException;

  void deleteSourcingRule(SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDto)
      throws PromiseEngineException;

  void deleteMultipleSourcingRules(
      List<SourcingRulesConfigurationDomainDto> sourcingRulesConfigurationDtos)
      throws PromiseEngineException;
}
