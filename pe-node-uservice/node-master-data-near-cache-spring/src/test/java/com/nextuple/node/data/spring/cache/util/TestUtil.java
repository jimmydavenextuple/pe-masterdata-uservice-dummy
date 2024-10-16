/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.data.spring.cache.util;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.data.cache.domain.NodeDataCacheKey;
import com.nextuple.node.data.cache.domain.NodeDataCacheValue;
import com.nextuple.node.domain.outbound.NodeResponse;
import java.util.Map;

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
    nodeDataCacheValue.setServiceOptionEligibilities(getServiceOptionEligibilities());
    return nodeDataCacheValue;
  }

  public BaseResponse<NodeResponse> getNodeResponse() {
    BaseResponse<NodeResponse> baseResponse = new BaseResponse<>();
    NodeResponse nodeResponse = NodeResponse.builder().build();
    nodeResponse.setNodeId("node-1");
    nodeResponse.setOrgId("org-1");
    nodeResponse.setBopisEligible(Boolean.TRUE);
    nodeResponse.setServiceOptionEligibilities(getServiceOptionEligibilities());
    baseResponse.setMessage("Node details fetched successfully");
    baseResponse.setPayload(nodeResponse);
    return baseResponse;
  }

  public Map<String, Boolean> getServiceOptionEligibilities() {
    return Map.of(
        "sdndEligible", Boolean.TRUE,
        "expressEligible", Boolean.TRUE,
        "nextdayEligible", Boolean.TRUE);
  }
}
