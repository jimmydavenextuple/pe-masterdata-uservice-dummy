package com.hbc.pe.masterdata.calendar.service;

import com.hbc.calendar.domain.dto.NodeCarrierCalendarCacheKeyDto;
import com.hbc.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.hbc.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.pe.masterdata.calendar.domain.CalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.NodeCarrierServiceCalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.entity.NodeCarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.mapper.CalendarMapper;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.exception.DateException;
import com.hbc.pe.masterdata.calendar.util.DateValidation;
import com.hbc.postgres.config.ReaderDS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class NodeCarrierServiceCalendarService {

  private static final Logger logger =
      LoggerFactory.getLogger(NodeCarrierServiceCalendarService.class);
  private static final CalendarMapper INSTANCE = Mappers.getMapper(CalendarMapper.class);
  private final NodeCarrierServiceCalendarDomain nodeCarrierServiceCalendarDomain;
  private final CalendarDomain calendarDomain;
  private final DateValidation dateValidation;
  private static final String ORG_ID = "orgId";
  private static final String CALENDAR_ID = "calendarId";

  /** Creates a new Node Carrier Service Calendar */
  public NodeCarrierServiceCalendarResponse processCreateNodeCarrierServiceCalendarResponse(
      NodeCarrierServiceCalendarRequest nodeCarrierServiceCalendarRequest)
      throws CalendarDomainException, DateException, CommonServiceException {
    if (!dateValidation.validateDate(nodeCarrierServiceCalendarRequest.getEffectiveDate())) {
      throw new DateException(
          "Invalid Date",
          nodeCarrierServiceCalendarRequest.getCalendarId(),
          nodeCarrierServiceCalendarRequest.getOrgId());
    }
    validateCalendarId(
        nodeCarrierServiceCalendarRequest.getCalendarId(),
        nodeCarrierServiceCalendarRequest.getOrgId());
    var nodeCarrierServiceCalendarEntity =
        INSTANCE.convertToNodeCarrierServiceCalendarEntity(nodeCarrierServiceCalendarRequest);
    var savedNodeCarrierServiceCalendarEntity =
        nodeCarrierServiceCalendarDomain.saveNodeCarrierServiceCalendarEntity(
            nodeCarrierServiceCalendarEntity);
    return INSTANCE.convertToNodeCarrierServiceCalendarResponse(
        savedNodeCarrierServiceCalendarEntity);
  }

  /** Get Node, Carrier&Service Calendar details by orgId, nodeId and carrierServiceId */
  @ReaderDS
  public List<NodeCarrierServiceCalendarResponse> processGetNodeCarrierServiceCalendar(
      String orgId, String nodeId, String carrierServiceId, Optional<String> serviceOption)
      throws CalendarDomainException {
    return INSTANCE.convertToNodeCarrierServiceCalendarResponseList(
        getAndFilterNodeCarrierServiceCalendar(orgId, nodeId, carrierServiceId, serviceOption));
  }

  public List<NodeCarrierServiceCalendarEntity> getAndFilterNodeCarrierServiceCalendar(
      String orgId, String nodeId, String carrierServiceId, Optional<String> serviceOption)
      throws CalendarDomainException {
    List<NodeCarrierServiceCalendarEntity> entityList;
    List<NodeCarrierServiceCalendarEntity> filteredList;
    try {
      if (serviceOption.isPresent() && !ObjectUtils.isEmpty(serviceOption.get())) {
        String carrierServiceOption = "ALL-" + serviceOption.get();
        entityList =
            nodeCarrierServiceCalendarDomain.getNodeCarrierServiceCalendar(
                orgId, nodeId, carrierServiceId, carrierServiceOption);
        filteredList =
            "ALL".equals(carrierServiceId)
                ? new ArrayList<>()
                : getFilteredEntityList(carrierServiceId, entityList);
        if (CollectionUtils.isEmpty(filteredList)) {
          filteredList = getFilteredEntityList(carrierServiceOption, entityList);
        }
      } else {
        entityList =
            nodeCarrierServiceCalendarDomain.getNodeCarrierServiceCalendar(
                orgId, nodeId, carrierServiceId);
        filteredList = getFilteredEntityList(carrierServiceId, entityList);
      }
      return CollectionUtils.isEmpty(filteredList) ? entityList : filteredList;
    } catch (Exception e) {
      logger.error("Error in getAndFilterNodeCarrierServiceCalendar");
      throw e;
    }
  }

  private List<NodeCarrierServiceCalendarEntity> getFilteredEntityList(
      String carrierServiceId, List<NodeCarrierServiceCalendarEntity> entityList) {
    return entityList.stream()
        .filter(x -> carrierServiceId.equals(x.getCarrierServiceId()))
        .collect(Collectors.toList());
  }

  public void validateCalendarId(String calendarId, String orgId)
      throws CalendarDomainException, CommonServiceException {
    var calendarEntity = calendarDomain.getCalendar(orgId, calendarId);

    if (ObjectUtils.isEmpty(calendarEntity)) {
      logger.error("Cannot create a node carrier service calendar as calendarId/orgId is invalid");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CALENDAR_ID, FieldError.builder().rejectedValue(calendarId).build());
      throw new CommonServiceException(
          "Cannot create a node carrier service calendar as calendarId/orgId is invalid",
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
  }

  public List<NodeCarrierCalendarCacheKeyDto> getAllNodeCarrierCalendarCacheKeys(Integer limit)
      throws CalendarDomainException {
    List<NodeCarrierServiceCalendarEntity> nodeCarrierServiceCalendarEntities =
        nodeCarrierServiceCalendarDomain.getAllNodeCarrierServiceCalendars(limit);

    return INSTANCE.convertToNodeCarrierCalendarCacheKeyDtoList(nodeCarrierServiceCalendarEntities);
  }
}
