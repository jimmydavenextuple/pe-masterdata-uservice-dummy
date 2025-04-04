/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.repository;

import com.nextuple.postgres.repository.CommonJpaRepository;
import com.nextuple.promise.sourcing.rule.api.domain.enums.RulesConfigurationModuleNameEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.persistence.entity.RulesConfigurationEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.RulesConfigurationKey;
import java.util.List;
import java.util.Optional;

public interface RulesConfigurationRepository
    extends CommonJpaRepository<RulesConfigurationEntity, RulesConfigurationKey> {
  List<RulesConfigurationEntity> findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(
      String orgId, Long attributeDefinitionId, String rule);

  List<RulesConfigurationEntity> findByOrgIdAndAttributeDefinitionIdAndRule(
      String orgId, Long attributeDefinitionId, String rule);

  Optional<RulesConfigurationEntity> findByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(
      String orgId,
      String ruleName,
      String rule,
      RulesConfigurationModuleNameEnum moduleName,
      SourcingAttributesDefinitionScopeEnum scope);

  List<RulesConfigurationEntity> findByOrgIdAndAttributeDefinitionIdAndModuleNameAndScope(
      String orgId,
      Long attributeDefinitionId,
      RulesConfigurationModuleNameEnum moduleName,
      SourcingAttributesDefinitionScopeEnum scope);
}
