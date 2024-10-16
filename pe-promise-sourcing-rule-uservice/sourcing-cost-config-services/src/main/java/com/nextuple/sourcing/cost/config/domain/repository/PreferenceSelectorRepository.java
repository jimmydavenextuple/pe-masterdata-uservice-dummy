/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.domain.repository;

import com.nextuple.sourcing.cost.config.domain.entity.PreferenceSelectorEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferenceSelectorRepository
    extends JpaRepository<PreferenceSelectorEntity, String> {
  Optional<PreferenceSelectorEntity> findByIdAndOrgId(long id, String orgId);

  List<PreferenceSelectorEntity> findByOrgIdAndSelectorCf(String orgId, String selectorCf);

  List<PreferenceSelectorEntity> findByOrgIdAndCostType(String orgId, String costType);

  @Query(value = "SELECT * FROM preference_selector LIMIT ?1", nativeQuery = true)
  List<PreferenceSelectorEntity> findAllPreferenceSelectorEntities(Integer limit);

  Optional<PreferenceSelectorEntity> findFirstByOrgIdAndCostType(String orgId, String costType);
}
