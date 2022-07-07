package com.hbc.pe.masterdata.calendar.service;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.ErrorType;
import com.hbc.pe.masterdata.calendar.domain.CalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.NodeCalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.entity.CalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.inbound.NodeCalendarRequest;
import com.hbc.pe.masterdata.calendar.domain.mapper.CalendarMapper;
import com.hbc.pe.masterdata.calendar.domain.outbound.CalendarResponse;
import com.hbc.pe.masterdata.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeCalendarService {

  private static final CalendarMapper INSTANCE = Mappers.getMapper(CalendarMapper.class);
  private final NodeCalendarDomain nodeCalendarDomain;

  private final CalendarDomain calendarDomain;

  /** Creates a new Node Calendar */
  public NodeCalendarResponse processCreateNodeCalendar(NodeCalendarRequest nodeCalendarRequest)
      throws CalendarDomainException {
    validateCalendarId(nodeCalendarRequest.getCalendarId(), nodeCalendarRequest.getOrgId());

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

  private void validateCalendarId(String calendarId, String orgId)
          throws CalendarDomainException, CommonServiceException {
     CalendarEntity calendarEntity = calendarDomain.getCalendar(orgId, calendarId);

    if(){
      throw new CalendarDomainException(
              "Cannot create a node calendar as the global calendar does not exists for the given calendarId and OrdId", null, calendarId, orgId, null, null);
    }
  }
}
