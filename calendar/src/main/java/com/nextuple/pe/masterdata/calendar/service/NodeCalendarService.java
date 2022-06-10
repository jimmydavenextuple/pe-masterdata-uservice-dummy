package com.nextuple.pe.masterdata.calendar.service;

import com.nextuple.pe.masterdata.calendar.domain.NodeCalendarDomain;
import com.nextuple.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import com.nextuple.pe.masterdata.calendar.domain.inbound.NodeCalendarRequest;
import com.nextuple.pe.masterdata.calendar.domain.mapper.CalendarMapper;
import com.nextuple.pe.masterdata.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.pe.masterdata.calendar.exception.CalendarDomainException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeCalendarService {

  private static final CalendarMapper INSTANCE = Mappers.getMapper(CalendarMapper.class);
  private final NodeCalendarDomain nodeCalendarDomain;

  /** Creates a new Node Calendar */
  public NodeCalendarResponse processCreateNodeCalendar(NodeCalendarRequest nodeCalendarRequest)
      throws CalendarDomainException {
    NodeCalendarEntity nodeCalendarEntity =
        INSTANCE.convertToNodeCalendarEntity(nodeCalendarRequest);
    NodeCalendarEntity savedNodeCalendarEntity =
        nodeCalendarDomain.saveNodeCalendarEntity(nodeCalendarEntity);
    return INSTANCE.convertToNodeCalendarResponse(savedNodeCalendarEntity);
  }

  /** Get Node Calendar details by calendarId and orgId */
  public List<NodeCalendarResponse> processGetNodeCalendar(String orgId, String nodeId)
      throws CalendarDomainException {
    return INSTANCE.convertToNodeCalendarResponseList(
        nodeCalendarDomain.getNodeCalendar(orgId, nodeId));
  }
}
