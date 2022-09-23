package com.hbc.jobs.consumers.service;

import com.hbc.common.constants.CommonConstants;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.csvdownload.exception.CsvDataValidationException;
import com.hbc.jobs.consumers.domain.mapper.NodeCarrierRequestMapper;
import com.hbc.jobs.consumers.exception.InvalidActionTypeException;
import com.hbc.jobs.consumers.exception.NodeCarrierMapperException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.RecordInputDto;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NodeCarrierMapper implements FeignClientMapper {

  private final Logger logger = LoggerFactory.getLogger(NodeCarrierMapper.class);

  private final NodeCarrierFeign nodeCarrierFeign;

  public static final NodeCarrierRequestMapper INSTANCE =
      Mappers.getMapper(NodeCarrierRequestMapper.class);

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
    var nodeCarrierRequest = new NodeCarrierRequest();
    int bound = header.length;
    for (var i = 0; i < bound; i++) {
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
    nodeCarrierRequest.setCarrierServiceId("");

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
        return ProcessingLeadTimesRaw.class;
      }
      logger.error("Unable to map an object!");
      throw new NodeCarrierMapperException(
          "Error while mapping an object to the expected object", jobTypeEnum);
    } catch (Exception e) {
      logger.error("Error while mapping to DTO");
      throw new NodeCarrierMapperException(
          "Exception while mapping an object to expected object", e, jobTypeEnum);
    }
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs)
      throws NodeCarrierMapperException, InvalidActionTypeException {
    if (jobTypeEnum == JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES) {
      var processingLeadTimesRaw = (ProcessingLeadTimesRaw) request;

      validateProcessingLeadTimeAndActionType(
          processingLeadTimesRaw.getActionType(), processingLeadTimesRaw.getProcessingTime());

      if (CommonConstants.UPDATE_U.equalsIgnoreCase(processingLeadTimesRaw.getActionType())) {
        return ResponseEntity.ok(
            nodeCarrierFeign.createNodeCarrier(
                INSTANCE.convertToNodeCarrierRequest(processingLeadTimesRaw)));
      } else if (CommonConstants.DELETE_D.equalsIgnoreCase(
          processingLeadTimesRaw.getActionType())) {
        return ResponseEntity.ok(
            nodeCarrierFeign.deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
                processingLeadTimesRaw.getNodeId(),
                processingLeadTimesRaw.getOrgId(),
                processingLeadTimesRaw.getCarrierServiceId(),
                processingLeadTimesRaw.getServiceOption()));
      }
    }
    logger.error("Failed to make a call based on job type");
    throw new NodeCarrierMapperException("Please provide the valid job type", jobTypeEnum);
  }

  private void validateProcessingLeadTimeAndActionType(
      String actionType, String processingLeadTimeString) throws InvalidActionTypeException {
    if (ObjectUtils.isEmpty(actionType) || !CommonConstants.actionTypes.contains(actionType)) {
      logger.error("Invalid action type: {}", actionType);
      throw new InvalidActionTypeException("Invalid action type: " + actionType, actionType);
    }
    if (!NumberUtils.isCreatable(processingLeadTimeString)) {
      logger.error("Invalid processing lead time: {}", processingLeadTimeString);
      throw new CsvDataValidationException(
          "Invalid processing lead time: " + processingLeadTimeString);
    }
  }
}
