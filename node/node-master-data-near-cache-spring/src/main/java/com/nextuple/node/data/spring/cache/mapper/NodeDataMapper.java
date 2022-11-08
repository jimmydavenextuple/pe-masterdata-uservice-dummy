package com.nextuple.node.data.spring.cache.mapper;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.node.data.cache.domain.NodeDataCacheKey;
import com.nextuple.node.data.cache.domain.NodeDataCacheValue;
import com.nextuple.node.domain.outbound.NodeResponse;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class NodeDataMapper
    implements GenericMapper<
        NodeDataCacheKey, NodeDataCacheValue, String, BaseResponse<NodeResponse>> {

  public static final DataMapper DATA_MAPPER = Mappers.getMapper(DataMapper.class);

  @Override
  public NodeDataCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(NodeDataCacheKey cacheKey) {
    return null;
  }

  @Override
  public NodeDataCacheValue responseToCacheValue(BaseResponse<NodeResponse> resp) {

    return DATA_MAPPER.toNodeCacheValue(resp.getPayload());
  }

  @Override
  public BaseResponse<NodeResponse> cacheValueToResponse(NodeDataCacheValue cacheValue) {
    return null;
  }
}
