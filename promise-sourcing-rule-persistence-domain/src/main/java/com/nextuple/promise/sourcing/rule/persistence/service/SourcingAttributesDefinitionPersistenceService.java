/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.projection.SourcingRuleByNodeGroupCountProjection;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributesDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.SourcingAttributesDefinitionDomainKey;
import java.util.List;
import java.util.Optional;

public interface SourcingAttributesDefinitionPersistenceService
    extends DomainPersistenceService<
        SourcingAttributesDefinitionDomainDto, SourcingAttributesDefinitionDomainKey> {

  SourcingAttributesDefinitionDomainDto saveSourcingRuleAttributesDefinitionEntity(
      SourcingAttributesDefinitionDomainDto sourcingRuleAttributesDefinitionDto)
      throws PromiseEngineException;

  Optional<SourcingAttributesDefinitionDomainDto> getSourcingRuleAttributesDefinitionEntityById(
      Long id) throws PromiseEngineException;

  Optional<SourcingAttributesDefinitionDomainDto>
      getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(Long id, String orgId)
          throws PromiseEngineException;

  Optional<SourcingAttributesDefinitionDomainDto>
      getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(
          Long id, String orgId, SourcingAttributesDefinitionScopeEnum scope)
          throws PromiseEngineException;

  List<SourcingAttributesDefinitionDomainDto>
      fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(String orgId, String name)
          throws PromiseEngineException;

  List<SourcingAttributesDefinitionDomainDto>
      fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
          String orgId,
          SourcingAttributesDefinitionStatus status,
          SourcingAttributesDefinitionScopeEnum scope)
          throws PromiseEngineException;

  List<SourcingRuleByNodeGroupCountProjection> fetchActiveSourcingRuleCountByNodeGroupIds(
      List<String> nodeGroupEntityIds, Long sourcingAttributesDefinitionId);
}
