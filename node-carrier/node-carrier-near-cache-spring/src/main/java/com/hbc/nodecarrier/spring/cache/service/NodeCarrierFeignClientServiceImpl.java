package com.hbc.nodecarrier.spring.cache.service;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.nodecarrier.cache.domain.NodeCarrierCacheKey;
import com.hbc.nodecarrier.cache.domain.NodeCarrierCacheValue;
import com.hbc.nodecarrier.spring.cache.feign.NodeCarrierFeignImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeCarrierFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        NodeCarrierCacheKey, NodeCarrierCacheValue, String, BaseResponse<NodeCarrierResponse>> {

  @Autowired NodeCarrierFeignImpl nodeCarrierFeign;

  @Autowired
  GenericMapper<
          NodeCarrierCacheKey, NodeCarrierCacheValue, String, BaseResponse<NodeCarrierResponse>>
      nodeCarrierMapper;

  @Override
  public NodeCarrierCacheValue get(NodeCarrierCacheKey key) {
    try {
      return nodeCarrierMapper.responseToCacheValue(
          nodeCarrierFeign.getNodeCarrier(
              key.getNodeId(), key.getOrgId(), key.getCarrierServiceId(), key.getServiceOption()));
    } catch (RuntimeException e) {
      return null;
    }
  }
}
