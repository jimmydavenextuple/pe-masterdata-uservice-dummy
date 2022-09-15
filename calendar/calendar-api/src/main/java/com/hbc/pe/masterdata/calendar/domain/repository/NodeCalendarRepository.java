package com.hbc.pe.masterdata.calendar.domain.repository;

import com.hbc.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeCalendarRepository extends JpaRepository<NodeCalendarEntity, String> {
  List<NodeCalendarEntity> findByOrgIdAndNodeId(String orgId, String nodeId);

  @Query(value = "SELECT * FROM node_calendars LIMIT ?1", nativeQuery = true)
  List<NodeCalendarEntity> findAllNodeCalendarByLimit(Integer limit);
}
