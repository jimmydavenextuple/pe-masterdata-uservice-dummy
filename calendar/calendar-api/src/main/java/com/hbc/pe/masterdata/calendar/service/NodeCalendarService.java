package com.hbc.pe.masterdata.calendar.service;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.ErrorType;
import com.hbc.common.response.error.FieldError;
import com.hbc.pe.masterdata.calendar.domain.CalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.NodeCalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.entity.CalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.entity.NodeCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.inbound.NodeCalendarRequest;
import com.hbc.pe.masterdata.calendar.domain.mapper.CalendarMapper;
import com.hbc.pe.masterdata.calendar.domain.outbound.CalendarResponse;
import com.hbc.pe.masterdata.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeCalendarService {

  private static final Logger logger = LoggerFactory.getLogger(NodeCalendarService.class);
  private static final CalendarMapper INSTANCE = Mappers.getMapper(CalendarMapper.class);
  private final NodeCalendarDomain nodeCalendarDomain;
  private static final String ORG_ID = "orgId";
  private static final String CALENDAR_ID = "calendarId";
  private static final String NODE_EXCEPTION_MESSAGE = "Node not found with given details";
  private final CalendarDomain calendarDomain;

  /** Creates a new Node Calendar */
  public NodeCalendarResponse processCreateNodeCalendar(NodeCalendarRequest nodeCalendarRequest)
      throws CalendarDomainException, CommonServiceException {
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

    if(calendarEntity == null){
      logger.info(NODE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CALENDAR_ID, FieldError.builder().rejectedValue(calendarId).build());
      throw new CommonServiceException(
              NODE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }
}
