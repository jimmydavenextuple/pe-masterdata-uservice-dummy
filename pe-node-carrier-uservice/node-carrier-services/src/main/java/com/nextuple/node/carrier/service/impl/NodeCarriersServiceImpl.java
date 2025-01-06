/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.service.impl;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.carrier.domain.NodeCarriersDomain;
import com.nextuple.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.nextuple.node.carrier.domain.entity.NodeCarriersEntity;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersUpdateRequest;
import com.nextuple.node.carrier.domain.mapper.NodeCarriersMapper;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import com.nextuple.node.carrier.exception.InvalidDataException;
import com.nextuple.node.carrier.service.NodeCarriersService;
import com.nextuple.node.carrier.service.ValidationService;
import com.nextuple.postgres.config.ReaderDS;
import jakarta.transaction.Transactional;
import java.util.Collections;
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

@Service
@RequiredArgsConstructor
public class NodeCarriersServiceImpl implements NodeCarriersService {
  private static final Logger logger = LoggerFactory.getLogger(NodeCarriersServiceImpl.class);
  private static final String ORG_ID = "orgId";
  private static final String NODE_ID = "nodeId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";
  private static final String SERVICE_OPTION = "serviceOption";
  private static final String NODE_CARRIER_NOT_FOUND_ERROR_MSG =
      "Node Carrier not found for given details";
  private static final String NODE_CARRIER_NOT_FOUND =
      "Node carrier not found with given orgId and nodeId";
  private final NodeCarriersDomain nodeCarriersDomain;

  private static final String NODE_CARRIER_NOT_FOUND_FOR_GIVEN_KEY =
      "Node Carriers not found for given key";
  private final ValidationService validationService;

  public static final NodeCarriersMapper INSTANCE = Mappers.getMapper(NodeCarriersMapper.class);

  @Transactional
  public NodeCarriersResponse createNodeCarrier(NodeCarriersRequest nodeCarriersRequest)
      throws InvalidDataException, CommonServiceException {
    validationService.validate(nodeCarriersRequest);
    var nodeCarrierEntity = INSTANCE.nodeCarriersRequestToEntity(nodeCarriersRequest);
    return INSTANCE.toNodeCarriersResponse(
        nodeCarriersDomain.saveNodeCarrierEntity(nodeCarrierEntity));
  }

  @ReaderDS
  public NodeCarriersResponse getNodeCarrierDetails(
      String orgId, String nodeId, String carrierServiceId, String serviceOption)
      throws CommonServiceException {
    Optional<NodeCarriersEntity> nodeCarriersEntityList =
        nodeCarriersDomain.filterAndGetNodeCarrierDetails(
            orgId, nodeId, carrierServiceId, serviceOption);
    handleEmptyNodeCarrier(nodeCarriersEntityList, orgId, nodeId, carrierServiceId, serviceOption);
    return INSTANCE.toNodeCarriersResponse(nodeCarriersEntityList.get());
  }

  @Transactional
  public NodeCarriersResponse updateNodeCarrier(
      String orgId,
      String nodeId,
      String carrierServiceId,
      String serviceOption,
      NodeCarriersUpdateRequest nodeCarriersUpdateRequest)
      throws CommonServiceException, InvalidDataException {

    validationService.validate(nodeCarriersUpdateRequest);
    Optional<NodeCarriersEntity> existingNodeCarriersEntity =
        nodeCarriersDomain.findNodeCarrierDetails(orgId, nodeId, carrierServiceId, serviceOption);
    handleEmptyNodeCarrier(
        existingNodeCarriersEntity, orgId, nodeId, carrierServiceId, serviceOption);
    INSTANCE.updateNodeCarrierEntity(nodeCarriersUpdateRequest, existingNodeCarriersEntity.get());
    return INSTANCE.toNodeCarriersResponse(
        nodeCarriersDomain.saveNodeCarrierEntity(existingNodeCarriersEntity.get()));
  }

  @Transactional
  public NodeCarriersResponse deleteNodeCarrier(
      String orgId, String nodeId, String carrierServiceId, String serviceOption)
      throws CommonServiceException {

    Optional<NodeCarriersEntity> nodeCarriersEntity =
        nodeCarriersDomain.findNodeCarrierDetails(orgId, nodeId, carrierServiceId, serviceOption);
    handleEmptyNodeCarrier(nodeCarriersEntity, orgId, nodeId, carrierServiceId, serviceOption);

    var nodeCarrierResponse = INSTANCE.toNodeCarriersResponse(nodeCarriersEntity.get());
    nodeCarriersDomain.deleteNodeCarrierEntityByNodeIdAndOrgIdAndCarrierServiceIdAndServiceOption(
        nodeCarriersEntity.get().getOrgId(),
        nodeCarriersEntity.get().getNodeId(),
        nodeCarriersEntity.get().getCarrierServiceId(),
        nodeCarriersEntity.get().getServiceOption());
    return nodeCarrierResponse;
  }

  @Transactional
  public List<NodeCarriersResponse> deleteNodeCarrierByNodeId(String orgId, String nodeId)
      throws CommonServiceException {

    List<NodeCarriersEntity> nodeCarriersEntity =
        nodeCarriersDomain.findNodeCarriersMandatoryByOrgIdAndNodeId(orgId, nodeId);
    if (nodeCarriersEntity.isEmpty()) {
      return Collections.emptyList();
    }
    return INSTANCE.toNodeCarriersResponseList(
        nodeCarriersDomain.deleteAllNodeCarrierEntityByOrgIdAndNodeId(orgId, nodeId));
  }

  public List<NodeCarriersResponse> getNodeCarriersList(
      String orgId, String nodeId, String serviceOption) throws CommonServiceException {
    List<NodeCarriersEntity> nodeCarrierEntityList =
        nodeCarriersDomain.findNodeCarriersListByOrgIdAndNodeIdAndServiceOption(
            nodeId, orgId, serviceOption);
    validationService.validate(orgId, nodeId, serviceOption);
    if (nodeCarrierEntityList.isEmpty()) {
      logger.error(NODE_CARRIER_NOT_FOUND_FOR_GIVEN_KEY);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SERVICE_OPTION, FieldError.builder().rejectedValue(serviceOption).build());
      throw new CommonServiceException(
          NODE_CARRIER_NOT_FOUND_FOR_GIVEN_KEY, HttpStatus.NOT_FOUND, 0x1774, errorMap);
    }
    return INSTANCE.toNodeCarriersResponseList(nodeCarrierEntityList);
  }

  public List<NodeCarrierListCacheKeyDto> getAllNodeCarriersCacheKeys(Integer limit)
      throws CommonServiceException {
    var nodeCarrierEntities = nodeCarriersDomain.getAllNodeCarriers(limit);
    return INSTANCE.toNodeCarriersCacheKeyDto(nodeCarrierEntities);
  }

  private void handleEmptyNodeCarrier(
      Optional<NodeCarriersEntity> entity,
      String orgId,
      String nodeId,
      String carrierServiceId,
      String serviceOption)
      throws CommonServiceException {
    if (entity.isEmpty()) {
      logger.error(NODE_CARRIER_NOT_FOUND_ERROR_MSG);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      errorMap.put(SERVICE_OPTION, FieldError.builder().rejectedValue(serviceOption).build());
      throw new CommonServiceException(
          NODE_CARRIER_NOT_FOUND_ERROR_MSG, HttpStatus.NOT_FOUND, 0x1781, errorMap);
    }
  }

  @Override
  public List<NodeCarriersResponse> getNodeCarriersListByOrgIdAndNodeId(String orgId, String nodeId)
      throws CommonServiceException {
    return INSTANCE.toNodeCarriersResponseList(
        nodeCarriersDomain.findByOrgIdAndNodeId(orgId, nodeId));
  }

  @Override
  public List<String> getListOfCarrierServiceNameByOrgIdAndNodeId(String orgId, String nodeId)
      throws CommonServiceException {
    return getNodeCarriersListByOrgIdAndNodeId(orgId, nodeId).stream()
        .map(NodeCarriersResponse::getCarrierServiceId)
        .distinct()
        .toList();
  }

  @Override
  public List<NodeCarriersResponse> getAllNodeCarriersByOrgIdCarrierServiceId(
      String orgId, String carrierServiceId) throws CommonServiceException {
    List<NodeCarriersEntity> nodeCarriersEntityList =
        nodeCarriersDomain.getAllNodeCarriersByOrgIdCarrierServiceId(orgId, carrierServiceId);
    return INSTANCE.toNodeCarriersResponseList(nodeCarriersEntityList);
  }

  @Override
  public List<NodeCarriersResponse> getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
      String orgId, String nodeId, String carrierServiceId) throws CommonServiceException {
    List<NodeCarriersEntity> nodeCarriersEntityList =
        nodeCarriersDomain.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
            orgId, nodeId, carrierServiceId);
    return INSTANCE.toNodeCarriersResponseList(nodeCarriersEntityList);
  }
}
