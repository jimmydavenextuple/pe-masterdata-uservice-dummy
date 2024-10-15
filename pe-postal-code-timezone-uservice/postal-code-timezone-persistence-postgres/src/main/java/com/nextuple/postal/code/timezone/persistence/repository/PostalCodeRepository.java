/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.persistence.repository;

import com.nextuple.postal.code.timezone.api.domain.projection.MarketRegionProjection;
import com.nextuple.postal.code.timezone.persistence.entity.PostalCodeEntity;
import com.nextuple.postal.code.timezone.persistence.entity.key.PostalCodeKey;
import com.nextuple.postgres.repository.CommonJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostalCodeRepository extends CommonJpaRepository<PostalCodeEntity, PostalCodeKey> {

  PostalCodeEntity save(PostalCodeEntity postalCodeEntity);

  Optional<PostalCodeEntity> findByOrgIdAndZipCode(String orgId, String postalCode);

  List<PostalCodeEntity> findByOrgIdAndZipCodePrefix(String orgId, String postalCodePrefix);

  @Modifying
  @Query(
      value =
          "SELECT "
              + " v.country AS country , COUNT (DISTINCT v.city) AS noOfCities , COUNT (DISTINCT v.state) AS noOfStates "
              + " , COUNT (DISTINCT v.zip_code_prefix) AS noOfZipCodePrefixes , "
              + " CAST( MAX( v.last_modified_date ) AS Date ) AS uploadDate FROM postal_code v WHERE v.org_id = ?1 "
              + "GROUP BY v.country ",
      nativeQuery = true)
  List<MarketRegionProjection> findRecordsByOrgId(String orgId);

  List<PostalCodeEntity> findByOrgIdAndCountry(String orgId, String country);

  @Query(
      value = "SELECT zip_code_prefix FROM postal_code WHERE org_id = ?1 AND state = ?2",
      nativeQuery = true)
  List<String> findByOrgIdAndState(String orgId, String state);

  List<PostalCodeEntity> findByOrgIdOrderByStateAscZipCodePrefixAsc(String orgId);
}
