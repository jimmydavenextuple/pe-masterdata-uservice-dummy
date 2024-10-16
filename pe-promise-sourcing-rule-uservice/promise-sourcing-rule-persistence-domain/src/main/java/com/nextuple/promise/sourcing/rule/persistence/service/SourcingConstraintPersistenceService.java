/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingConstraintEnum;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingConstraintDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.SourcingConstraintDomainKey;
import java.util.List;
import java.util.Optional;

public interface SourcingConstraintPersistenceService
    extends DomainPersistenceService<SourcingConstraintDomainDto, SourcingConstraintDomainKey> {

  SourcingConstraintDomainDto saveSourcingConstraintEntity(
      SourcingConstraintDomainDto sourcingConstraintDto) throws PromiseEngineException;

  List<SourcingConstraintDomainDto> saveSourcingConstraintEntities(
      List<SourcingConstraintDomainDto> sourcingConstraintDtos) throws PromiseEngineException;

  Optional<SourcingConstraintDomainDto> getSourcingConstraintEntityByIdAndOrgId(
      Long id, String orgId) throws PromiseEngineException;

  List<SourcingConstraintDomainDto> fetchByOrgIdAndGroupId(String orgId, String groupId)
      throws PromiseEngineException;

  List<SourcingConstraintDomainDto> fetchByOrgIdAndGroupIdAndConstraint(
      String orgId, String groupId, SourcingConstraintEnum constraint)
      throws PromiseEngineException;

  void deleteSourcingConstraint(SourcingConstraintDomainDto sourcingConstraintDto)
      throws PromiseEngineException;

  void deleteByIdIn(List<Long> sourcingConstraintIdsToDelete) throws PromiseEngineException;
}
