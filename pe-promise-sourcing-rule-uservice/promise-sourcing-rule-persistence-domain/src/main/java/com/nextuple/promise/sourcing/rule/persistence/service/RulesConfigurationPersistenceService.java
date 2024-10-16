/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.RuleConfigurationParam;
import com.nextuple.promise.sourcing.rule.persistence.domain.RulesConfigurationDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.RulesConfigurationDomainKey;
import java.util.List;
import java.util.Optional;

public interface RulesConfigurationPersistenceService
    extends DomainPersistenceService<RulesConfigurationDomainDto, RulesConfigurationDomainKey> {

  RulesConfigurationDomainDto saveRulesConfiguration(
      RulesConfigurationDomainDto rulesConfigurationDomainDto) throws PromiseEngineException;

  List<RulesConfigurationDomainDto> findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(
      String orgId, Long attributesDefinitionId, String rule) throws PromiseEngineException;

  Optional<RulesConfigurationDomainDto> findByOrgIdAndAttributeDefinitionIdAndRule(
      String orgId, Long attributesDefinitionId, String rule) throws PromiseEngineException;

  Optional<RulesConfigurationDomainDto> findByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(
      RuleConfigurationParam ruleConfigurationParam) throws PromiseEngineException;

  void deleteRuleConfiguration(RulesConfigurationDomainDto rulesConfigurationDomainDto)
      throws PromiseEngineException;
}
