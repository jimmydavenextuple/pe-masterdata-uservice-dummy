package com.nextuple.pe.masterdata.calendar.domain;

import com.nextuple.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import com.nextuple.pe.masterdata.calendar.domain.repository.NodeCalendarRepository;
import com.nextuple.pe.masterdata.calendar.exception.CalendarDomainException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeCalendarDomain {
  private static final Logger logger = LoggerFactory.getLogger(NodeCalendarDomain.class);
  private final NodeCalendarRepository nodeCalendarRepository;

  public NodeCalendarEntity saveNodeCalendarEntity(NodeCalendarEntity nodeCalendarEntity)
      throws CalendarDomainException {
    logger.debug("Inside saveNodeCalendarEntity()");
    try {
      return nodeCalendarRepository.save(nodeCalendarEntity);
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to create node calendar",
          e,
          nodeCalendarEntity.getCalendarId(),
          nodeCalendarEntity.getOrgId(),
          nodeCalendarEntity.getNodeId(),
          null);
    }
  }

  public List<NodeCalendarEntity> getNodeCalendar(String orgId, String nodeId)
      throws CalendarDomainException {
    logger.debug("Inside getNodeCalendarByNodeId()");
    try {
      return nodeCalendarRepository.findByOrgIdAndNodeId(orgId, nodeId);
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch node calendar", e, null, orgId, nodeId, null);
    }
  }
}
