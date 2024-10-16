/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.repository;

import com.nextuple.calendar.persistence.entity.NodeCalendarEntity;
import com.nextuple.calendar.persistence.entity.key.NodeCalendarKey;
import com.nextuple.postgres.repository.CommonJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeCalendarRepository
    extends CommonJpaRepository<NodeCalendarEntity, NodeCalendarKey> {

  @Query(
      value =
          "SELECT * FROM node_calendars WHERE org_id = ?1 AND node_id = ?2 ORDER BY effective_date DESC, created_date DESC",
      nativeQuery = true)
  List<NodeCalendarEntity> findByOrgIdAndNodeId(String orgId, String nodeId);

  Optional<NodeCalendarEntity> findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(
      String calendarId, String nodeId, String orgId, String effectiveDate);

  @Query(value = "SELECT * FROM node_calendars LIMIT ?1", nativeQuery = true)
  List<NodeCalendarEntity> findAllNodeCalendarByLimit(Integer limit);

  List<NodeCalendarEntity> findNodeCalendarByCalendarIdAndOrgId(String calendarId, String orgId);
}
