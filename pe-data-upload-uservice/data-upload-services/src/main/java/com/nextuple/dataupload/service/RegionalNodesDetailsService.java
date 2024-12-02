/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.NODE_DEFAULT_SORT_BY;
import static com.nextuple.dataupload.util.CommonDashboardUtil.fetchEligibleNodeServiceOption;
import static com.nextuple.dataupload.util.CommonDashboardUtil.fetchNodeProcessingTimeForEligibleServiceOptions;

import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.dataupload.domain.dto.NodeListDto;
import com.nextuple.dataupload.domain.dto.NodeWorkingCalendarDto;
import com.nextuple.dataupload.domain.dto.PickupTimeDto;
import com.nextuple.dataupload.domain.mapper.NodeMapper;
import com.nextuple.dataupload.domain.pojo.ProcessingTimeDetails;
import com.nextuple.node.carrier.domain.feign.INodeCarrierFeign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.postgres.config.ReaderDS;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionalNodesDetailsService {

  private final CalendarFeign calendarFeign;
  private final NodeFeign nodeFeign;
  @Autowired INodeCarrierFeign nodeCarrierFeign;

  private final PageProperties pageProperties;

  public static final NodeMapper INSTANCE = Mappers.getMapper(NodeMapper.class);

  @ReaderDS
  public PagePayload<NodeListDto> getNodesList(
      String orgId, String nodeIds, String nodeType, PageParams pageParams) {
    PagePayload<NodeListDto> nodeListDtoPagePayload = new PagePayload<>();
    List<NodeListDto> responseList = new ArrayList<>();
    Integer pageNo = pageParams.getPageNo().orElse(pageProperties.getPageNo());
    Integer pageSize = pageParams.getPageSize().orElse(pageProperties.getPageSize());
    String sortBy = pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY);
    String sortOrder = pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER);
    PagePayload<NodeDto> nodeResponse = null;
    if (nodeIds == null && nodeType == null) {
      nodeResponse = nodeFeign.getNodeList(orgId, pageNo, pageSize, sortBy, sortOrder).getPayload();
    } else {
      nodeResponse =
          nodeFeign
              .getNodeListV2(orgId, nodeIds, nodeType, pageNo, pageSize, sortBy, sortOrder)
              .getPayload();
    }

    List<NodeDto> nodeResponseList = nodeResponse.getData();
    for (NodeDto nodeServiceResponse : nodeResponseList) {
      List<NodeCalendarResponse> nodeCalendarResponseList = new ArrayList<>();
      try {
        nodeCalendarResponseList =
            calendarFeign
                .handleGetNodeCalendar(
                    nodeServiceResponse.getOrgId(), nodeServiceResponse.getNodeId())
                .getPayload();
      } catch (RuntimeException e) {
        log.error("Empty Node Calendar Response List");
      }
      List<NodeCarrierResponse> nodeCarrierResponses =
          nodeCarrierFeign
              .getNodeCarrierListWithLastPickUpTimeDetails(
                  nodeServiceResponse.getNodeId(), nodeServiceResponse.getOrgId())
              .getPayload();
      List<NodeCarrierResponse> nodeServiceOptionsResponse =
          nodeCarrierFeign
              .getNodeCarrierList(nodeServiceResponse.getNodeId(), nodeServiceResponse.getOrgId())
              .getPayload();

      CalendarResponse calendarDetails = null;
      if (!nodeCalendarResponseList.isEmpty()) {
        try {
          calendarDetails =
              calendarFeign
                  .getCalendar(
                      nodeCalendarResponseList.getFirst().getOrgId(),
                      nodeCalendarResponseList.getFirst().getCalendarId())
                  .getPayload();
        } catch (Exception e) {
          log.error("No node calendar associated to nodeId : {}", nodeServiceResponse.getNodeId());
        }
      }
      responseList.add(
          setNodeListDto(
              nodeServiceResponse,
              nodeCalendarResponseList,
              nodeCarrierResponses,
              nodeServiceOptionsResponse,
              calendarDetails));
    }
    nodeListDtoPagePayload.setPagination(nodeResponse.getPagination());
    nodeListDtoPagePayload.setData(responseList);
    return nodeListDtoPagePayload;
  }

  private NodeListDto setNodeListDto(
      NodeDto nodeResponse,
      List<NodeCalendarResponse> nodeCalendarResponseList,
      List<NodeCarrierResponse> nodeCarrierResponse,
      List<NodeCarrierResponse> nodeServiceOptionsResponse,
      CalendarResponse calendarDetails) {
    NodeListDto nodeListDto;
    nodeListDto = INSTANCE.toNodeListDto(nodeResponse);
    if (!nodeCalendarResponseList.isEmpty() && ObjectUtils.isNotEmpty(calendarDetails)) {
      nodeListDto.setNodeWorkingCalendar(
          setNodeCalendar(nodeCalendarResponseList, calendarDetails));
    }
    String[] validServiceOptions = fetchEligibleNodeServiceOption(nodeResponse);
    nodeListDto.setProcessingTimeDetails(
        getProcessingTimeDetails(nodeServiceOptionsResponse, validServiceOptions));
    nodeListDto.setServiceOptions(List.of(validServiceOptions));
    nodeListDto.setCarrierServices(getCarrierServiceIds(nodeCarrierResponse));
    nodeListDto.setPickupTime(getPickupTimeDetails(nodeCarrierResponse));
    return nodeListDto;
  }

  private List<PickupTimeDto> getPickupTimeDetails(
      List<NodeCarrierResponse> nodeCarrierResponseList) {
    Map<String, List<NodeCarrierServiceCalendarResponse>> nodeCalendarMap = new HashMap<>();

    Set<String> uniqueNodeIds =
        nodeCarrierResponseList.stream()
            .map(NodeCarrierResponse::getNodeId)
            .collect(Collectors.toSet());

    for (String nodeId : uniqueNodeIds) {
      List<NodeCarrierServiceCalendarResponse> nodeCarrierServiceCalendarResponses =
          nodeCarrierResponseList.stream()
              .filter(response -> response.getNodeId().equals(nodeId))
              .findFirst()
              .map(
                  matchingResponse ->
                      calendarFeign
                          .getNodeCarrierServiceCalendarForOrgIdAndNodeId(
                              matchingResponse.getOrgId(), nodeId)
                          .getPayload())
              .orElse(Collections.emptyList());

      nodeCalendarMap.put(nodeId, nodeCarrierServiceCalendarResponses);
    }

    List<PickupTimeDto> pickupTimeDtoList = new ArrayList<>();
    for (NodeCarrierResponse nodeCarrierResponse : nodeCarrierResponseList) {
      var pickupTimeDto = new PickupTimeDto();
      pickupTimeDto.setNodeId(nodeCarrierResponse.getNodeId());
      pickupTimeDto.setCarrierServiceId(nodeCarrierResponse.getCarrierServiceId());
      pickupTimeDto.setPickupTime(nodeCarrierResponse.getLastPickupTime());

      pickupTimeDto.setPickupCalendarId(
          nodeCalendarMap.get(nodeCarrierResponse.getNodeId()).stream()
              .filter(
                  calendar ->
                      calendar
                          .getCarrierServiceId()
                          .equals(nodeCarrierResponse.getCarrierServiceId()))
              .findFirst()
              .map(NodeCarrierServiceCalendarResponse::getCalendarId)
              .orElse("N/A"));

      pickupTimeDtoList.add(pickupTimeDto);
    }
    return pickupTimeDtoList;
  }

  private List<String> getCarrierServiceIds(List<NodeCarrierResponse> nodeCarrierResponse) {

    List<String> carrierServiceIds = new ArrayList<>();
    nodeCarrierResponse.forEach(
        nodeCarrier -> carrierServiceIds.add(nodeCarrier.getCarrierServiceId()));
    return carrierServiceIds.stream().distinct().toList();
  }

  private NodeWorkingCalendarDto setNodeCalendar(
      List<NodeCalendarResponse> nodeCalendarResponseList, CalendarResponse calendarResponse) {
    return NodeWorkingCalendarDto.builder()
        .effectiveDate(nodeCalendarResponseList.getFirst().getEffectiveDate())
        .calendarId(calendarResponse.getCalendarId())
        .description(calendarResponse.getDescription())
        .exceptionDays(calendarResponse.getExceptionDays())
        .isSundayWorking(calendarResponse.getIsSundayWorking())
        .isMondayWorking(calendarResponse.getIsMondayWorking())
        .isTuesdayWorking(calendarResponse.getIsTuesdayWorking())
        .isWednesdayWorking(calendarResponse.getIsWednesdayWorking())
        .isThursdayWorking(calendarResponse.getIsThursdayWorking())
        .isFridayWorking(calendarResponse.getIsFridayWorking())
        .isSaturdayWorking(calendarResponse.getIsSaturdayWorking())
        .build();
  }

  private List<ProcessingTimeDetails> getProcessingTimeDetails(
      List<NodeCarrierResponse> nodeCarrierResponse, String[] validServiceOptions) {
    List<ProcessingTimeDetails> processingTimeDetailsList = new ArrayList<>();
    Map<String, Double> validServiceOptionProcessingTime =
        fetchNodeProcessingTimeForEligibleServiceOptions(nodeCarrierResponse, validServiceOptions);
    validServiceOptionProcessingTime.forEach(
        (serviceOption, processingTime) ->
            processingTimeDetailsList.add(
                ProcessingTimeDetails.builder()
                    .serviceOption(serviceOption)
                    .processingTime(processingTime)
                    .build()));
    return processingTimeDetailsList;
  }
}
