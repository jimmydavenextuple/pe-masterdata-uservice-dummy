/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.repository;

import com.nextuple.postgres.repository.CommonJpaRepository;
import com.nextuple.promise.sourcing.rule.persistence.entity.NamedOptimizationStrategyEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.NamedOptimizationStrategyKey;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface NamedOptimizationStrategyRepository
    extends CommonJpaRepository<NamedOptimizationStrategyEntity, NamedOptimizationStrategyKey> {

  Optional<NamedOptimizationStrategyEntity> findById(long id);

  List<NamedOptimizationStrategyEntity> findByOrgIdAndGroupId(String orgId, String groupId);

  Page<NamedOptimizationStrategyEntity> findByOrgIdAndGroupId(
      String orgId, String groupId, Pageable pageable);

  List<NamedOptimizationStrategyEntity> findByOrgIdAndOptimizationStrategyName(
      String orgId, String optimizationStrategyName);

  void deleteByIdIn(List<Long> ids);

  Optional<NamedOptimizationStrategyEntity> findByIdAndOrgId(long id, String orgId);

  List<NamedOptimizationStrategyEntity> findByOrgIdAndOptimizationStrategyDetails(
      String orgId, String optimizationStrategyDetails);
}
