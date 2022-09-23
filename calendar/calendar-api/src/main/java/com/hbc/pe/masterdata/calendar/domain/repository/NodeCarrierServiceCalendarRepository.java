package com.hbc.pe.masterdata.calendar.domain.repository;

import com.hbc.pe.masterdata.calendar.domain.entity.NodeCarrierServiceCalendarEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeCarrierServiceCalendarRepository
    extends JpaRepository<NodeCarrierServiceCalendarEntity, String> {

  @Query(
      value =
          "SELECT * FROM node_carrier_service_calendars "
              + "where org_id = ?1 "
              + "AND node_id = ?2 "
              + "AND (carrier_service_id = ?3 OR carrier_service_id = 'ALL' )",
      nativeQuery = true)
  List<NodeCarrierServiceCalendarEntity> findAllNodeCarrierServiceCalendar(
      String orgId, String nodeId, String carrierServiceId);

  @Query(
      value =
          "SELECT * FROM node_carrier_service_calendars "
              + "where org_id = ?1 "
              + "AND node_id = ?2 "
              + "AND (carrier_service_id = ?3 OR carrier_service_id = ?4 OR carrier_service_id = 'ALL' )",
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
}
