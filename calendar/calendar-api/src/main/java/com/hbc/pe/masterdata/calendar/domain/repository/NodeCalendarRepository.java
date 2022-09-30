package com.hbc.pe.masterdata.calendar.domain.repository;

import com.hbc.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeCalendarRepository extends JpaRepository<NodeCalendarEntity, String> {

  @Query(
      value =
          "SELECT * FROM node_calendars WHERE org_id = ?1 AND node_id = ?2 ORDER BY effective_date DESC, created_date DESC",
      nativeQuery = true)
  List<NodeCalendarEntity> findByOrgIdAndNodeId(String orgId, String nodeId);

  Optional<NodeCalendarEntity> findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(
      String calendarId, String nodeId, String orgId, String effectiveDate);

  @Query(value = "SELECT * FROM node_calendars LIMIT ?1", nativeQuery = true)
  List<NodeCalendarEntity> findAllNodeCalendarByLimit(Integer limit);
}
