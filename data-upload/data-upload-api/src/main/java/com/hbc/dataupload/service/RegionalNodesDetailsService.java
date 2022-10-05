package com.hbc.dataupload.service;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.common.base.PagePayload;
import com.hbc.dataupload.domain.dto.NodeListDto;
import com.hbc.dataupload.domain.dto.NodeWorkingCalendarDto;
import com.hbc.dataupload.domain.dto.PickupTimeDto;
import com.hbc.dataupload.domain.mapper.NodeMapper;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.domain.dto.NodeDto;
import com.hbc.node.domain.feign.NodeFeign;
import com.hbc.postgres.config.ReaderDS;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionalNodesDetailsService {

  private final CalendarFeign calendarFeign;
  private final NodeFeign nodeFeign;
  private final NodeCarrierFeign nodeCarrierFeign;

  public static final NodeMapper INSTANCE = Mappers.getMapper(NodeMapper.class);

  @ReaderDS
  public PagePayload<NodeListDto> getNodesList(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {
    PagePayload<NodeListDto> nodeListDtoPagePayload = new PagePayload<>();
    List<NodeListDto> responseList = new ArrayList<>();

    PagePayload<NodeDto> nodeResponse =
        nodeFeign.getNodeList(orgId, pageNo, pageSize, sortBy, sortOrder).getPayload();

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

      List<NodeCarrierResponse> nodeCarrierResponse =
          nodeCarrierFeign
              .getNodeCarrierListWithLastPickUpTimeDetails(
                  nodeServiceResponse.getNodeId(), nodeServiceResponse.getOrgId())
              .getPayload();

      responseList.add(
          setNodeListDto(nodeServiceResponse, nodeCalendarResponseList, nodeCarrierResponse));
    }
    nodeListDtoPagePayload.setPagination(nodeResponse.getPagination());
    nodeListDtoPagePayload.setData(responseList);
    return nodeListDtoPagePayload;
  }

  private NodeListDto setNodeListDto(
      NodeDto nodeResponse,
      List<NodeCalendarResponse> nodeCalendarResponseList,
      List<NodeCarrierResponse> nodeCarrierResponse) {
    NodeListDto nodeListDto;
    nodeListDto = INSTANCE.toNodeListDto(nodeResponse);
    nodeListDto.setNodeWorkingCalendar(setNodeCalendar(nodeCalendarResponseList));
    nodeListDto.setServiceOptions(getServiceOptions(nodeCarrierResponse));
    nodeListDto.setCarrierServices(getCarrierServiceIds(nodeCarrierResponse));
    nodeListDto.setPickupTime(getPickupTimeDetails(nodeCarrierResponse));
    return nodeListDto;
  }

  private List<PickupTimeDto> getPickupTimeDetails(
      List<NodeCarrierResponse> nodeCarrierResponseList) {

    List<PickupTimeDto> pickupTimeDtoList = new ArrayList<>();
    for (NodeCarrierResponse nodeCarrierResponse : nodeCarrierResponseList) {
      PickupTimeDto pickupTimeDto = new PickupTimeDto();
      pickupTimeDto.setNodeId(nodeCarrierResponse.getNodeId());
      pickupTimeDto.setCarrierServiceId(nodeCarrierResponse.getCarrierServiceId());
      pickupTimeDto.setPickupTime(nodeCarrierResponse.getLastPickupTime());
      pickupTimeDtoList.add(pickupTimeDto);
    }
    return pickupTimeDtoList;
  }

  private List<String> getCarrierServiceIds(List<NodeCarrierResponse> nodeCarrierResponse) {

    List<String> carrierServiceIds = new ArrayList<>();
    nodeCarrierResponse.forEach(
        nodeCarrier -> carrierServiceIds.add(nodeCarrier.getCarrierServiceId()));
    return carrierServiceIds.stream().distinct().collect(Collectors.toList());
  }

  private List<NodeWorkingCalendarDto> setNodeCalendar(
      List<NodeCalendarResponse> nodeCalendarResponseList) {

    List<NodeWorkingCalendarDto> nodeWorkingCalendarDtoList = new ArrayList<>();
    for (NodeCalendarResponse nodeCalendarResponse : nodeCalendarResponseList) {
      NodeWorkingCalendarDto nodeWorkingCalendarDto = new NodeWorkingCalendarDto();
      nodeWorkingCalendarDto.setCalendarId(nodeCalendarResponse.getCalendarId());
      nodeWorkingCalendarDto.setEffectiveDate(nodeCalendarResponse.getEffectiveDate());
      nodeWorkingCalendarDtoList.add(nodeWorkingCalendarDto);
    }
    return nodeWorkingCalendarDtoList;
  }

  private List<String> getServiceOptions(List<NodeCarrierResponse> nodeCarrierResponse) {
    List<String> serviceOptions = new ArrayList<>();
    nodeCarrierResponse.forEach(nodeCarrier -> serviceOptions.add(nodeCarrier.getServiceOption()));
    return serviceOptions.stream().distinct().collect(Collectors.toList());
  }
}
