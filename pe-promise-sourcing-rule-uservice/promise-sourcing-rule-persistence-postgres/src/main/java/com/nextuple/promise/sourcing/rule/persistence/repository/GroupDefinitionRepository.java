/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.repository;

import com.nextuple.postgres.repository.CommonJpaRepository;
import com.nextuple.promise.sourcing.rule.persistence.entity.GroupDefinitionEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.GroupDefinitionKey;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupDefinitionRepository
    extends CommonJpaRepository<GroupDefinitionEntity, GroupDefinitionKey> {

  Optional<GroupDefinitionEntity> findById(long id);

  Optional<GroupDefinitionEntity> findByIdAndOrgId(long id, String orgId);

  List<GroupDefinitionEntity> findByOrgIdAndGroupName(String orgId, String groupName);

  List<GroupDefinitionEntity> findByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
      String orgId, Long sourcingAttributesDefinitionId, String reqAttributesValue);

  List<GroupDefinitionEntity>
      findBySourcingAttributesDefinitionIdAndReqAttributesValueAndOptionalAttributesValueAndOrgId(
          Long sourcingAttributesDefinitionId,
          String reqAttributesValue,
          String optionalAttributesValue,
          String orgId);

  List<GroupDefinitionEntity> findByOrgIdAndSourcingAttributesDefinitionId(
      String orgId, Long sourcingAttributesDefinitionId);

  void deleteByIdIn(List<Long> ids);
}
