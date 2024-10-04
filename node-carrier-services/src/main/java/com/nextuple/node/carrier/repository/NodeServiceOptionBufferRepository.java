/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.repository;

import com.nextuple.node.carrier.domain.entity.NodeServiceOptionBufferEntity;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NodeServiceOptionBufferRepository
    extends JpaRepository<NodeServiceOptionBufferEntity, Long> {
  Optional<NodeServiceOptionBufferEntity> findByOrgIdAndId(String orgId, Long id);

  Optional<NodeServiceOptionBufferEntity>
      findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
          String orgId,
          String nodeId,
          String serviceOption,
          Date bufferStartDate,
          Date bufferEndDate);

  List<NodeServiceOptionBufferEntity> findByOrgIdAndNodeIdAndServiceOption(
      String orgId, String nodeId, String serviceOption);

  void deleteByOrgIdAndId(String orgId, Long id);

  @Query(
      value =
          "SELECT * from node_service_option_buffers tb where tb.org_id = ?1 AND tb.node_id = ?2 AND tb.service_option = ?3 AND tb.buffer_end_date >= ?4 AND tb.buffer_start_date <= ?5",
      nativeQuery = true)
  List<NodeServiceOptionBufferEntity> findApplicableBuffers(
      String orgId,
      String nodeId,
      String serviceOption,
      LocalDate requestDate,
      LocalDate requestDatePlusHorizon);
}
