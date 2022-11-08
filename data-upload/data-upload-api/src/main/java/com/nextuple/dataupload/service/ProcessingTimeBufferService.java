package com.nextuple.dataupload.service;

import com.nextuple.common.base.PagePayload;
import com.nextuple.dataupload.common.outbound.ProcessingTimeBufferResponse;
import com.nextuple.dataupload.common.pojo.ProcessingTimeBuffer;
import com.nextuple.node.carrier.domain.feign.NodeCarrierFeign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.feign.NodeFeign;
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

  private static final String ACTIVE = "Active";
  private static final String INACTIVE = "Inactive";
  private final NodeFeign nodeFeign;
  private final NodeCarrierFeign nodeCarrierFeign;

  public PagePayload<ProcessingTimeBufferResponse> getProcessingTimeBuffers(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {
    PagePayload<ProcessingTimeBufferResponse> processingTimeBufferDtoPagePayload =
        new PagePayload<>();
    List<ProcessingTimeBufferResponse> responseList = new ArrayList<>();

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

  private ProcessingTimeBufferResponse setProcessingTimeBuffers(
      NodeDto nodeDto, List<NodeCarrierResponse> nodeCarrierResponses) {
    return ProcessingTimeBufferResponse.builder()
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
      List<NodeCarrierResponse> nodeServiceResponses) {
    List<ProcessingTimeBuffer> processingTimeBufferList = new ArrayList<>();

    nodeServiceResponses.forEach(
        nodeServiceResponse -> {
          var processingTimeBuffer = new ProcessingTimeBuffer();
          processingTimeBuffer.setServiceOption(nodeServiceResponse.getServiceOption());
          processingTimeBuffer.setBufferHours(nodeServiceResponse.getBufferHours());
          processingTimeBuffer.setBufferStartDate(nodeServiceResponse.getBufferStartDate());
          processingTimeBuffer.setBufferEndDate(nodeServiceResponse.getBufferEndDate());
          processingTimeBuffer.setStatus(computeStatus(nodeServiceResponse));
          processingTimeBufferList.add(processingTimeBuffer);
        });

    return processingTimeBufferList;
  }

  private String computeStatus(NodeCarrierResponse nodeServiceResponse) {
    var currentDate = new Date();
    if (nodeServiceResponse.getBufferHours() != null
        && nodeServiceResponse.getBufferStartDate() != null
        && nodeServiceResponse.getBufferEndDate() != null) {
      if (currentDate.compareTo(nodeServiceResponse.getBufferEndDate()) <= 0) return ACTIVE;
      else return INACTIVE;
    }
    return null;
  }

  private List<String> getServiceOptions(List<NodeCarrierResponse> nodeServiceResponse) {
    List<String> serviceOptions = new ArrayList<>();
    nodeServiceResponse.forEach(nodeCarrier -> serviceOptions.add(nodeCarrier.getServiceOption()));
    return serviceOptions;
  }
}
