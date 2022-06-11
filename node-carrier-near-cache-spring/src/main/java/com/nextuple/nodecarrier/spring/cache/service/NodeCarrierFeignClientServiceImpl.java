package com.nextuple.nodecarrier.spring.cache.service;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierCacheValue;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierResponse;
import com.nextuple.nodecarrier.spring.cache.feign.NodeCarrierFeignImpl;
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
