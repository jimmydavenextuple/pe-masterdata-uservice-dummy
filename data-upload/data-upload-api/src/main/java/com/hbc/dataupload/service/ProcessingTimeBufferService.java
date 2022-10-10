package com.hbc.dataupload.service;

import com.hbc.common.base.PagePayload;
import com.hbc.dataupload.domain.dto.ProcessingTimeBufferDto;
import com.hbc.dataupload.domain.pojo.ProcessingTimeBuffer;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.domain.dto.NodeDto;
import com.hbc.node.domain.feign.NodeFeign;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessingTimeBufferService {

  private final NodeFeign nodeFeign;
  private final NodeCarrierFeign nodeCarrierFeign;

  public PagePayload<ProcessingTimeBufferDto> getProcessingTimeBuffers(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {
    PagePayload<ProcessingTimeBufferDto> processingTimeBufferDtoPagePayload = new PagePayload<>();
    List<ProcessingTimeBufferDto> responseList = new ArrayList<>();

    PagePayload<NodeDto> nodeDtoPagePayload =
        nodeFeign.getNodeList(orgId, pageNo, pageSize, sortBy, sortOrder).getPayload();

    List<NodeDto> nodeDtoList = nodeDtoPagePayload.getData();

    nodeDtoList.forEach(
        nodeDto -> {
          List<NodeCarrierResponse> nodeCarrierResponses =
              nodeCarrierFeign
                  .getNodeCarrierList(nodeDto.getNodeId(), nodeDto.getOrgId())
                  .getPayload();

          responseList.add(setProcessingTimeBuffers(nodeDto, nodeCarrierResponses));
        });

    processingTimeBufferDtoPagePayload.setPagination(nodeDtoPagePayload.getPagination());
    processingTimeBufferDtoPagePayload.setData(responseList);

    return processingTimeBufferDtoPagePayload;
  }

  private ProcessingTimeBufferDto setProcessingTimeBuffers(
      NodeDto nodeDto, List<NodeCarrierResponse> nodeCarrierResponses) {
    return ProcessingTimeBufferDto.builder()
        .nodeId(nodeDto.getNodeId())
        .orgId(nodeDto.getOrgId())
        .nodeType(nodeDto.getNodeType())
        .street(nodeDto.getStreet())
        .city(nodeDto.getCity())
        .province(nodeDto.getProvince())
        .postalCode(nodeDto.getPostalCode())
        .serviceOptions(getServiceOptions(nodeCarrierResponses))
        .processingTimeBuffers(getProcessingTimeBufferList(nodeCarrierResponses))
        .build();
  }

  private List<ProcessingTimeBuffer> getProcessingTimeBufferList(
      List<NodeCarrierResponse> nodeCarrierResponses) {
    List<ProcessingTimeBuffer> processingTimeBufferList = new ArrayList<>();

    nodeCarrierResponses.forEach(
        nodeCarrierResponse -> {
          ProcessingTimeBuffer processingTimeBuffer = new ProcessingTimeBuffer();
          processingTimeBuffer.setServiceOption(nodeCarrierResponse.getServiceOption());
          processingTimeBuffer.setBufferHours(nodeCarrierResponse.getBufferHours());
          processingTimeBuffer.setBufferStartDate(nodeCarrierResponse.getBufferStartDate());
          processingTimeBuffer.setBufferEndDate(nodeCarrierResponse.getBufferEndDate());
          processingTimeBuffer.setStatus(computeStatus(nodeCarrierResponse));
          processingTimeBufferList.add(processingTimeBuffer);
        });

    return processingTimeBufferList;
  }

  private String computeStatus(NodeCarrierResponse nodeCarrierResponse) {
    var currentDate = new Date();
    if (nodeCarrierResponse.getBufferHours() != null) {
      if (currentDate.compareTo(nodeCarrierResponse.getBufferEndDate()) <= 0) return "Active";
      else return "InActive";
    }
    return null;
  }

  private List<String> getServiceOptions(List<NodeCarrierResponse> nodeCarrierResponse) {
    List<String> serviceOptions = new ArrayList<>();
    nodeCarrierResponse.forEach(nodeCarrier -> serviceOptions.add(nodeCarrier.getServiceOption()));
    return serviceOptions;
  }
}
