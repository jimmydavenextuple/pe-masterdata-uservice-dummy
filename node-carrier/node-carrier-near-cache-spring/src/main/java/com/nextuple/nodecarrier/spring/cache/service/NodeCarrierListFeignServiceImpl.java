package com.nextuple.nodecarrier.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierListCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierListCacheValue;
import com.nextuple.nodecarrier.spring.cache.feign.NodeCarrierListFeignImpl;
import com.nextuple.nodecarrier.spring.cache.mapper.NodeCarrierListMapper;
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
