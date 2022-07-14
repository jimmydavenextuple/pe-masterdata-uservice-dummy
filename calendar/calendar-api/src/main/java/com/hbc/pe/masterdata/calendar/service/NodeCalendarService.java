package com.hbc.pe.masterdata.calendar.service;

import com.hbc.calendar.domain.inbound.NodeCalendarRequest;
import com.hbc.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.pe.masterdata.calendar.domain.CalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.NodeCalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.entity.CalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.mapper.CalendarMapper;
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
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class NodeCalendarService {
  private static final CalendarMapper INSTANCE = Mappers.getMapper(CalendarMapper.class);
  private static final Logger logger = LoggerFactory.getLogger(NodeCalendarService.class);
  private final NodeCalendarDomain nodeCalendarDomain;
  private final CalendarDomain calendarDomain;
  private static final String ORG_ID = "orgId";
  private static final String CALENDAR_ID = "calendarId";

  /** Creates a new Node Calendar */
  public NodeCalendarResponse processCreateNodeCalendar(NodeCalendarRequest nodeCalendarRequest)
      throws CalendarDomainException, CommonServiceException {
    validateCalendarId(nodeCalendarRequest.getCalendarId(), nodeCalendarRequest.getOrgId());
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

  public void validateCalendarId(String calendarId, String orgId)
      throws CalendarDomainException, CommonServiceException {
    CalendarEntity calendarEntity = calendarDomain.getCalendar(orgId, calendarId);

    if (ObjectUtils.isEmpty(calendarEntity)) {
      logger.info("Cannot create a node calendar as calendarId/orgId is invalid");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CALENDAR_ID, FieldError.builder().rejectedValue(calendarId).build());
      throw new CommonServiceException(
          "Cannot create a node calendar as calendarId/orgId is invalid",
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
  }
}
