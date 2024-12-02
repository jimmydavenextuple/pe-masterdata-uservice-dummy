/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.service;

import com.nextuple.calendar.domain.dto.NodeCarrierCalendarCacheKeyDto;
import com.nextuple.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.calendar.persistence.domain.NodeCarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.exception.DateException;
import com.nextuple.calendar.persistence.service.CalendarPersistenceService;
import com.nextuple.calendar.persistence.service.NodeCarrierServiceCalendarPersistenceService;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.pe.masterdata.calendar.domain.mapper.CalendarMapper;
import com.nextuple.pe.masterdata.calendar.util.CalendarValidation;
import com.nextuple.pe.masterdata.calendar.util.DateValidation;
import com.nextuple.postgres.config.ReaderDS;
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
  private final NodeCarrierServiceCalendarPersistenceService
      nodeCarrierServiceCalendarPersistenceService;
  private final CalendarPersistenceService calendarPersistenceService;
  private final DateValidation dateValidation;
  private final CalendarValidation calendarValidation;
  private static final String ORG_ID = "orgId";
  private static final String CALENDAR_ID = "calendarId";
  private static final String NODE_ID = "nodeId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";
  private static final String EFFECTIVE_DATE = "effectiveDate";

  /** Creates a new Node Carrier Service Calendar */
  public NodeCarrierServiceCalendarResponse processCreateNodeCarrierServiceCalendarResponse(
      NodeCarrierServiceCalendarRequest nodeCarrierServiceCalendarRequest)
      throws CalendarDomainException, DateException, CommonServiceException {
    validateEffectiveDate(
        nodeCarrierServiceCalendarRequest.getEffectiveDate(),
        nodeCarrierServiceCalendarRequest.getOrgId(),
        nodeCarrierServiceCalendarRequest.getCalendarId());
    validateCalendarId(
        nodeCarrierServiceCalendarRequest.getCalendarId(),
        nodeCarrierServiceCalendarRequest.getOrgId());

    calendarValidation.validateNodeId(
        nodeCarrierServiceCalendarRequest.getNodeId(),
        nodeCarrierServiceCalendarRequest.getOrgId());
    calendarValidation.validateCarrierServiceId(
        nodeCarrierServiceCalendarRequest.getOrgId(),
        nodeCarrierServiceCalendarRequest.getCarrierServiceId());

    var nodeCarrierServiceCalendarDomainDto =
        INSTANCE.convertToNodeCarrierServiceCalendarDomainDto(nodeCarrierServiceCalendarRequest);
    Optional<NodeCarrierServiceCalendarDomainDto> existingNodeCarrierServiceCalendarDomainDto =
        nodeCarrierServiceCalendarPersistenceService
            .findByCalendarIdAndOrgIdAndNodeIdAndCarrierServiceIdAndEffectiveDate(
                nodeCarrierServiceCalendarRequest.getCalendarId(),
                nodeCarrierServiceCalendarRequest.getOrgId(),
                nodeCarrierServiceCalendarRequest.getNodeId(),
                nodeCarrierServiceCalendarRequest.getCarrierServiceId(),
                nodeCarrierServiceCalendarRequest.getEffectiveDate());
    checkNodeCarrierServiceCalendarIsAlreadyPresent(
        nodeCarrierServiceCalendarDomainDto, existingNodeCarrierServiceCalendarDomainDto);
    var savedNodeCarrierServiceCalendarDomainDto =
        nodeCarrierServiceCalendarPersistenceService.saveNodeCarrierServiceCalendar(
            nodeCarrierServiceCalendarDomainDto);
    return INSTANCE.convertToNodeCarrierServiceCalendarResponse(
        savedNodeCarrierServiceCalendarDomainDto);
  }

  private void validateEffectiveDate(String effectiveDate, String orgId, String calendarId)
      throws DateException {
    if (!dateValidation.validateDate(effectiveDate)) {
      throw new DateException("Invalid Date", calendarId, orgId);
    }
  }

  private static void checkNodeCarrierServiceCalendarIsAlreadyPresent(
      NodeCarrierServiceCalendarDomainDto nodeCarrierServiceCalendarDomainDto,
      Optional<NodeCarrierServiceCalendarDomainDto> existingNodeCarrierServiceCalendarDomainDto)
      throws CommonServiceException {
    if (existingNodeCarrierServiceCalendarDomainDto.isPresent()) {
      logger.error(
          "Node Carrier Service Calendar already exists for calendarId:{}, orgId:{}, nodeId:{}, carrierServiceId:{}, effectiveDate:{}",
          nodeCarrierServiceCalendarDomainDto.getCalendarId(),
          nodeCarrierServiceCalendarDomainDto.getOrgId(),
          nodeCarrierServiceCalendarDomainDto.getNodeId(),
          nodeCarrierServiceCalendarDomainDto.getCarrierServiceId(),
          nodeCarrierServiceCalendarDomainDto.getEffectiveDate());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          CALENDAR_ID,
          FieldError.builder()
              .rejectedValue(nodeCarrierServiceCalendarDomainDto.getCalendarId())
              .build());
      errorMap.put(
          ORG_ID,
          FieldError.builder()
              .rejectedValue(nodeCarrierServiceCalendarDomainDto.getOrgId())
              .build());
      errorMap.put(
          NODE_ID,
          FieldError.builder()
              .rejectedValue(nodeCarrierServiceCalendarDomainDto.getNodeId())
              .build());
      errorMap.put(
          CARRIER_SERVICE_ID,
          FieldError.builder()
              .rejectedValue(nodeCarrierServiceCalendarDomainDto.getNodeId())
              .build());
      errorMap.put(
          EFFECTIVE_DATE,
          FieldError.builder()
              .rejectedValue(nodeCarrierServiceCalendarDomainDto.getEffectiveDate())
              .build());
      throw new CommonServiceException(
          "Node Carrier Service Calendar already exists for the given details",
          HttpStatus.BAD_REQUEST,
          0x1772,
          errorMap);
    }
  }

  /** Get Node, Carrier&Service Calendar details by orgId, nodeId and carrierServiceId */
  @ReaderDS
  public List<NodeCarrierServiceCalendarResponse> processGetNodeCarrierServiceCalendar(
      String orgId, String nodeId, String carrierServiceId, Optional<String> serviceOption)
      throws CalendarDomainException {
    return INSTANCE.convertToNodeCarrierServiceCalendarResponseList(
        getAndFilterNodeCarrierServiceCalendar(orgId, nodeId, carrierServiceId, serviceOption));
  }

  public List<NodeCarrierServiceCalendarResponse> processGetNodeCarrierServiceCalendarByNodeId(
      String orgId, String nodeId) throws CalendarDomainException {
    return INSTANCE.convertToNodeCarrierServiceCalendarResponseList(
        getNodeCarrierServiceCalendarForOrgAndNodeId(orgId, nodeId));
  }

  private List<NodeCarrierServiceCalendarDomainDto> getNodeCarrierServiceCalendarForOrgAndNodeId(
      String orgId, String nodeId) throws CalendarDomainException {
    try {
      return nodeCarrierServiceCalendarPersistenceService.getNodeCarrierServiceCalendar(
          orgId, nodeId);
    } catch (Exception e) {
      logger.error("Error in getNodeCarrierServiceCalendarForOrgAndNodeId");
      throw e;
    }
  }

  public List<NodeCarrierServiceCalendarDomainDto> getAndFilterNodeCarrierServiceCalendar(
      String orgId, String nodeId, String carrierServiceId, Optional<String> serviceOption)
      throws CalendarDomainException {
    List<NodeCarrierServiceCalendarDomainDto> domainDtoList;
    List<NodeCarrierServiceCalendarDomainDto> filteredList;
    try {
      if (serviceOption.isPresent() && !ObjectUtils.isEmpty(serviceOption.get())) {
        String carrierServiceOption = "ALL-" + serviceOption.get();
        domainDtoList =
            nodeCarrierServiceCalendarPersistenceService.getNodeCarrierServiceCalendar(
                orgId, nodeId, carrierServiceId, carrierServiceOption);
        filteredList =
            "ALL".equals(carrierServiceId)
                ? new ArrayList<>()
                : getFilteredDomainDtoList(carrierServiceId, domainDtoList);
        if (CollectionUtils.isEmpty(filteredList)) {
          filteredList = getFilteredDomainDtoList(carrierServiceOption, domainDtoList);
        }
      } else {
        domainDtoList =
            nodeCarrierServiceCalendarPersistenceService.getNodeCarrierServiceCalendar(
                orgId, nodeId, carrierServiceId);
        filteredList = getFilteredDomainDtoList(carrierServiceId, domainDtoList);
      }
      return CollectionUtils.isEmpty(filteredList) ? domainDtoList : filteredList;
    } catch (Exception e) {
      logger.error("Error in getAndFilterNodeCarrierServiceCalendar");
      throw e;
    }
  }

  private List<NodeCarrierServiceCalendarDomainDto> getFilteredDomainDtoList(
      String carrierServiceId, List<NodeCarrierServiceCalendarDomainDto> domainDtoList) {
    return domainDtoList.stream()
        .filter(x -> carrierServiceId.equals(x.getCarrierServiceId()))
        .collect(Collectors.toList());
  }

  public void validateCalendarId(String calendarId, String orgId)
      throws CalendarDomainException, CommonServiceException {
    var calendarDomainDto = calendarPersistenceService.getCalendar(orgId, calendarId);

    if (ObjectUtils.isEmpty(calendarDomainDto)) {
      logger.error("Cannot create a node carrier service calendar as calendarId/orgId is invalid");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CALENDAR_ID, FieldError.builder().rejectedValue(calendarId).build());
      throw new CommonServiceException(
          "Cannot create a node carrier service calendar as calendarId/orgId is invalid",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  public List<NodeCarrierCalendarCacheKeyDto> getAllNodeCarrierCalendarCacheKeys(Integer limit)
      throws CalendarDomainException {
    List<NodeCarrierServiceCalendarDomainDto> nodeCarrierServiceCalendarDomainDtos =
        nodeCarrierServiceCalendarPersistenceService.getAllNodeCarrierServiceCalendars(limit);

    return INSTANCE.convertToNodeCarrierCalendarCacheKeyDtoList(
        nodeCarrierServiceCalendarDomainDtos);
  }

  public List<NodeCarrierServiceCalendarResponse> processGetAllNodeCarrierServiceCalendar(
      String orgId) throws CalendarDomainException {
    return INSTANCE.convertToNodeCarrierServiceCalendarResponseList(
        nodeCarrierServiceCalendarPersistenceService.getAllNodeCarrierServiceCalendarsByOrgId(
            orgId));
  }
}
