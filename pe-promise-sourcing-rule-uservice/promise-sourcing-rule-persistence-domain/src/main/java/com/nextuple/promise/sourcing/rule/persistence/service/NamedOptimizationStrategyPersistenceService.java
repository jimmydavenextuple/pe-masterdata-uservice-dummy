/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.domain.NamedOptimizationStrategyDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.NamedOptimizationStrategyDomainKey;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NamedOptimizationStrategyPersistenceService
    extends DomainPersistenceService<
        NamedOptimizationStrategyDomainDto, NamedOptimizationStrategyDomainKey> {

  NamedOptimizationStrategyDomainDto saveOptimizationStrategy(
      NamedOptimizationStrategyDomainDto namedOptimizationStrategyDomainDto)
      throws PromiseEngineException;

  Optional<NamedOptimizationStrategyDomainDto> fetchOptimizationStrategyById(Long id)
      throws PromiseEngineException;

  Optional<NamedOptimizationStrategyDomainDto> fetchOptimizationStrategyByIdAndOrgId(
      Long id, String orgId) throws PromiseEngineException;

  List<NamedOptimizationStrategyDomainDto> fetchOptimizationStrategyByOrgIdAndGroupId(
      String orgId, String groupId) throws PromiseEngineException;

  List<NamedOptimizationStrategyDomainDto> fetchOptimizationStrategyByOrgIdAndStrategyName(
      String orgId, String optimizationStrategyName) throws PromiseEngineException;

  void deleteOptimizationStrategy(
      NamedOptimizationStrategyDomainDto namedOptimizationStrategyDomainDto)
      throws PromiseEngineException;

  Page<NamedOptimizationStrategyDomainDto> fetchOptimizationStrategyByOrgIdAndGroupId(
      String orgId, String groupId, Pageable pageable) throws PromiseEngineException;

  void deleteByIdIn(List<Long> optimizationRuleIds) throws PromiseEngineException;

  List<NamedOptimizationStrategyDomainDto>
      fetchOptimizationStrategyByOrgIdAndOptimizationStrategyDetails(
          String orgId, String optimizationStrategyDetails) throws PromiseEngineException;
}
