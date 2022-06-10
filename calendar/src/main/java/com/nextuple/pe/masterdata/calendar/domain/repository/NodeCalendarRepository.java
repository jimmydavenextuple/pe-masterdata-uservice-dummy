package com.nextuple.pe.masterdata.calendar.domain.repository;

import com.nextuple.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeCalendarRepository extends JpaRepository<NodeCalendarEntity, String> {
  List<NodeCalendarEntity> findByOrgIdAndNodeId(String orgId, String nodeId);
}
