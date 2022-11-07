package com.hbc.jobs.consumers.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;

import com.hbc.common.constants.CommonConstants;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.response.error.FieldError;
import com.hbc.common.util.DateUtil;
import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.csvdownload.exception.CsvDataValidationException;
import com.hbc.jobs.consumers.domain.mapper.NodeCarrierRequestMapper;
import com.hbc.jobs.consumers.exception.InvalidActionTypeException;
import com.hbc.jobs.consumers.exception.InvalidJobTypeException;
import com.hbc.jobs.consumers.exception.NodeCarrierMapperException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.NodeCarrierUpload;
import com.hbc.jobs.framework.common.domain.pojo.ProcessingTimeBufferUpload;
import com.hbc.jobs.framework.common.domain.pojo.RecordInputDto;
import com.hbc.jobs.framework.common.enums.ModuleEnum;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.factory.Mappers;
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
  private final NodeCarrierFeign nodeCarrierFeign;

  public static final NodeCarrierRequestMapper INSTANCE =
      Mappers.getMapper(NodeCarrierRequestMapper.class);

  @Setter private JobTypeEnum jobTypeEnum;

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

  private ResponseEntity<?> invokeProcessingTimeBuffer(ProcessingTimeBufferUpload request) {
    var nodeCarrierBufferRequest = INSTANCE.convertToNodeCarrierBufferRequest(request);
    nodeCarrierBufferRequest.setBufferStartDate(DateUtil.getDateUTC(request.getBufferStartDate()));
    nodeCarrierBufferRequest.setBufferEndDate(DateUtil.getDateUTC(request.getBufferEndDate()));
    return ResponseEntity.ok(nodeCarrierFeign.updateBuffer(nodeCarrierBufferRequest));
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

    if (CommonConstants.UPDATE_U.equalsIgnoreCase(processingLeadTimesRaw.getActionType())) {
      return ResponseEntity.ok(
          nodeCarrierFeign.createNodeCarrier(
              INSTANCE.convertToNodeCarrierRequest(processingLeadTimesRaw)));
    } else if (CommonConstants.DELETE_D.equalsIgnoreCase(processingLeadTimesRaw.getActionType())) {
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

  public void throwCommonServiceException(String errorMessage, String field, String fieldValue)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(field, FieldError.builder().rejectedValue(fieldValue).build());
    throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x1772, errorMap);
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
