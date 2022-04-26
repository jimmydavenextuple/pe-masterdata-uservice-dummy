package com.nextuple.nodde.spring.cache.service;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.node.outbound.NodeValidationResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.nodde.spring.cache.feign.NodeFeignImpl;
import com.nextuple.node.cache.domain.NodeCacheKey;
import com.nextuple.node.cache.domain.NodeCacheValue;
import com.nextuple.node.cache.domain.NodeValidationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        NodeCacheKey, NodeCacheValue, NodeValidationRequest, BaseResponse<NodeValidationResponse>> {

  @Autowired NodeFeignImpl nodeFeign;

  @Autowired
  GenericMapper<
          NodeCacheKey, NodeCacheValue, NodeValidationRequest, BaseResponse<NodeValidationResponse>>
      nodeMapper;

  @Override
  public NodeCacheValue get(NodeCacheKey key) {
    try {
      NodeCacheValue nodeCacheValue =
          nodeMapper.responseToCacheValue(nodeFeign.get(nodeMapper.cacheKeyToRequest(key)));
      Boolean isValid = nodeCacheValue.getNodeDetails().getNodes().get(0).getValidationPassed();
      if (Boolean.TRUE.equals(isValid)) {
        return nodeCacheValue;
      }
      return null;
    } catch (RuntimeException ex) {
      return null;
    }
  }
}
