package com.hbc.jobs.consumers.service;

import com.hbc.jobs.consumers.exception.NodeCarrierMapperException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.RecordInputDto;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NodeCarrierMapper implements FeignClientMapper {

  private final NodeCarrierFeign nodeCarrierFeign;
  @Setter private JobTypeEnum jobTypeEnum;

  @Override
  public Object getDTOFromCustomMapper(String stringRecord) {
    String[] rows = stringRecord.split("\n");
    String[] header = rows[0].split(",");
    String[] data = rows[1].split(",");

    if (jobTypeEnum == JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES) {
      return convertCSVToNodeCarrierRequest(header, data);
    } else {
      return null;
    }
  }

  private NodeCarrierRequest convertCSVToNodeCarrierRequest(String[] header, String[] data) {
    NodeCarrierRequest nodeCarrierRequest = new NodeCarrierRequest();
    int bound = header.length;
    for (int i = 0; i < bound; i++) {
      switch (header[i]) {
        case "nodeId":
          nodeCarrierRequest.setNodeId(data[i]);
          break;
        case "orgId":
          nodeCarrierRequest.setOrgId(data[i]);
          break;
        case "serviceOptions":
          nodeCarrierRequest.setServiceOption(data[i]);
          break;
        case "processingTime (in hrs)":
          nodeCarrierRequest.setProcessingTime(Double.valueOf(data[i]));
          break;
        default:
          break;
      }
    }
    nodeCarrierRequest.setCarrierServiceId("ALL-SDND");
    nodeCarrierRequest.setLastPickupTime("0");

    return nodeCarrierRequest;
  }

  @Override
  public Map<String, String> getColumnNameMapping(String[] headerColumns) {
    return getDefaultColumnNameMapping(headerColumns);
  }

  @Override
  public Class mapTODto() throws NodeCarrierMapperException {
    try {
      if (jobTypeEnum == JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES) {
        return NodeCarrierRequest.class;
      }
      log.error("Unable to map an object!");
      throw new NodeCarrierMapperException(
          "Error while mapping an object to the expected object", jobTypeEnum);
    } catch (Exception e) {
      log.error("Error while mapping to DTO");
      throw new NodeCarrierMapperException(
          "Exception while mapping an object to expected object", e, jobTypeEnum);
    }
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs)
      throws NodeCarrierMapperException {
    if (jobTypeEnum == JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES) {
      NodeCarrierRequest nodeCarrierRequest = (NodeCarrierRequest) request;
      return ResponseEntity.ok(nodeCarrierFeign.createNodeCarrier(nodeCarrierRequest));
    }
    log.error("Failed to make a call based on job type");
    throw new NodeCarrierMapperException("Please provide the valid job type", jobTypeEnum);
  }
}
