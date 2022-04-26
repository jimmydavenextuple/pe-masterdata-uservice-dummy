package com.nextuple.nodde.spring.cache.mapper;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.node.NodeValidationDetails;
import com.nextuple.core.node.outbound.NodeValidationResponse;
import com.nextuple.node.cache.domain.NodeCacheKey;
import com.nextuple.node.cache.domain.NodeCacheValue;
import com.nextuple.node.cache.domain.NodeValidationRequest;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class NodeMapper
    implements GenericMapper<
        NodeCacheKey, NodeCacheValue, NodeValidationRequest, BaseResponse<NodeValidationResponse>> {
  public static final DataMapper DATA_MAPPER = Mappers.getMapper(DataMapper.class);

  @Override
  public NodeCacheKey requestToCacheKey(NodeValidationRequest request) {
    return null;
  }

  @Override
  public NodeValidationRequest cacheKeyToRequest(NodeCacheKey cacheKey) {
    String nodeNo = cacheKey.getNodeTenantDetails().getNodeNo();
    String tenantId = cacheKey.getNodeTenantDetails().getTenantId();
    List<NodeValidationDetails> nodeValidationDetails = new ArrayList<>();
    NodeValidationDetails nodeValidationDetail = new NodeValidationDetails();
    nodeValidationDetail.setNodeNo(nodeNo);
    nodeValidationDetail.setTenantId(tenantId);
    nodeValidationDetails.add(nodeValidationDetail);
    return NodeValidationRequest.builder().nodes(nodeValidationDetails).build();
  }

  @Override
  public NodeCacheValue responseToCacheValue(BaseResponse<NodeValidationResponse> resp) {
    return NodeCacheValue.builder()
        .nodeDetails(DATA_MAPPER.toNodeCacheValue(resp.getPayload())) // Check this
        .build();
  }

  @Override
  public BaseResponse<NodeValidationResponse> cacheValueToResponse(NodeCacheValue cacheValue) {
    return null;
  }
}
