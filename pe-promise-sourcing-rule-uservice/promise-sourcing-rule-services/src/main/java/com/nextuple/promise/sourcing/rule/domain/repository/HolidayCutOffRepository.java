/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.domain.repository;

import com.nextuple.promise.sourcing.rule.domain.entity.HolidayCutoffEntity;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayCutOffRepository
    extends JpaRepository<HolidayCutoffEntity, String>, HolidayCutoffCustomRepository {
  Optional<HolidayCutoffEntity> findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(
      String orgId, String holidayCutoffName, String holidayCutoffRule);

  List<HolidayCutoffEntity> findByOrgIdAndHolidayCutoffRuleAndHolidayCutoffDate(
      String orgId, String holidayCutoffRule, Date holidayCutoffDate);

  Page<HolidayCutoffEntity> findAllByOrgIdAndSourcingAttributesDefinitionId(
      String orgId, Long sourcingAttributesDefinitionId, Pageable pageable);

  Optional<HolidayCutoffEntity> findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRule(
      String orgId, Long sourcingAttributesDefinitionId, String holidayCutoffRule);

  List<HolidayCutoffEntity> findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRuleIn(
      String orgId, Long sourcingAttributesDefinitionId, List<String> holidayCutoffRules);
}
