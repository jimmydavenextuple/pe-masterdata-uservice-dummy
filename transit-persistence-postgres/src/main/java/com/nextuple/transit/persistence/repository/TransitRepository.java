/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.repository;

import com.nextuple.postgres.repository.CommonJpaRepository;
import com.nextuple.transit.domain.pojo.ProjectedTransitEntity;
import com.nextuple.transit.persistence.entity.TransitEntity;
import com.nextuple.transit.persistence.entity.key.TransitKey;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransitRepository extends CommonJpaRepository<TransitEntity, TransitKey> {

  @Query(
      value =
          "SELECT * FROM transit_data t WHERE t.org_id = ?1 AND t.source_geozone = ?2 AND t.destination_geozone = ?3 AND ( t.carrier_service_id = ?4 OR t.carrier_service_id = ?5 OR t.carrier_service_id = 'ALL')",
      nativeQuery = true)
  List<TransitEntity> findByCarrierServiceIdsWithServiceOption(
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId1,
      String carrierServiceId2);

  @Query(
      value =
          "SELECT * FROM transit_data t WHERE t.org_id = ?1 AND t.destination_geozone = ?2  AND t.source_geozone IN ?3 ",
      nativeQuery = true)
  List<TransitEntity> findByOrgIdAndDestinationGeozoneAndSourceGeoZones(
      String orgId, String destinationGeozone, List<String> sourceGeozones);

  @Query(
      value =
          "SELECT DISTINCT destination_geozone FROM transit_data t WHERE t.org_id = ?1 AND t.source_geozone = ?2 AND t.carrier_service_id in ?3",
      nativeQuery = true)
  List<String> findByOrgIdAndSourceGeozoneAndCarrierServiceIds(
      String orgId, String sourceGeozone, List<String> carrierServiceId);

  @Query(
      value =
          "SELECT COUNT(*) FROM transit_data t WHERE t.org_id = ?1 AND t.carrier_service_id = ?2",
      nativeQuery = true)
  Integer findTransitCountByOrgIdAndCarrierServiceId(String orgId, String carrierServiceId);

  List<TransitEntity> findByOrgIdAndDestinationGeozone(String orgId, String destinationGeozone);

  List<ProjectedTransitEntity> findByOrgIdAndCarrierServiceIdAndDestinationGeozoneIn(
      String orgId, String carrierServiceId, List<String> destinationGeozones);

  @Query(
      value =
          "SELECT DISTINCT source_geozone FROM transit_data t WHERE t.org_id = ?1 AND t.carrier_service_id = ?2",
      nativeQuery = true)
  List<String> findDistinctSourceGeoZones(String orgId, String carrierServiceId);

  @Query(
      value =
          "SELECT DISTINCT destination_geozone FROM transit_data t WHERE t.org_id = ?1 AND t.carrier_service_id = ?2",
      nativeQuery = true)
  List<String> findDistinctDestinationGeoZones(String orgId, String carrierServiceId);
}
