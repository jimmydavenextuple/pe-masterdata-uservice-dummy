package com.nextuple.node.data.spring.cache.util;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.domain.node.NodeResponse;
import com.nextuple.node.data.cache.domain.NodeDataCacheKey;
import com.nextuple.node.data.cache.domain.NodeDataCacheValue;

public class TestUtil {

  public NodeDataCacheKey getNodeCacheKey() {
    NodeDataCacheKey nodeDataCacheKey = NodeDataCacheKey.builder().build();
    nodeDataCacheKey.setNodeId("node-1");
    nodeDataCacheKey.setOrgId("org-1");
    return nodeDataCacheKey;
  }

  public NodeDataCacheValue getNodeCacheValue() {
    NodeDataCacheValue nodeDataCacheValue = NodeDataCacheValue.builder().build();
    nodeDataCacheValue.setNodeId("node-1");
    nodeDataCacheValue.setOrgId("org-1");
    nodeDataCacheValue.setBopisEligible(Boolean.TRUE);
    nodeDataCacheValue.setExpressEligible(Boolean.TRUE);
    return nodeDataCacheValue;
  }

  public BaseResponse<NodeResponse> getNodeResponse() {
    BaseResponse<NodeResponse> baseResponse = new BaseResponse<>();
    NodeResponse nodeResponse = NodeResponse.builder().build();
    nodeResponse.setNodeId("node-1");
    nodeResponse.setOrgId("org-1");
    nodeResponse.setBopisEligible(Boolean.TRUE);
    nodeResponse.setExpressEligible(Boolean.TRUE);
    baseResponse.setMessage("Node details fetched successfully");
    baseResponse.setPayload(nodeResponse);
    return baseResponse;
  }
}
