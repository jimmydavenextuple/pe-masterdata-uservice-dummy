/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.repository;

import com.nextuple.postgres.repository.CommonJpaRepository;
import com.nextuple.transit.persistence.entity.TransitBufferV2Entity;
import com.nextuple.transit.persistence.entity.key.TransitBufferV2Key;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransitBufferV2Repository
    extends CommonJpaRepository<TransitBufferV2Entity, TransitBufferV2Key> {
  @Query(
      value =
          "SELECT * from transit_buffers_data tb where tb.org_id = ?1 AND tb.destination_geozone = ?2 AND tb.buffer_end_date >= ?3 AND tb.buffer_start_date <= ?4",
      nativeQuery = true)
  List<TransitBufferV2Entity> findApplicableBuffers(
      String orgId,
      String destinationGeozone,
      LocalDate requestDate,
      LocalDate requestDatePlusHorizon);

  List<TransitBufferV2Entity> findByOrgIdAndDestinationGeozone(
      String orgId, String destinationGeozone);

  Optional<TransitBufferV2Entity> findByOrgIdAndId(String orgId, Long id);

  List<TransitBufferV2Entity> findByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
      String orgId, String destinationGeozone, String sourceGeozone, String carrierServiceId);

  Optional<TransitBufferV2Entity>
      findByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndTransitBufferConfigRequestId(
          String orgId,
          String destinationGeozone,
          String sourceGeozone,
          String carrierServiceId,
          Long transitBufferConfigRequestId);

  void deleteByIdAndOrgId(Long id, String orgId);

  Optional<TransitBufferV2Entity>
      findByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndBufferStartDateAndBufferEndDate(
          String orgId,
          String destinationGeozone,
          String sourceGeozone,
          String carrierServiceId,
          Date bufferStartDate,
          Date bufferEndDate);

  List<TransitBufferV2Entity> findByTransitBufferConfigRequestId(Long transitBufferConfigRequestId);
}
