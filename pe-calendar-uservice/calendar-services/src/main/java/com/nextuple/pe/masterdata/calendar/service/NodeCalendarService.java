/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.service;

import com.nextuple.calendar.domain.dto.NodeCalendarCacheKeyDto;
import com.nextuple.calendar.domain.inbound.NodeCalendarRequest;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.calendar.persistence.domain.NodeCalendarDomainDto;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.exception.DateException;
import com.nextuple.calendar.persistence.service.CalendarPersistenceService;
import com.nextuple.calendar.persistence.service.NodeCalendarPersistenceService;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.pe.masterdata.calendar.domain.mapper.CalendarMapper;
import com.nextuple.pe.masterdata.calendar.util.CalendarValidation;
import com.nextuple.pe.masterdata.calendar.util.DateValidation;
import com.nextuple.postgres.config.ReaderDS;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
  private final NodeCalendarPersistenceService nodeCalendarPersistenceService;
  private final DateValidation dateValidation;
  private final CalendarPersistenceService calendarPersistenceService;
  private final CalendarValidation calendarValidation;
  private static final String ORG_ID = "orgId";
  private static final String CALENDAR_ID = "calendarId";
  private static final String NODE_ID = "nodeId";
  private static final String EFFECTIVE_DATE = "effectiveDate";

  /** Creates a new Node Calendar */
  public NodeCalendarResponse processCreateNodeCalendar(NodeCalendarRequest nodeCalendarRequest)
      throws CalendarDomainException, CommonServiceException, DateException {
    if (!dateValidation.validateDate(nodeCalendarRequest.getEffectiveDate())) {
      throw new DateException(
          "Invalid Date", nodeCalendarRequest.getCalendarId(), nodeCalendarRequest.getOrgId());
    }
    validateCalendarId(nodeCalendarRequest.getCalendarId(), nodeCalendarRequest.getOrgId());
    calendarValidation.validateNodeId(
        nodeCalendarRequest.getNodeId(), nodeCalendarRequest.getOrgId());
    var nodeCalendarDomainDto = INSTANCE.convertToNodeCalendarDomainDto(nodeCalendarRequest);
    Optional<NodeCalendarDomainDto> existingNodeCalendarDomainDto =
        nodeCalendarPersistenceService.findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(
            nodeCalendarRequest.getCalendarId(),
            nodeCalendarRequest.getNodeId(),
            nodeCalendarRequest.getOrgId(),
            nodeCalendarRequest.getEffectiveDate());
    if (existingNodeCalendarDomainDto.isPresent()) {
      logger.error(
          "Node Calendar already exists for calendarId:{} , nodeId:{} , orgId:{}, effectiveDate:{}",
          nodeCalendarDomainDto.getCalendarId(),
          nodeCalendarDomainDto.getNodeId(),
          nodeCalendarDomainDto.getOrgId(),
          nodeCalendarDomainDto.getEffectiveDate());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          CALENDAR_ID,
          FieldError.builder().rejectedValue(nodeCalendarDomainDto.getCalendarId()).build());
      errorMap.put(
          NODE_ID, FieldError.builder().rejectedValue(nodeCalendarDomainDto.getNodeId()).build());
      errorMap.put(
          ORG_ID, FieldError.builder().rejectedValue(nodeCalendarDomainDto.getOrgId()).build());
      errorMap.put(
          EFFECTIVE_DATE,
          FieldError.builder().rejectedValue(nodeCalendarDomainDto.getEffectiveDate()).build());
      throw new CommonServiceException(
          "Node Calendar already exists for the given details",
          HttpStatus.BAD_REQUEST,
          0x1772,
          errorMap);
    }
    var savedNodeCalendarDomainDto =
        nodeCalendarPersistenceService.saveNodeCalendar(nodeCalendarDomainDto);
    return INSTANCE.convertToNodeCalendarResponse(savedNodeCalendarDomainDto);
  }

  /** Get Node Calendar details by calendarId and orgId */
  @ReaderDS
  public List<NodeCalendarResponse> processGetNodeCalendar(String orgId, String nodeId)
      throws CalendarDomainException {
    return INSTANCE.convertToNodeCalendarResponseList(
        nodeCalendarPersistenceService.getNodeCalendar(orgId, nodeId));
  }

  public void validateCalendarId(String calendarId, String orgId)
      throws CalendarDomainException, CommonServiceException {
    var calendarDomainDto = calendarPersistenceService.getCalendar(orgId, calendarId);

    if (ObjectUtils.isEmpty(calendarDomainDto)) {
      logger.error("Cannot create a node calendar as calendarId/orgId is invalid");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CALENDAR_ID, FieldError.builder().rejectedValue(calendarId).build());
      throw new CommonServiceException(
          "Cannot create a node calendar as calendarId/orgId is invalid",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  public List<NodeCalendarCacheKeyDto> getAllNodeCalendarCacheKeys(Integer limit)
      throws CalendarDomainException {
    var nodeCalendarDomainDtoList = nodeCalendarPersistenceService.getAllNodeCalendar(limit);

    return INSTANCE.convertToNodeCalendarCacheKeyDtoList(nodeCalendarDomainDtoList);
  }

  public List<NodeCalendarResponse> getNodeAssociationWithCalendar(String calendarId, String orgId)
      throws CalendarDomainException {
    var nodeCalendarDomainDtoList =
        nodeCalendarPersistenceService.getNodeServiceCalendarByOrgIdAndCalendarId(
            calendarId, orgId);

    return INSTANCE.convertToNodeCalendarResponseList(nodeCalendarDomainDtoList);
  }
}
