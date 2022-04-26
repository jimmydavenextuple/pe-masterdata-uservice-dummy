package com.nextuple.node.spring.cache.util;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.node.NodeValidationDetails;
import com.nextuple.core.node.outbound.NodeValidationResponse;
import com.nextuple.core.tenant.TenantDetails;
import com.nextuple.core.tenant.enums.TenantStatusEnum;
import com.nextuple.node.cache.domain.NodeCacheKey;
import com.nextuple.node.cache.domain.NodeCacheValue;
import com.nextuple.node.cache.domain.NodeDetails;
import com.nextuple.node.cache.domain.NodeTenantDetails;
import com.nextuple.node.cache.domain.NodeValidationRequest;
import java.util.ArrayList;
import java.util.List;

public class TestUtil {
  public NodeCacheKey getNodeCacheKey(String nodeNo, String tenantId) {
    NodeTenantDetails nodeTenantDetails =
        NodeTenantDetails.builder().nodeNo(nodeNo).tenantId(tenantId).build();
    NodeCacheKey nodeCacheKey = NodeCacheKey.builder().nodeTenantDetails(nodeTenantDetails).build();
    nodeCacheKey.setNodeTenantDetails(nodeTenantDetails);
    return nodeCacheKey;
  }

  private TenantDetails getTenantDetails(String tenantId, String tenantName) {
    TenantDetails tenantDetails = new TenantDetails();
    tenantDetails.setTenantId(tenantId);
    tenantDetails.setTenantName(tenantName);
    tenantDetails.setStatus(TenantStatusEnum.ACTIVATED);
    return tenantDetails;
  }

  public NodeCacheValue getNodeCacheValue(String nodeNo, String nodeName) {
    NodeCacheValue nodeCacheValue = NodeCacheValue.builder().build();
    NodeDetails nodeDetails = NodeDetails.builder().build();
    nodeDetails.setNodeNo(nodeNo);
    nodeDetails.setNodeName(nodeName);
    nodeCacheValue.setNodeDetails(nodeDetails);
    return nodeCacheValue;
  }

  public NodeValidationResponse getNodeValidationResponse(String nodeNo, String nodeName) {
    NodeValidationResponse nodeValidationResponse = new NodeValidationResponse();
    List<NodeValidationDetails> nodes = new ArrayList<>();
    NodeValidationDetails nodeValidationDetails = new NodeValidationDetails();
    nodeValidationDetails.setNodeNo(nodeNo);
    nodeValidationDetails.setNodeName(nodeName);
    nodeValidationDetails.setValidationPassed(true);
    nodeValidationDetails.setTenantId("Nextuple");
    nodes.add(0, nodeValidationDetails);
    nodeValidationResponse.setNodes(nodes);
    return nodeValidationResponse;
  }

  public NodeValidationRequest getNodeValidationRequest(String nodeNo, String nodeName) {
    NodeValidationRequest nodeValidationRequest = new NodeValidationRequest();
    nodeValidationRequest.setNodes(getNodeValidationResponse(nodeNo, nodeName).getNodes());
    return nodeValidationRequest;
  }

  public BaseResponse<NodeValidationResponse> getBaseResponseOfNodeValidationResponse(
      String nodeNo, String nodeName) {
    BaseResponse<NodeValidationResponse> response = new BaseResponse<>();
    response.setMessage("Node details fetched successfully");
    response.setPayload(getNodeValidationResponse(nodeNo, nodeName));
    return response;
  }
}
