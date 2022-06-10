package com.nextuple.pe.masterdata.calendar.domain;

import com.nextuple.pe.masterdata.calendar.domain.entity.NodeCarrierServiceCalendarEntity;
import com.nextuple.pe.masterdata.calendar.domain.repository.NodeCarrierServiceCalendarRepository;
import com.nextuple.pe.masterdata.calendar.exception.CalendarDomainException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeCarrierServiceCalendarDomain {
  private static final Logger logger =
      LoggerFactory.getLogger(NodeCarrierServiceCalendarDomain.class);
  private final NodeCarrierServiceCalendarRepository nodeCarrierServiceCalendarRepository;

  public NodeCarrierServiceCalendarEntity saveNodeCarrierServiceCalendarEntity(
      NodeCarrierServiceCalendarEntity nodeCarrierServiceCalendarEntity)
      throws CalendarDomainException {
    logger.debug("Inside saveCarrierServiceEntity()");
    try {
      return nodeCarrierServiceCalendarRepository.save(nodeCarrierServiceCalendarEntity);
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to create node carrier service calendar",
          e,
          nodeCarrierServiceCalendarEntity.getCalendarId(),
          nodeCarrierServiceCalendarEntity.getOrgId(),
          nodeCarrierServiceCalendarEntity.getNodeId(),
          nodeCarrierServiceCalendarEntity.getCarrierServiceId());
    }
  }

  public List<NodeCarrierServiceCalendarEntity> getNodeCarrierServiceCalendar(
      String orgId, String nodeId, String carrierServiceId) throws CalendarDomainException {
    try {
      return nodeCarrierServiceCalendarRepository.findAllNodeCarrierServiceCalendar(
          orgId, nodeId, carrierServiceId);
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch node carrier service calendar",
          e,
          null,
          orgId,
          nodeId,
          carrierServiceId);
    }
  }

  public List<NodeCarrierServiceCalendarEntity> getNodeCarrierServiceCalendar(
      String orgId, String nodeId, String carrierServiceId, String carrierServiceOption)
      throws CalendarDomainException {
    try {
      return nodeCarrierServiceCalendarRepository.findNodeCarrierServiceCalendar(
          orgId, nodeId, carrierServiceId, carrierServiceOption);
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch node carrier service calendar",
          e,
          null,
          orgId,
          nodeId,
          carrierServiceId);
    }
  }
}
