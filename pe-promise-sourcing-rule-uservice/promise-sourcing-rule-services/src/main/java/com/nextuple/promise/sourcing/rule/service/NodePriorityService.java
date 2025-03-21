/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NodePriorityRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NodePriorityUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeDetail;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupWithNodesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodePriorityResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.NodePriorityInfo;
import com.nextuple.promise.sourcing.rule.domain.mapper.NodePriorityMapper;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodeGroupDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodePriorityDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.NodeGroupPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.NodePriorityPersistenceService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodePriorityService {

  private static final Logger logger = LoggerFactory.getLogger(NodePriorityService.class);

  private static final String ORG_ID = "orgId";
  private static final String ID = "id";
  private static final String NODE_GROUP_ID = "nodeGroupId";
  private static final String NODE_GROUP_NAME = "nodeGroupName";
  private static final String NODE_ID = "nodeId";
  private static final String NODE_GROUP_DETAIL_EXCEPTION_MESSAGE =
      "Node group details not found for given orgId , nodeGroupId and nodeId";
  private static final NodePriorityMapper INSTANCE = Mappers.getMapper(NodePriorityMapper.class);
  private final NodePriorityPersistenceService nodePriorityPersistenceService;
  private final NodeGroupPersistenceService nodeGroupPersistenceService;
  @Autowired private final NodeFeign nodeFeign;

  public NodePriorityResponse processAddNodePriorityToNodeGroup(
      NodePriorityRequest nodePriorityRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processAddNodePriorityToNodeGroup service --");
    validateNodeGroupId(nodePriorityRequest.getNodeGroupId(), nodePriorityRequest.getOrgId());
    validateNodeId(nodePriorityRequest.getNodeId(), nodePriorityRequest.getOrgId());
    var nodeGroupDetails = INSTANCE.toNodePriorityEntity(nodePriorityRequest);
    return INSTANCE.toNodePriorityResponse(
        nodePriorityPersistenceService.saveNodePriorityEntity(nodeGroupDetails));
  }

  public NodeGroupDomainDto validateNodeGroupId(Long nodeGroupId, String orgId)
      throws CommonServiceException, PromiseEngineException {
    Optional<NodeGroupDomainDto> existingNodeGroupDomainDto =
        nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(nodeGroupId, orgId);
    if (existingNodeGroupDomainDto.isEmpty()) {
      logger.error("Node Group and OrgId does not exist");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(NODE_GROUP_ID, FieldError.builder().rejectedValue(nodeGroupId).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          "Node Group and OrgId does not exist", HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return existingNodeGroupDomainDto.get();
  }

  public List<NodePriorityDomainDto> getNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(
      String orgId, Long nodeGroupId, String nodeId) throws PromiseEngineException {
    return nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(
        orgId, nodeGroupId, nodeId);
  }

  public void validateNodeId(String nodeId, String orgId) throws CommonServiceException {
    try {
      BaseResponse<NodeResponse> nodeResponse = nodeFeign.getNodeDetails(nodeId, orgId);

      if (!Objects.isNull(nodeResponse.getPayload())
          && Boolean.FALSE.equals(nodeResponse.getPayload().getIsActive())) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
        errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
        throw new CommonServiceException(
            "Cannot add node to node group as node is inactive",
            HttpStatus.BAD_REQUEST,
            0x1771,
            errorMap);
      }
    } catch (FeignException e) {
      logger.error("Invalid node id", e);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      throw new CommonServiceException("Invalid node id", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  public NodeGroupDetailsResponse processGetNodesAssociatedToANodeGroup(
      String orgId, Long nodeGroupId) throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processGetNodesAssociatedToANodeGroup service --");
    NodeGroupDomainDto nodeGroupDomainDto = validateNodeGroupId(nodeGroupId, orgId);
    List<NodePriorityDomainDto> nodePriorityDomainDtoList =
        nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            orgId, nodeGroupId);
    List<NodePriorityInfo> nodePriorityInfoList = new ArrayList<>();
    if (!nodePriorityDomainDtoList.isEmpty()) {
      for (NodePriorityDomainDto nodePriorityDomainDto : nodePriorityDomainDtoList) {
        NodePriorityInfo nodePriorityInfo = new NodePriorityInfo();
        nodePriorityInfo.setNodeId(nodePriorityDomainDto.getNodeId());
        nodePriorityInfo.setPriority(nodePriorityDomainDto.getPriority());
        nodePriorityInfo.setCustomAttributes(nodeGroupDomainDto.getCustomAttributes());
        nodePriorityInfoList.add(nodePriorityInfo);
      }
    } else {
      logger.error("No nodes associated to the given node group: {}", nodeGroupId);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_GROUP_ID, FieldError.builder().rejectedValue(nodeGroupId).build());
      throw new CommonServiceException(
          "No nodes associated to the given node group", HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return NodeGroupDetailsResponse.builder()
        .orgId(orgId)
        .nodeGroupId(nodeGroupId)
        .nodeGroupName(nodeGroupDomainDto.getNodeGroupName())
        .nodeInfo(nodePriorityInfoList)
        .customAttributes(nodeGroupDomainDto.getCustomAttributes())
        .build();
  }

  public NodePriorityResponse processGetNodePriorityDetailsByIdAndOrgId(Long id, String orgId)
      throws PromiseEngineException, CommonServiceException {

    Optional<NodePriorityDomainDto> nodePriorityDomainDto =
        nodePriorityPersistenceService.fetchNodePriorityEntityByIdAndOrgId(id, orgId);
    if (nodePriorityDomainDto.isEmpty()) {
      logger.error("Node group details not found for given id and orgId");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          "Node group details not found for given id and orgId",
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
    return INSTANCE.toNodePriorityResponse(nodePriorityDomainDto.get());
  }

  public NodePriorityResponse processUpdateNodePriorityWithinNodeGroup(
      String orgId,
      Long nodeGroupId,
      String nodeId,
      NodePriorityUpdationRequest nodePriorityUpdationRequest)
      throws PromiseEngineException, CommonServiceException {
    List<NodePriorityDomainDto> nodePriorityDomainDtoList =
        getNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(orgId, nodeGroupId, nodeId);
    if (nodePriorityDomainDtoList.isEmpty()) {
      logger.error(NODE_GROUP_DETAIL_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_GROUP_ID, FieldError.builder().rejectedValue(nodeGroupId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      throw new CommonServiceException(
          NODE_GROUP_DETAIL_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    INSTANCE.updateNodePriorityEntity(
        nodePriorityUpdationRequest, nodePriorityDomainDtoList.get(0));
    return INSTANCE.toNodePriorityResponse(
        nodePriorityPersistenceService.saveNodePriorityEntity(nodePriorityDomainDtoList.get(0)));
  }

  public NodePriorityResponse processRemoveNodeFromNodeGroup(
      String orgId, Long nodeGroupId, String nodeId)
      throws PromiseEngineException, CommonServiceException {

    List<NodePriorityDomainDto> nodePriorityDomainDtoList =
        getNodePriorityListByOrgIdAndNodeGroupIdAndNodeId(orgId, nodeGroupId, nodeId);
    if (nodePriorityDomainDtoList.isEmpty()) {
      logger.error(NODE_GROUP_DETAIL_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_GROUP_ID, FieldError.builder().rejectedValue(nodeGroupId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      throw new CommonServiceException(
          NODE_GROUP_DETAIL_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    var nodeGroupDetailsResponse =
        INSTANCE.toNodePriorityResponse(nodePriorityDomainDtoList.get(0));
    nodePriorityPersistenceService.deleteNodePriorityEntity(nodePriorityDomainDtoList.get(0));
    return nodeGroupDetailsResponse;
  }

  public List<NodePriorityDomainDto> processRemoveAllNodesFromNodeGroup(
      String orgId, Long nodeGroupId) throws PromiseEngineException {
    return nodePriorityPersistenceService.deleteNodePriorityByOrgIdAndNodeGroupId(
        orgId, nodeGroupId);
  }

  public List<NodePriorityDomainDto> getNodePriorityListByNodeGroupIds(
      List<Long> nodeGroupEntityIds) {
    return nodePriorityPersistenceService.findByNodeGroupIdsInOrderByPriority(nodeGroupEntityIds);
  }

  public List<NodePriorityResponse> getNodePriorityListByNodeIdAndOrgId(String nodeId, String orgId)
      throws PromiseEngineException, CommonServiceException {
    List<NodePriorityDomainDto> nodePriorityDomainDtoList =
        nodePriorityPersistenceService.fetchNodePriorityListByNodeIdAndOrgId(nodeId, orgId);
    if (nodePriorityDomainDtoList.isEmpty()) {
      logger.error(
          "Node group details not found for given nodeid:" + nodeId + " and orgId:" + orgId);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      throw new CommonServiceException(
          "Node group details not found for given id and orgId",
          HttpStatus.NOT_FOUND,
          0x2711,
          errorMap);
    }
    return INSTANCE.toNodePriorityResponseList(nodePriorityDomainDtoList);
  }

  public List<NodePriorityDomainDto> deleteNodePriorityAssociatedToNodeGroup(
      String orgId, Long nodeGroupId) throws PromiseEngineException {
    List<NodePriorityDomainDto> nodePriorityEntityList =
        nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            orgId, nodeGroupId);
    if (nodePriorityEntityList.isEmpty()) {
      return Collections.emptyList();
    }
    return nodePriorityPersistenceService.deleteNodePriorityByOrgIdAndNodeGroupId(
        orgId, nodeGroupId);
  }

  @Transactional
  public NodeGroupWithNodesResponse editNodesWithinNodeGroup(
      String orgId, String nodeGroupName, NodeGroupWithNodesResponse nodeGroupWithNodes)
      throws PromiseEngineException, CommonServiceException {

    List<NodeGroupDomainDto> nodeGroupDomainDtoList =
        nodeGroupPersistenceService.fetchNodeGroupByOrgIdAndName(orgId, nodeGroupName);

    Long nodeGroupId;
    if (!nodeGroupDomainDtoList.isEmpty()) {
      nodeGroupId = nodeGroupDomainDtoList.get(0).getId();
    } else {
      logger.error("Node group with given nodeGroupName and orgId does not exist");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(NODE_GROUP_NAME, FieldError.builder().rejectedValue(nodeGroupName).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          "Node group with given nodeGroupName and orgId does not exist",
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }

    NodeGroupWithNodesResponse nodeGroupWithNodesResponse = new NodeGroupWithNodesResponse();
    nodeGroupWithNodesResponse.setNodeGroupName(nodeGroupName);
    nodeGroupWithNodesResponse.setNodes(new ArrayList<>());

    deleteNodePriorityAssociatedToNodeGroup(orgId, nodeGroupId);
    List<NodeDetail> validNodes = new ArrayList<>();
    for (NodeDetail nodeDetail : nodeGroupWithNodes.getNodes()) {
      NodePriorityRequest nodePriorityRequest =
          new NodePriorityRequest(
              orgId, nodeDetail.getNodeId(), nodeDetail.getSequence(), nodeGroupId);
      Boolean isNodeValid = checkForNodeValidation(nodePriorityRequest);
      if (Boolean.TRUE.equals(isNodeValid)) {
        var nodeGroupDetails = INSTANCE.toNodePriorityEntity(nodePriorityRequest);
        INSTANCE.toNodePriorityResponse(
            nodePriorityPersistenceService.saveNodePriorityEntity(nodeGroupDetails));
        NodeDetail validNodeDetails =
            new NodeDetail(nodeDetail.getNodeId(), nodeDetail.getSequence());
        validNodes.add(validNodeDetails);
      }
    }
    nodeGroupWithNodesResponse.setNodes(validNodes);
    return nodeGroupWithNodesResponse;
  }

  @Transactional
  public List<NodePriorityDomainDto> deleteNodesFromTheNodeGroup(String orgId, Long nodeGroupId)
      throws PromiseEngineException, CommonServiceException {
    Optional<NodeGroupDomainDto> nodeGroupEntityOptional =
        nodeGroupPersistenceService.fetchNodeGroupByIdAndOrgId(nodeGroupId, orgId);
    if (nodeGroupEntityOptional.isEmpty()) {
      logger.error("Node Group not found for {} and {}", orgId, nodeGroupId);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(nodeGroupId).build());
      throw new CommonServiceException(
          "Node Group not found", HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return deleteNodePriorityAssociatedToNodeGroup(orgId, nodeGroupId);
  }

  public boolean checkForNodeValidation(NodePriorityRequest nodePriorityRequest) {
    try {
      nodeFeign.getNodeDetails(nodePriorityRequest.getNodeId(), nodePriorityRequest.getOrgId());
    } catch (FeignException e) {
      logger.error(
          "Node not found with given orgId :{} and nodeId:{}",
          nodePriorityRequest.getNodeId(),
          nodePriorityRequest.getOrgId());
      return false;
    }
    return true;
  }
}
