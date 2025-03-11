/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.common.base.PagePayload;
import com.nextuple.dataupload.domain.dto.NodeCarrierServiceResponse;
import com.nextuple.dataupload.domain.mapper.NodeCarrierServiceCalendarMapper;
import com.nextuple.dataupload.domain.pojo.PickUpCalendar;
import com.nextuple.node.carrier.domain.feign.INodeCarrierFeign;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class NodeCarrierServiceDetailsService {

  private final NodeFeign nodeFeign;
  private final INodeCarrierFeign nodeCarrierFeign;
  private final CalendarFeign calendarFeign;

  private final NodeCarrierServiceCalendarMapper mapper =
      Mappers.getMapper(NodeCarrierServiceCalendarMapper.class);

  public PagePayload<NodeCarrierServiceResponse> getNodeCarrierServiceDetails(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {

    PagePayload<NodeDto> nodeResponse =
        nodeFeign.getNodeList(orgId, pageNo, pageSize, sortBy, sortOrder).getPayload();

    List<NodeCarrierServiceResponse> nodeCarrierServiceResponses =
        nodeResponse.getData().stream().map(this::createNodeCarrierServiceResponse).toList();

    PagePayload<NodeCarrierServiceResponse> nodeCarrierServiceResponsePagePayload =
        new PagePayload<>();
    nodeCarrierServiceResponsePagePayload.setData(nodeCarrierServiceResponses);
    nodeCarrierServiceResponsePagePayload.setPagination(nodeResponse.getPagination());
    return nodeCarrierServiceResponsePagePayload;
  }

  private NodeCarrierServiceResponse createNodeCarrierServiceResponse(NodeDto nodeDto) {
    List<String> distinctCarrierServiceIds =
        nodeCarrierFeign
            .getUniqueNodeCarrierServiceList(nodeDto.getOrgId(), nodeDto.getNodeId())
            .getPayload();

    List<PickUpCalendar> pickUpCalendarList = new ArrayList<>();
    distinctCarrierServiceIds.stream()
        .filter(carrierServiceId -> !ObjectUtils.isEmpty(carrierServiceId))
        .forEach(
            carrierServiceId ->
                pickUpCalendarList.addAll(
                    mapper.convertToPickUpCalendarList(
                        Collections.singletonList(
                            calendarFeign
                                .getNodeCarrierServiceCalendar(
                                    nodeDto.getOrgId(), nodeDto.getNodeId(), carrierServiceId, null)
                                .getPayload()
                                .stream()
                                .max(
                                    Comparator.comparing(
                                        NodeCarrierServiceCalendarResponse::getEffectiveDate))
                                .orElse(null)))));
    pickUpCalendarList.removeIf(Objects::isNull);

    return getNodeCarrierServiceResponse(nodeDto, distinctCarrierServiceIds, pickUpCalendarList);
  }

  private static NodeCarrierServiceResponse getNodeCarrierServiceResponse(
      NodeDto nodeDto,
      List<String> distinctCarrierServiceIds,
      List<PickUpCalendar> pickUpCalendarList) {
    return NodeCarrierServiceResponse.builder()
        .nodeId(nodeDto.getNodeId())
        .orgId(nodeDto.getOrgId())
        .street(nodeDto.getStreet())
        .city(nodeDto.getCity())
        .state(nodeDto.getState())
        .zipCode(nodeDto.getZipCode())
        .nodeType(nodeDto.getNodeType())
        .carrierServices(distinctCarrierServiceIds)
        .pickupCalendar(pickUpCalendarList)
        .customAttributes(nodeDto.getCustomAttributes())
        .build();
  }
}
