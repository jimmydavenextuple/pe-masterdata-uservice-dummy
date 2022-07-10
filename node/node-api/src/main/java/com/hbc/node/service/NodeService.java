package com.hbc.node.service;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.node.domain.NodeDomain;
import com.hbc.node.domain.entity.NodeEntity;
import com.hbc.node.domain.inbound.NodeRequest;
import com.hbc.node.domain.inbound.NodeUpdationRequest;
import com.hbc.node.domain.mapper.NodeMapper;
import com.hbc.node.domain.outbound.NodeResponse;
import com.hbc.node.exception.NodeDomainException;
import java.util.HashMap;
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
public class NodeService {

  private static final Logger logger = LoggerFactory.getLogger(NodeService.class);
  private static final String ORG_ID = "orgId";
  private static final String NODE_ID = "nodeId";

  private final NodeDomain nodeDomain;

  public static final NodeMapper INSTANCE = Mappers.getMapper(NodeMapper.class);

  private static final String NODE_EXCEPTION_MESSAGE = "Node not found with given details";

  public NodeResponse createNode(NodeRequest nodeRequest) throws NodeDomainException {

    var nodeEntity = INSTANCE.nodeRequestToNodeEntity(nodeRequest);

    return INSTANCE.toNodeResponse(nodeDomain.saveNodeEntity(nodeEntity));
  }

  public NodeResponse updateNodeDetails(
      String nodeId, String orgId, NodeUpdationRequest nodeUpdationRequest)
      throws NodeDomainException, CommonServiceException {

    Optional<NodeEntity> existingNodeEntity = nodeDomain.findNodeByNodeIdAndOrgId(nodeId, orgId);

    if (existingNodeEntity.isEmpty()) {
      logger.error(NODE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      throw new CommonServiceException(
          NODE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    INSTANCE.updateNodeEntity(nodeUpdationRequest, existingNodeEntity.get());
    return INSTANCE.toNodeResponse(nodeDomain.saveNodeEntity(existingNodeEntity.get()));
  }

  public NodeResponse getNodeDetails(String nodeId, String orgId)
      throws NodeDomainException, CommonServiceException {

    Optional<NodeEntity> nodeEntity = nodeDomain.findNodeByNodeIdAndOrgId(nodeId, orgId);

    if (nodeEntity.isEmpty()) {
      logger.error(NODE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      throw new CommonServiceException(
          NODE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return INSTANCE.toNodeResponse(nodeEntity.get());
  }

  public NodeResponse deleteNode(String nodeId, String orgId)
      throws NodeDomainException, CommonServiceException {

    Optional<NodeEntity> nodeEntity = nodeDomain.findNodeByNodeIdAndOrgId(nodeId, orgId);

    if (nodeEntity.isEmpty()) {
      logger.error(NODE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      throw new CommonServiceException(
          NODE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    var nodeResponse = INSTANCE.toNodeResponse(nodeEntity.get());
    nodeDomain.deleteNode(nodeEntity.get());
    return nodeResponse;
  }
}
