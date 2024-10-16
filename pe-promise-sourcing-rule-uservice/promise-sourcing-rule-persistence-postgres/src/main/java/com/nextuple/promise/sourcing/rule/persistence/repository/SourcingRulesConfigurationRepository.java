/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.repository;

import com.nextuple.postgres.repository.CommonJpaRepository;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingRulesConfigurationEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.SourcingRulesConfigurationKey;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SourcingRulesConfigurationRepository
    extends CommonJpaRepository<SourcingRulesConfigurationEntity, SourcingRulesConfigurationKey> {

  Optional<SourcingRulesConfigurationEntity> findById(long id);

  Optional<List<SourcingRulesConfigurationEntity>> findByOrgIdAndSourcingRule(
      String orgId, String sourcingRule);

  Optional<SourcingRulesConfigurationEntity> findByIdAndOrgId(Long id, String orgId);

  Optional<List<SourcingRulesConfigurationEntity>> findByOrgId(String orgId);

  Page<SourcingRulesConfigurationEntity> findByOrgId(String orgId, Pageable pageable);

  List<SourcingRulesConfigurationEntity>
      findByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleStartsWith(
          String orgId, Long sourcingAttributesDefinitionId, String sourcingRule);

  SourcingRulesConfigurationEntity findByOrgIdAndSourcingRuleName(
      String orgId, String sourcingRuleName);

  SourcingRulesConfigurationEntity findByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
      String orgId, Long sourcingAttributesDefinitionId, String sourcingRuleName);
}
