/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.repository;

import com.nextuple.calendar.persistence.entity.NodeCarrierServiceCalendarEntity;
import com.nextuple.calendar.persistence.entity.key.NodeCarrierServiceCalendarKey;
import com.nextuple.postgres.repository.CommonJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeCarrierServiceCalendarRepository
    extends CommonJpaRepository<NodeCarrierServiceCalendarEntity, NodeCarrierServiceCalendarKey> {

  @Query(
      value =
          "SELECT * FROM node_carrier_service_calendars "
              + "where org_id = ?1 "
              + "AND node_id = ?2 "
              + "AND (carrier_service_id = ?3 OR carrier_service_id = 'ALL' ) ORDER BY effective_date DESC, created_date DESC",
      nativeQuery = true)
  List<NodeCarrierServiceCalendarEntity> findAllNodeCarrierServiceCalendar(
      String orgId, String nodeId, String carrierServiceId);

  @Query(
      value =
          "SELECT * FROM node_carrier_service_calendars "
              + "where org_id = ?1 "
              + "AND node_id = ?2 "
              + "AND (carrier_service_id = ?3 OR carrier_service_id = ?4 OR carrier_service_id = 'ALL' ) ORDER BY effective_date DESC, created_date DESC",
      nativeQuery = true)
  List<NodeCarrierServiceCalendarEntity> findNodeCarrierServiceCalendar(
      String orgId, String nodeId, String carrierServiceId, String carrierServiceOption);

  Optional<NodeCarrierServiceCalendarEntity>
      findByCalendarIdAndOrgIdAndNodeIdAndCarrierServiceIdAndEffectiveDate(
          String calendarId,
          String orgId,
          String nodeId,
          String carrierServiceId,
          String effectiveDate);

  @Query(value = "SELECT * FROM node_carrier_service_calendars LIMIT ?1", nativeQuery = true)
  List<NodeCarrierServiceCalendarEntity> findAllNodeCarrierServiceCalendars(Integer limit);

  @Query(
      value =
          "SELECT * FROM node_carrier_service_calendars WHERE org_id = ?1 ORDER BY created_date DESC",
      nativeQuery = true)
  List<NodeCarrierServiceCalendarEntity> findAllNodeCarrierServiceCalendarsByOrgId(String orgId);

  List<NodeCarrierServiceCalendarEntity> findAllNodeCarrierServiceCalendarsByOrgIdAndNodeId(
      String orgId, String nodeId);

  List<NodeCarrierServiceCalendarEntity> deleteAllNodeCarrierServiceCalendarsByOrgIdAndNodeId(
      String orgId, String nodeId);

  @Query(
      value =
          """
        SELECT *
        FROM node_carrier_service_calendars
        WHERE org_id = ?1
        AND node_id = ?2
        ORDER BY effective_date DESC, created_date DESC
        """,
      nativeQuery = true)
  List<NodeCarrierServiceCalendarEntity>
      findNodeCarrierServiceCalendarByOrgIdAndNodeIdForDistCarrierServiceId(
          String orgId, String nodeId);
}
