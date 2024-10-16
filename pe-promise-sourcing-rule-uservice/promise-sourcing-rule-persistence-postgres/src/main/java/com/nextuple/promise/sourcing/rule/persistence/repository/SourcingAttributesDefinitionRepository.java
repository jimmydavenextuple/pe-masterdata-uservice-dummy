/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.repository;

import com.nextuple.postgres.repository.CommonJpaRepository;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.projection.SourcingRuleByNodeGroupCountProjection;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingAttributesDefinitionEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.SourcingAttributesDefinitionKey;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SourcingAttributesDefinitionRepository
    extends CommonJpaRepository<
        SourcingAttributesDefinitionEntity, SourcingAttributesDefinitionKey> {
  Optional<SourcingAttributesDefinitionEntity> findById(long id);

  Optional<SourcingAttributesDefinitionEntity> findByIdAndOrgId(long id, String orgId);

  Optional<SourcingAttributesDefinitionEntity> findByIdAndOrgIdAndScope(
      long id, String orgId, SourcingAttributesDefinitionScopeEnum scope);

  List<SourcingAttributesDefinitionEntity> findByOrgIdAndName(String orgId, String name);

  List<SourcingAttributesDefinitionEntity> findByOrgIdAndStatusAndScope(
      String orgId,
      SourcingAttributesDefinitionStatus status,
      SourcingAttributesDefinitionScopeEnum scope);

  @Query(
      value =
          "SELECT details.node_groups as nodeGroup, count(details.id) FROM public.sourcing_rule_details as details\n"
              + "inner join public.sourcing_rules_configuration as config\n"
              + "on config.id=details.sourcing_rule_id \n"
              + "where details.node_groups in :nodeGroupIds \n"
              + "and config.sourcing_attributes_definition_id=:sourcingAttributesDefinitionId \n"
              + "group by details.node_groups",
      nativeQuery = true)
  List<SourcingRuleByNodeGroupCountProjection> fetchActiveSourcingRuleCountByNodeGroupIds(
      @Param("nodeGroupIds") List<String> nodeGroupEntityIds,
      @Param("sourcingAttributesDefinitionId") Long sourcingAttributesDefinitionId);
}
