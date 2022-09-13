package com.hbc.node.carrier.service;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.node.carrier.domain.NodeCarrierDomain;
import com.hbc.node.carrier.domain.entity.NodeCarrierEntity;
import com.hbc.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierSelectionRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.hbc.node.carrier.domain.mapper.NodeCarrierMapper;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
import com.hbc.node.carrier.exception.InvalidDataException;
import com.hbc.node.carrier.exception.NodeCarrierDomainException;
import com.hbc.postgres.config.ReaderDS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Service
public class NodeCarrierService {

  private static final Logger logger = LoggerFactory.getLogger(NodeCarrierService.class);
  private static final String ORG_ID = "orgId";
  private static final String NODE_ID = "nodeId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";
  private static final String SERVICE_OPTION = "serviceOption";
  private static final String NODE_CARRIER_NOT_FOUND_ERROR_MSG =
      "Node Carrier not found for given details";

  private final NodeCarrierDomain nodeCarrierDomain;

  public static final NodeCarrierMapper INSTANCE = Mappers.getMapper(NodeCarrierMapper.class);

  public NodeCarrierResponse createNodeCarrier(NodeCarrierRequest nodeCarrierRequest)
      throws NodeCarrierDomainException, InvalidDataException, CommonServiceException {
    validateBufferHours(nodeCarrierRequest.getBufferHours());
    if (ObjectUtils.isEmpty(nodeCarrierRequest.getCarrierServiceId())) {
      validateProcessingLeadTime(nodeCarrierRequest.getProcessingTime());
    } else {
      validateLastPickupTime(nodeCarrierRequest.getLastPickupTime());
    }
    var nodeCarrierEntity = INSTANCE.nodeCarrierRequestToEntity(nodeCarrierRequest);
    return INSTANCE.toNodeCarrierDto(nodeCarrierDomain.saveNodeCarrierEntity(nodeCarrierEntity));
  }

  public NodeCarrierResponse updateBufferData(NodeCarrierBufferRequest nodeCarrierBufferRequest)
      throws NodeCarrierDomainException, CommonServiceException {

    validateBufferHours(nodeCarrierBufferRequest.getBufferHours());
    var nodeCarrierEntity = INSTANCE.nodeCarrierBufferRequestToEntity(nodeCarrierBufferRequest);

    Optional<NodeCarrierEntity> existingNodeEntity =
        nodeCarrierDomain.findNodeCarrierDetails(
            nodeCarrierEntity.getNodeId(),
            nodeCarrierEntity.getOrgId(),
            "",
            nodeCarrierEntity.getServiceOption());

    if (!existingNodeEntity.isPresent()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          NODE_ID, FieldError.builder().rejectedValue(nodeCarrierEntity.getNodeId()).build());
      errorMap.put(
          ORG_ID, FieldError.builder().rejectedValue(nodeCarrierEntity.getOrgId()).build());
      errorMap.put(
          SERVICE_OPTION,
          FieldError.builder().rejectedValue(nodeCarrierEntity.getServiceOption()).build());
      throw new CommonServiceException(
          NODE_CARRIER_NOT_FOUND_ERROR_MSG, HttpStatus.NOT_FOUND, 0x1773, errorMap);
    }
    INSTANCE.updateNodeCarrierEntityWithBuffer(nodeCarrierBufferRequest, existingNodeEntity.get());
    return INSTANCE.toNodeCarrierDto(
        nodeCarrierDomain.saveNodeCarrierEntity(existingNodeEntity.get()));
  }

  public void validateLastPickupTime(String lastPickupTime) throws InvalidDataException {
    var regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
    if (!lastPickupTime.matches(regex)) {
      throw new InvalidDataException("LastPickupTime is invalid", lastPickupTime, null);
    }
  }

  @ReaderDS
  public NodeCarrierResponse getNodeCarrierDetails(
      String nodeId, String orgId, String carrierServiceId, String serviceOption)
      throws NodeCarrierDomainException, CommonServiceException {
    String allServiceOption = "ALL-" + serviceOption;
    List<NodeCarrierEntity> nodeCarrierEntityList =
        nodeCarrierDomain.filterAndGetNodeCarrierDetails(
            nodeId, orgId, carrierServiceId, allServiceOption);

    Optional<NodeCarrierEntity> nodeCarrierEntity = Optional.empty();
    if (!"ALL".equals(carrierServiceId)) {
      nodeCarrierEntity =
          nodeCarrierEntityList.stream()
              .filter(x -> carrierServiceId.equals(x.getCarrierServiceId()))
              .findFirst();
    }
    if (nodeCarrierEntity.isEmpty()) {
      nodeCarrierEntity =
          nodeCarrierEntityList.stream()
              .filter(x -> allServiceOption.equals(x.getCarrierServiceId()))
              .findAny();
    }
    if (nodeCarrierEntity.isEmpty()) {
      nodeCarrierEntity =
          nodeCarrierEntityList.stream()
              .filter(x -> "ALL".equals(x.getCarrierServiceId()))
              .findAny();
    }

    if (nodeCarrierEntity.isEmpty()
        || !Objects.equals(nodeCarrierEntity.get().getServiceOption(), serviceOption)) {
      logger.error(NODE_CARRIER_NOT_FOUND_ERROR_MSG);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      errorMap.put(SERVICE_OPTION, FieldError.builder().rejectedValue(serviceOption).build());
      throw new CommonServiceException(
          NODE_CARRIER_NOT_FOUND_ERROR_MSG, HttpStatus.NOT_FOUND, 0x1773, errorMap);
    }
    return INSTANCE.toNodeCarrierDto(nodeCarrierEntity.get());
  }

  public NodeCarrierResponse updateNodeCarrier(
      String nodeId,
      String orgId,
      String carrierServiceId,
      String serviceOption,
      NodeCarrierUpdateRequest nodeCarrierUpdateRequest)
      throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {

    validateLastPickupTime(nodeCarrierUpdateRequest.getLastPickupTime());
    Optional<NodeCarrierEntity> existingNodeEntity =
        nodeCarrierDomain.findNodeCarrierDetails(nodeId, orgId, carrierServiceId, serviceOption);

    if (!existingNodeEntity.isPresent()) {
      logger.error(NODE_CARRIER_NOT_FOUND_ERROR_MSG);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      errorMap.put(SERVICE_OPTION, FieldError.builder().rejectedValue(serviceOption).build());
      throw new CommonServiceException(
          NODE_CARRIER_NOT_FOUND_ERROR_MSG, HttpStatus.NOT_FOUND, 0x1773, errorMap);
    }
    logger.info(
        "Response before updation of node-carrier :{}",
        INSTANCE.toNodeCarrierDto(existingNodeEntity.get()));
    INSTANCE.updateNodeCarrierEntity(nodeCarrierUpdateRequest, existingNodeEntity.get());
    return INSTANCE.toNodeCarrierDto(
        nodeCarrierDomain.saveNodeCarrierEntity(existingNodeEntity.get()));
  }

  public NodeCarrierResponse deleteNodeCarrier(
      String nodeId, String orgId, String carrierServiceId, String serviceOption)
      throws NodeCarrierDomainException, CommonServiceException {
    Optional<NodeCarrierEntity> nodeCarrierEntity =
        nodeCarrierDomain.findNodeCarrierDetails(nodeId, orgId, carrierServiceId, serviceOption);

    if (!nodeCarrierEntity.isPresent()) {
      logger.error(NODE_CARRIER_NOT_FOUND_ERROR_MSG);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      errorMap.put(SERVICE_OPTION, FieldError.builder().rejectedValue(serviceOption).build());
      throw new CommonServiceException(
          NODE_CARRIER_NOT_FOUND_ERROR_MSG, HttpStatus.NOT_FOUND, 0x1773, errorMap);
    }
    logger.info(
        "Response before deletion of node-carrier :{}",
        INSTANCE.toNodeCarrierDto(nodeCarrierEntity.get()));
    var nodeCarrierResponse = INSTANCE.toNodeCarrierDto(nodeCarrierEntity.get());
    nodeCarrierDomain.deleteNodeCarrierEntity(nodeCarrierEntity.get());
    return nodeCarrierResponse;
  }

  @ReaderDS
  public List<NodeCarrierResponse> getNodeCarrierForNodeIdAOrgIdAndServiceOption(
      String nodeId, String orgId, String serviceOption)
      throws NodeCarrierDomainException, CommonServiceException {

    List<NodeCarrierEntity> nodeCarrierEntityList =
        nodeCarrierDomain.findNodeCarrierByNodeIdAOrgIdAndServiceOption(
            nodeId, orgId, serviceOption);

    if (nodeCarrierEntityList.isEmpty()) {
      logger.error(NODE_CARRIER_NOT_FOUND_ERROR_MSG);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SERVICE_OPTION, FieldError.builder().rejectedValue(serviceOption).build());
      throw new CommonServiceException(
          NODE_CARRIER_NOT_FOUND_ERROR_MSG, HttpStatus.NOT_FOUND, 0x1773, errorMap);
    }
    List<NodeCarrierResponse> nodeCarrierResponseList = new ArrayList<>();

    nodeCarrierEntityList.forEach(
        nodeCarrierEntity ->
            nodeCarrierResponseList.add(INSTANCE.toNodeCarrierDto(nodeCarrierEntity)));

    return nodeCarrierResponseList;
  }

  @ReaderDS
  public List<NodeCarrierResponse> getNodeCarrierForNodeIdAndOrgId(String nodeId, String orgId)
      throws NodeCarrierDomainException {
    List<NodeCarrierEntity> nodeCarrierEntity =
        nodeCarrierDomain.findNodeCarrierByNodeIdAndOrgId(nodeId, orgId);

    return INSTANCE.toNodeCarrierResponseList(nodeCarrierEntity);
  }

  public void validateBufferHours(Double bufferHours) throws CommonServiceException {
    if (bufferHours != null && bufferHours < 0) {
      throw new CommonServiceException(
          "bufferHours cannot be negative", HttpStatus.BAD_REQUEST, 0x1775, null);
    }
  }

  public NodeCarrierResponse updateProcessingLeadTime(NodeCarrierRequest nodeCarrierRequest)
      throws NodeCarrierDomainException {
    var nodeCarrierEntity = INSTANCE.nodeCarrierRequestToEntity(nodeCarrierRequest);
    return INSTANCE.toNodeCarrierDto(nodeCarrierDomain.saveNodeCarrierEntity(nodeCarrierEntity));
  }

  private void validateProcessingLeadTime(Double processingLeadTime) throws InvalidDataException {
    if (ObjectUtils.isEmpty(processingLeadTime) || processingLeadTime < 0) {
      logger.error("Processing lead time can not be negative or empty");
      throw new InvalidDataException(
          "Processing lead time can not be negative or empty", null, processingLeadTime);
    }
  }

  public NodeCarrierSelectionResponse addNodeCarrierSelectionPriority(
      NodeCarrierSelectionRequest nodeCarrierSelectionRequest) {

    var nodeCarrierSelectionEntity =
        INSTANCE.nodeCarrierSelectionRequestToEntity(nodeCarrierSelectionRequest);
    return INSTANCE.toNodeCarrierSelectionResponse(
        nodeCarrierDomain.saveNodeCarrierSelectionEntity(nodeCarrierSelectionEntity));
  }

  public List<NodeCarrierSelectionResponse> getNodeCarrierSelectionDetails(
      String orgId, String serviceOption, String destinationGeozone) {

    return INSTANCE.toNodeCarrierSelectionResponseList(
        nodeCarrierDomain.findNodeCarrierByOrgIdAndServiceOptionAndDestinationGeoZone(
            orgId, serviceOption, destinationGeozone));
  }
}
