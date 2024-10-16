/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.service.impl;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.carrier.domain.NodeServiceOptionsDomain;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionEntity;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionUpdateRequest;
import com.nextuple.node.carrier.domain.mapper.NodeServiceOptionMapper;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionResponse;
import com.nextuple.node.carrier.exception.InvalidDataException;
import com.nextuple.node.carrier.service.NodeServiceOptionsService;
import com.nextuple.node.carrier.service.ValidationService;
import jakarta.transaction.Transactional;
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
public class NodeServiceOptionsServiceImpl implements NodeServiceOptionsService {

  private static final Logger logger = LoggerFactory.getLogger(NodeServiceOptionsServiceImpl.class);
  private static final String ORG_ID = "orgId";
  private static final String NODE_ID = "nodeId";
  private static final String SERVICE_OPTION = "serviceOption";
  private static final String NODE_SERVICE_OPTION_NOT_FOUND_ERROR_MSG =
      "Node Service Option not found for given details";

  private final NodeServiceOptionsDomain nodeServiceOptionsDomain;

  private final ValidationService validationService;
  public static final NodeServiceOptionMapper INSTANCE =
      Mappers.getMapper(NodeServiceOptionMapper.class);

  @Transactional
  public NodeServiceOptionResponse createNodeServiceOption(
      NodeServiceOptionRequest nodeServiceOptionRequest)
      throws CommonServiceException, InvalidDataException {
    validationService.validate(nodeServiceOptionRequest);
    var nodeServiceOptionEntity =
        INSTANCE.nodeServiceOptionRequestToEntity(nodeServiceOptionRequest);
    return INSTANCE.toNodeServiceOptionResponse(
        nodeServiceOptionsDomain.saveNodeServiceOptionEntity(nodeServiceOptionEntity));
  }

  @Transactional
  public NodeServiceOptionResponse updateNodeServiceOption(
      String orgId,
      String nodeId,
      String serviceOption,
      NodeServiceOptionUpdateRequest nodeServiceOptionUpdateRequest)
      throws CommonServiceException, InvalidDataException {

    validationService.validate(nodeServiceOptionUpdateRequest);

    Optional<NodeServiceOptionEntity> existingNodeServiceOptionEntity =
        nodeServiceOptionsDomain.findNodeServiceOptionEntity(orgId, nodeId, serviceOption);

    handleEmptyNodeServiceOption(existingNodeServiceOptionEntity, orgId, nodeId, serviceOption);

    INSTANCE.toUpdateNodeServiceOption(
        nodeServiceOptionUpdateRequest, existingNodeServiceOptionEntity.get());
    return INSTANCE.toNodeServiceOptionResponse(
        nodeServiceOptionsDomain.saveNodeServiceOptionEntity(
            existingNodeServiceOptionEntity.get()));
  }

  @Transactional
  public NodeServiceOptionResponse getNodeServiceOption(
      String orgId, String nodeId, String serviceOption) throws CommonServiceException {
    Optional<NodeServiceOptionEntity> existingNodeServiceOptionEntity =
        nodeServiceOptionsDomain.findNodeServiceOptionEntity(orgId, nodeId, serviceOption);

    handleEmptyNodeServiceOption(existingNodeServiceOptionEntity, orgId, nodeId, serviceOption);

    return INSTANCE.toNodeServiceOptionResponse(existingNodeServiceOptionEntity.get());
  }

  @Transactional
  public NodeServiceOptionResponse deleteNodeServiceOption(
      String orgId, String nodeId, String serviceOption) throws CommonServiceException {
    Optional<NodeServiceOptionEntity> existingNodeServiceOptionEntity =
        nodeServiceOptionsDomain.findNodeServiceOptionEntity(orgId, nodeId, serviceOption);

    handleEmptyNodeServiceOption(existingNodeServiceOptionEntity, orgId, nodeId, serviceOption);

    NodeServiceOptionResponse deletedNodeCarrier =
        INSTANCE.toNodeServiceOptionResponse(existingNodeServiceOptionEntity.get());

    nodeServiceOptionsDomain.deleteNodeServiceOptionEntityByOrgIdAndNodeIdAndServiceOption(
        existingNodeServiceOptionEntity.get().getOrgId(),
        existingNodeServiceOptionEntity.get().getNodeId(),
        existingNodeServiceOptionEntity.get().getServiceOption());

    return deletedNodeCarrier;
  }

  @Override
  public List<NodeServiceOptionResponse> getNodeServiceOptionList(String orgId, String nodeId)
      throws CommonServiceException {
    return INSTANCE.toNodeServiceOptionResponseList(
        nodeServiceOptionsDomain.findByOrgIdAndNodeId(orgId, nodeId));
  }

  private void handleEmptyNodeServiceOption(
      Optional<NodeServiceOptionEntity> entity, String orgId, String nodeId, String serviceOption)
      throws CommonServiceException {
    if (entity.isEmpty()) {
      logger.error(NODE_SERVICE_OPTION_NOT_FOUND_ERROR_MSG);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      errorMap.put(SERVICE_OPTION, FieldError.builder().rejectedValue(serviceOption).build());
      throw new CommonServiceException(
          NODE_SERVICE_OPTION_NOT_FOUND_ERROR_MSG, HttpStatus.NOT_FOUND, 0x1782, errorMap);
    }
  }
}
