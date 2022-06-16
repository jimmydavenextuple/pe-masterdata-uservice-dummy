package com.nextuple.node.carrier.service;

import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.carrier.domain.NodeCarrierDomain;
import com.nextuple.node.carrier.domain.entity.NodeCarrierEntity;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.nextuple.node.carrier.domain.mapper.NodeCarrierMapper;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.exception.CommonServiceException;
import com.nextuple.node.carrier.exception.NodeCarrierDomainException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
      throws NodeCarrierDomainException, CommonServiceException {

    NodeCarrierEntity nodeCarrierEntity = INSTANCE.nodeCarrierRequestToEntity(nodeCarrierRequest);

    Optional<NodeCarrierEntity> nodeCarrierEntity1 =
        nodeCarrierDomain.findNodeCarrierDetails(
            nodeCarrierEntity.getNodeId(),
            nodeCarrierEntity.getOrgId(),
            nodeCarrierEntity.getCarrierServiceId(),
            nodeCarrierEntity.getServiceOption());

    if (nodeCarrierEntity1.isPresent()) {
      logger.error(
          "Node Carrier already exists for nodeId:{} , orgId:{}, carrierServiceId:{}, serviceOption:{}",
          nodeCarrierEntity.getNodeId(),
          nodeCarrierEntity.getOrgId(),
          nodeCarrierEntity.getCarrierServiceId(),
          nodeCarrierEntity.getServiceOption());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          NODE_ID, FieldError.builder().rejectedValue(nodeCarrierEntity.getNodeId()).build());
      errorMap.put(
          ORG_ID, FieldError.builder().rejectedValue(nodeCarrierEntity.getOrgId()).build());
      errorMap.put(
          CARRIER_SERVICE_ID,
          FieldError.builder().rejectedValue(nodeCarrierEntity.getCarrierServiceId()).build());
      errorMap.put(
          SERVICE_OPTION,
          FieldError.builder().rejectedValue(nodeCarrierEntity.getServiceOption()).build());
      throw new CommonServiceException(
          "Node Carrier already exists for the given details",
          HttpStatus.BAD_REQUEST,
          0x1772,
          errorMap);
    }

    return INSTANCE.toNodeCarrierDto(nodeCarrierDomain.saveNodeCarrierEntity(nodeCarrierEntity));
  }

  public NodeCarrierResponse getNodeCarrierDetails(
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
    return INSTANCE.toNodeCarrierDto(nodeCarrierEntity.get());
  }

  public NodeCarrierResponse updateNodeCarrier(
      String nodeId,
      String orgId,
      String carrierServiceId,
      String serviceOption,
      NodeCarrierUpdateRequest nodeCarrierUpdateRequest)
      throws NodeCarrierDomainException, CommonServiceException {

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
    NodeCarrierResponse nodeCarrierResponse = INSTANCE.toNodeCarrierDto(nodeCarrierEntity.get());
    nodeCarrierDomain.deleteNodeCarrierEntity(nodeCarrierEntity.get());
    return nodeCarrierResponse;
  }

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
}
