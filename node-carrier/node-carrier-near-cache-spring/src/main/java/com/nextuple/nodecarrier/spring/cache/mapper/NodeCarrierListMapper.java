package com.nextuple.nodecarrier.spring.cache.mapper;

import static com.nextuple.nodecarrier.spring.cache.mapper.NodeCarrierMapper.DATA_MAPPER;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierListCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierListCacheValue;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class NodeCarrierListMapper
    implements GenericMapper<
        NodeCarrierListCacheKey,
        NodeCarrierListCacheValue,
        String,
        BaseResponse<List<NodeCarrierResponse>>> {

  @Override
  public NodeCarrierListCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(NodeCarrierListCacheKey cacheKey) {
    return null;
  }

  @Override
  public NodeCarrierListCacheValue responseToCacheValue(
      BaseResponse<List<NodeCarrierResponse>> resp) {
    return NodeCarrierListCacheValue.builder()
        .nodeCarrierDetailsList(DATA_MAPPER.convertToNodeCarrierCacheValue(resp.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<List<NodeCarrierResponse>> cacheValueToResponse(
      NodeCarrierListCacheValue cacheValue) {
    return null;
  }
}
