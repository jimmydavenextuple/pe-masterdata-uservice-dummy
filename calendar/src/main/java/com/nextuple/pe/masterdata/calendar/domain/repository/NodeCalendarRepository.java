package com.nextuple.pe.masterdata.calendar.domain.repository;

import com.nextuple.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeCalendarRepository extends JpaRepository<NodeCalendarEntity, String> {
  List<NodeCalendarEntity> findByOrgIdAndNodeId(String orgId, String nodeId);
}
