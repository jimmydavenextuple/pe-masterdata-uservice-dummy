package com.hbc.dataupload.service;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.common.base.PagePayload;
import com.hbc.dataupload.domain.dto.NodeCarrierServiceResponse;
import com.hbc.dataupload.domain.mapper.NodeCarrierServiceCalendarMapper;
import com.hbc.dataupload.domain.pojo.PickUpCalendar;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.domain.dto.NodeDto;
import com.hbc.node.domain.feign.NodeFeign;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class NodeCarrierServiceDetailsService {

  private final NodeFeign nodeFeign;
  private final NodeCarrierFeign nodeCarrierFeign;
  private final CalendarFeign calendarFeign;

  private final NodeCarrierServiceCalendarMapper mapper =
      Mappers.getMapper(NodeCarrierServiceCalendarMapper.class);

  public PagePayload<NodeCarrierServiceResponse> getNodeCarrierServiceDetails(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {

    PagePayload<NodeDto> nodeResponse =
        nodeFeign.getNodeList(orgId, pageNo, pageSize, sortBy, sortOrder).getPayload();

    List<NodeCarrierServiceResponse> nodeCarrierServiceResponses =
        nodeResponse.getData().stream()
            .map(this::createNodeCarrierServiceResponse)
            .collect(Collectors.toList());

    PagePayload<NodeCarrierServiceResponse> nodeCarrierServiceResponsePagePayload =
        new PagePayload<>();
    nodeCarrierServiceResponsePagePayload.setData(nodeCarrierServiceResponses);
    nodeCarrierServiceResponsePagePayload.setPagination(nodeResponse.getPagination());
    return nodeCarrierServiceResponsePagePayload;
  }

  private NodeCarrierServiceResponse createNodeCarrierServiceResponse(NodeDto nodeDto) {
    List<String> distinctCarrierServiceIds =
        nodeCarrierFeign
            .getUniqueNodeCarrierServiceList(nodeDto.getNodeId(), nodeDto.getOrgId())
            .getPayload();

    List<PickUpCalendar> pickUpCalendarList = new ArrayList<>();
    distinctCarrierServiceIds.stream()
        .filter(carrierServiceId -> !ObjectUtils.isEmpty(carrierServiceId))
        .forEach(
            carrierServiceId ->
                pickUpCalendarList.addAll(
                    mapper.convertToPickUpCalendarList(
                        calendarFeign
                            .getNodeCarrierServiceCalendar(
                                nodeDto.getOrgId(), nodeDto.getNodeId(), carrierServiceId, null)
                            .getPayload())));

    var nodeCarrierServiceResponse = new NodeCarrierServiceResponse();
    nodeCarrierServiceResponse.setNodeId(nodeDto.getNodeId());
    nodeCarrierServiceResponse.setOrgId(nodeDto.getOrgId());
    nodeCarrierServiceResponse.setStreet(nodeDto.getStreet());
    nodeCarrierServiceResponse.setCity(nodeDto.getCity());
    nodeCarrierServiceResponse.setProvince(nodeDto.getProvince());
    nodeCarrierServiceResponse.setPostalCode(nodeDto.getPostalCode());
    nodeCarrierServiceResponse.setCarrierServices(distinctCarrierServiceIds);
    nodeCarrierServiceResponse.setPickupCalendar(pickUpCalendarList);
    return nodeCarrierServiceResponse;
  }
}
