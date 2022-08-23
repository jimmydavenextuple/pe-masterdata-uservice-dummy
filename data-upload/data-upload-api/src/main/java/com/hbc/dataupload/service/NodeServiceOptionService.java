package com.hbc.dataupload.service;

import com.hbc.common.base.PagePayload;
import com.hbc.dataupload.domian.dto.NodeServiceOptionDto;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.domain.dto.NodeDto;
import com.hbc.node.domain.feign.NodeFeign;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NodeServiceOptionService {
  private final NodeFeign nodeFeign;
  private final NodeCarrierFeign nodeCarrierFeign;

  public PagePayload<NodeServiceOptionDto> getNodeServiceOption(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {
    PagePayload<NodeServiceOptionDto> nodeServiceOptionDtoPagePayload = new PagePayload<>();
    List<NodeServiceOptionDto> responseList = new ArrayList<>();

    PagePayload<NodeDto> nodeResponse =
        nodeFeign.getNodeList(orgId, pageNo, pageSize, sortBy, sortOrder).getPayload();

    List<NodeDto> nodeResponseList = nodeResponse.getData();

    for (NodeDto node : nodeResponseList) {
      List<NodeCarrierResponse> nodeCarrierResponse =
          nodeCarrierFeign.getNodeCarrierList(node.getNodeId(), node.getOrgId()).getPayload();

      var nodeServiceOptionDto = setNodeServiceOptionDto(node, nodeCarrierResponse);
      responseList.add(nodeServiceOptionDto);
    }
    nodeServiceOptionDtoPagePayload.setPagination(nodeResponse.getPagination());
    nodeServiceOptionDtoPagePayload.setData(responseList);

    return nodeServiceOptionDtoPagePayload;
  }

  private NodeServiceOptionDto setNodeServiceOptionDto(
      NodeDto node, List<NodeCarrierResponse> nodeCarrierResponse) {
    return NodeServiceOptionDto.builder()
        .nodeId(node.getNodeId())
        .orgId(node.getOrgId())
        .nodeType(node.getNodeType())
        .street(node.getStreet())
        .city(node.getCity())
        .province(node.getProvince())
        .isActive(computeIsActive(nodeCarrierResponse))
        .serviceOptions(getServiceOptions(nodeCarrierResponse))
        .processingTime(getProcessingTime(nodeCarrierResponse))
        .build();
  }

  private Boolean computeIsActive(List<NodeCarrierResponse> nodeCarrierResponse) {
    for (NodeCarrierResponse nodeCarrier : nodeCarrierResponse) {
      if (nodeCarrier.getProcessingTime() > 0) {
        return true;
      }
    }
    return false;
  }

  private Map<String, Double> getProcessingTime(List<NodeCarrierResponse> nodeCarrierResponse) {
    Map<String, Double> processingTime = new HashMap<>();
    nodeCarrierResponse.forEach(
        nodeCarrier ->
            processingTime.put(nodeCarrier.getServiceOption(), nodeCarrier.getProcessingTime()));
    return processingTime;
  }

  private List<String> getServiceOptions(List<NodeCarrierResponse> nodeCarrierResponse) {
    List<String> serviceOptions = new ArrayList<>();
    nodeCarrierResponse.forEach(nodeCarrier -> serviceOptions.add(nodeCarrier.getServiceOption()));
    return serviceOptions;
  }
}
