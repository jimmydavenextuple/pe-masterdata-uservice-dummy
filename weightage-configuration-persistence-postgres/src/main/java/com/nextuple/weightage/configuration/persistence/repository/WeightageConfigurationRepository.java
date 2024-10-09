/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.persistence.repository;

import com.nextuple.postgres.repository.CommonJpaRepository;
import com.nextuple.weightage.configuration.persistence.entity.WeightageConfigurationEntity;
import com.nextuple.weightage.configuration.persistence.entity.key.WeightageConfigurationKey;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WeightageConfigurationRepository
    extends CommonJpaRepository<WeightageConfigurationEntity, WeightageConfigurationKey> {
  List<WeightageConfigurationEntity> findByKeyInAndOrgIdAndType(
      List<String> keys, String orgId, String type);

  WeightageConfigurationEntity findByOrgIdAndTypeAndKey(String orgId, String type, String key);

  List<WeightageConfigurationEntity> findByKey(String key);

  @Query(value = "SELECT * FROM weightage_configuration LIMIT ?1", nativeQuery = true)
  List<WeightageConfigurationEntity> findAllRecords(Integer limit);
}
