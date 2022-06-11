package com.nextuple.nodecarrier.spring.cache.mapper;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierCacheValue;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierResponse;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class NodeCarrierMapper
    implements GenericMapper<
        NodeCarrierCacheKey, NodeCarrierCacheValue, String, BaseResponse<NodeCarrierResponse>> {

  public static final DataMapper DATA_MAPPER = Mappers.getMapper(DataMapper.class);

  @Override
  public NodeCarrierCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(NodeCarrierCacheKey cacheKey) {
    return null;
  }

  @Override
  public NodeCarrierCacheValue responseToCacheValue(BaseResponse<NodeCarrierResponse> resp) {
    return NodeCarrierCacheValue.builder()
        .nodeCarrierDetails(DATA_MAPPER.convertToNodeCarrierCacheValue(resp.getPayload()))
        .build();
  }

  @Override
  public BaseResponse<NodeCarrierResponse> cacheValueToResponse(NodeCarrierCacheValue cacheValue) {
    return null;
  }
}
