package com.nextuple.node.data.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.domain.node.NodeResponse;
import com.nextuple.node.data.cache.domain.NodeDataCacheKey;
import com.nextuple.node.data.cache.domain.NodeDataCacheValue;
import com.nextuple.node.data.spring.cache.feign.NodeDataFeignImpl;
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
