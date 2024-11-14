/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.actionTypes;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.dataupload.common.config.TenantDatabaseConfig;
import com.nextuple.jobs.consumers.domain.mapper.NodeCarrierRequestMapper;
import com.nextuple.jobs.consumers.exception.InvalidActionTypeException;
import com.nextuple.jobs.consumers.exception.InvalidJobTypeException;
import com.nextuple.jobs.consumers.exception.NodeCarrierMapperException;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.NodeCarrierUpload;
import com.nextuple.jobs.framework.common.domain.pojo.ProcessingTimeBufferUpload;
import com.nextuple.jobs.framework.common.domain.pojo.RecordInputDto;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.node.carrier.domain.feign.INodeCarrierFeign;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NodeCarrierMapper implements FeignClientMapper {

  private final Logger logger = LoggerFactory.getLogger(NodeCarrierMapper.class);
  private static final String ORG_ID = "orgId";
  private static final String NODE_ID = "nodeId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";
  private static final String SERVICE_OPTION = "serviceOption";
  @Autowired INodeCarrierFeign nodeCarrierFeign;

  public static final NodeCarrierRequestMapper INSTANCE =
      Mappers.getMapper(NodeCarrierRequestMapper.class);
  private static final String LONG_DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";
  private static final String INVALID_DATE_ERROR_MSG = "Invalid Date or Invalid Dateformat ";

  @Setter private JobTypeEnum jobTypeEnum;
  @Autowired private TenantDatabaseConfig tenantDatabaseConfig;

  @Override
  public ModuleEnum getModule() {
    return ModuleEnum.NODE_CARRIER;
  }

  @Override
  public void setJobType(JobTypeEnum jobType) {
    this.jobTypeEnum = jobType;
  }

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
        case NODE_ID:
          nodeCarrierRequest.setNodeId(data[i]);
          break;
        case ORG_ID:
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
  public Class mapTODto() throws NodeCarrierMapperException, InvalidJobTypeException {
    switch (jobTypeEnum) {
      case UPLOAD_PROCESSING_LEAD_TIMES:
        return ProcessingLeadTimesRaw.class;
      case UPLOAD_NODE_CARRIER:
        return NodeCarrierUpload.class;
      case UPLOAD_NODE_SERVICE_OPTION_BUFFER:
        return ProcessingTimeBufferUpload.class;
      default:
        {
          logger.error("Invalid JobType: {}", jobTypeEnum);
          throw new InvalidJobTypeException("Invalid JobType provided", jobTypeEnum.name());
        }
    }
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs)
      throws NodeCarrierMapperException, InvalidActionTypeException, CommonServiceException {
    switch (jobTypeEnum) {
      case UPLOAD_PROCESSING_LEAD_TIMES:
        return processProcessingLeadTimesUploadApiCall((ProcessingLeadTimesRaw) request);
      case UPLOAD_NODE_CARRIER:
        return processNodeCarrierUploadApiCall((NodeCarrierUpload) request);
      case UPLOAD_NODE_SERVICE_OPTION_BUFFER:
        return invokeProcessingTimeBuffer((ProcessingTimeBufferUpload) request);
      default:
        {
          logger.error("Failed to make a call based on job type");
          throw new NodeCarrierMapperException("Please provide the valid job type", jobTypeEnum);
        }
    }
  }

  private ResponseEntity<?> invokeProcessingTimeBuffer(ProcessingTimeBufferUpload request)
      throws CommonServiceException {
    String action = request.getAction();
    return switch (action) {
      case CREATE -> ResponseEntity.ok(
          nodeCarrierFeign.createBuffer(INSTANCE.convertToNodeCarrierBufferRequest(request)));
      case DELETE -> ResponseEntity.ok(
          nodeCarrierFeign.deleteBuffer(
              INSTANCE.convertToNodeServiceOptionBufferDeleteRequest(request)));
      default -> {
        logger.error("Invalid action type: {}", request.getAction());
        throw new CsvDataValidationException(
            "Please provide the valid action: " + request.getAction());
      }
    };
  }

  private ResponseEntity<?> processNodeCarrierUploadApiCall(NodeCarrierUpload nodeCarrierUpload) {
    var action = nodeCarrierUpload.getAction();
    switch (action) {
      case CREATE:
        return ResponseEntity.ok(
            nodeCarrierFeign.createNodeCarrier(
                INSTANCE.convertToNodeCarrierRequest(nodeCarrierUpload)));
      case UPDATE:
        return ResponseEntity.ok(
            nodeCarrierFeign.updateNodeCarrier(
                nodeCarrierUpload.getNodeId(),
                nodeCarrierUpload.getOrgId(),
                nodeCarrierUpload.getCarrierServiceId(),
                nodeCarrierUpload.getServiceOption(),
                INSTANCE.convertToNodeCarrierUpdateRequest(nodeCarrierUpload)));
      case DELETE:
        return ResponseEntity.ok(
            nodeCarrierFeign.deleteNodeCarrier(
                nodeCarrierUpload.getNodeId(),
                nodeCarrierUpload.getOrgId(),
                nodeCarrierUpload.getCarrierServiceId(),
                nodeCarrierUpload.getServiceOption()));
      default:
        {
          logger.error("Invalid action type: {}", nodeCarrierUpload.getAction());
          throw new CsvDataValidationException(
              "Please provide the valid action: " + nodeCarrierUpload.getAction());
        }
    }
  }

  public ResponseEntity<BaseResponse<NodeCarrierResponse>> processProcessingLeadTimesUploadApiCall(
      ProcessingLeadTimesRaw processingLeadTimesRaw)
      throws InvalidActionTypeException, CommonServiceException, NodeCarrierMapperException {
    validateProcessingLeadTimeAndActionType(
        processingLeadTimesRaw.getActionType(), processingLeadTimesRaw.getProcessingTime());

    if (UPDATE.equalsIgnoreCase(processingLeadTimesRaw.getActionType())) {
      validateServiceOptionsForNode(processingLeadTimesRaw);
      return ResponseEntity.ok(
          nodeCarrierFeign.createProcessingLeadTime(
              INSTANCE.convertToNodeCarrierRequest(processingLeadTimesRaw)));
    } else if (DELETE.equalsIgnoreCase(processingLeadTimesRaw.getActionType())) {
      var nodeId = processingLeadTimesRaw.getNodeId();
      var orgId = processingLeadTimesRaw.getOrgId();
      var carrierServiceId = processingLeadTimesRaw.getCarrierServiceId();
      var serviceOption = processingLeadTimesRaw.getServiceOption();

      if (StringUtils.isEmpty(nodeId)) {
        throwCommonServiceException("NodeId can't be empty", NODE_ID, nodeId);
      }
      if (StringUtils.isEmpty(orgId)) {
        throwCommonServiceException("OrgId can't be empty", ORG_ID, orgId);
      }
      if (StringUtils.isEmpty(serviceOption)) {
        throwCommonServiceException("ServiceOption can't be empty", SERVICE_OPTION, serviceOption);
      }

      return ResponseEntity.ok(
          nodeCarrierFeign.deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
              nodeId, orgId, carrierServiceId, serviceOption));
    }
    logger.error("Failed to make a call based on job type");
    throw new NodeCarrierMapperException("Please provide the valid job type", jobTypeEnum);
  }

  private void validateServiceOptionsForNode(ProcessingLeadTimesRaw processingLeadTimesRaw)
      throws CommonServiceException {
    String[] validServiceOptions =
        Arrays.stream(
                tenantDatabaseConfig
                    .fetchServiceOptions(processingLeadTimesRaw.getOrgId())
                    .split(","))
            .toArray(String[]::new);
    if (!Arrays.asList(validServiceOptions).contains(processingLeadTimesRaw.getServiceOption())) {
      logger.error("Invalid service option: {}", processingLeadTimesRaw.getServiceOption());
      throw new CsvDataValidationException(
          "Invalid service option: " + processingLeadTimesRaw.getServiceOption());
    }
  }

  public void throwCommonServiceException(String errorMessage, String field, String fieldValue)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(field, FieldError.builder().rejectedValue(fieldValue).build());
    throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x1772, errorMap);
  }

  private void validateProcessingLeadTimeAndActionType(
      String actionType, String processingLeadTimeString) throws InvalidActionTypeException {
    if (ObjectUtils.isEmpty(actionType) || !actionTypes.contains(actionType)) {
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
