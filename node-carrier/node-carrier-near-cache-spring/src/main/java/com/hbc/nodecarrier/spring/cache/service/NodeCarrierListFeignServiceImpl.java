package com.hbc.nodecarrier.spring.cache.service;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.nodecarrier.cache.domain.NodeCarrierListCacheKey;
import com.hbc.nodecarrier.cache.domain.NodeCarrierListCacheValue;
import com.hbc.nodecarrier.spring.cache.feign.NodeCarrierListFeignImpl;
import com.hbc.nodecarrier.spring.cache.mapper.NodeCarrierListMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeCarrierListFeignServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        NodeCarrierListCacheKey,
        NodeCarrierListCacheValue,
        String,
        BaseResponse<List<NodeCarrierResponse>>> {

  @Autowired NodeCarrierListFeignImpl nodeCarrierFeign;

  @Autowired NodeCarrierListMapper nodeCarrierMapper;

  @Override
  public NodeCarrierListCacheValue get(NodeCarrierListCacheKey key) {
    try {
      return nodeCarrierMapper.responseToCacheValue(
          nodeCarrierFeign.getNodeCarrierList(
              key.getNodeId(), key.getOrgId(), key.getServiceOption()));
    } catch (RuntimeException e) {
      return null;
    }
  }
}
