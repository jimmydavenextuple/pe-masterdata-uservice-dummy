package com.hbc.node.data.spring.cache.service;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.hbc.node.data.cache.domain.NodeDataCacheKey;
import com.hbc.node.data.cache.domain.NodeDataCacheValue;
import com.hbc.node.data.spring.cache.feign.NodeDataFeignImpl;
import com.hbc.node.domain.outbound.NodeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeDataFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        NodeDataCacheKey, NodeDataCacheValue, String, BaseResponse<NodeResponse>> {

  @Autowired NodeDataFeignImpl nodeFeign;

  @Autowired
  GenericMapper<NodeDataCacheKey, NodeDataCacheValue, String, BaseResponse<NodeResponse>>
      nodeMapper;

  @Override
  public NodeDataCacheValue get(NodeDataCacheKey key) {
    try {
      return nodeMapper.responseToCacheValue(
          nodeFeign.getNodeDetails(key.getNodeId(), key.getOrgId()));
    } catch (RuntimeException ex) {
      return null;
    }
  }
}
