package com.hbc.pe.masterdata.calendar.service;

import com.hbc.calendar.domain.inbound.NodeCalendarRequest;
import com.hbc.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.pe.masterdata.calendar.domain.NodeCalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.mapper.CalendarMapper;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import java.util.List;

import com.hbc.pe.masterdata.calendar.exception.DateException;
import com.hbc.pe.masterdata.calendar.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeCalendarService {

  private static final CalendarMapper INSTANCE = Mappers.getMapper(CalendarMapper.class);
  private final NodeCalendarDomain nodeCalendarDomain;

  /** Creates a new Node Calendar */
  public NodeCalendarResponse processCreateNodeCalendar(NodeCalendarRequest nodeCalendarRequest)
          throws CalendarDomainException, DateException {
    if(!DateUtil.validateDate(nodeCalendarRequest.getEffectiveDate())){
      throw new DateException(
              "Invalid Date",
              nodeCalendarRequest.getCalendarId(),
              nodeCalendarRequest.getOrgId()
      );
    }
    var nodeCalendarEntity = INSTANCE.convertToNodeCalendarEntity(nodeCalendarRequest);
    var savedNodeCalendarEntity = nodeCalendarDomain.saveNodeCalendarEntity(nodeCalendarEntity);
    return INSTANCE.convertToNodeCalendarResponse(savedNodeCalendarEntity);
  }

  /** Get Node Calendar details by calendarId and orgId */
  public List<NodeCalendarResponse> processGetNodeCalendar(String orgId, String nodeId)
      throws CalendarDomainException {
    return INSTANCE.convertToNodeCalendarResponseList(
        nodeCalendarDomain.getNodeCalendar(orgId, nodeId));
  }
}
