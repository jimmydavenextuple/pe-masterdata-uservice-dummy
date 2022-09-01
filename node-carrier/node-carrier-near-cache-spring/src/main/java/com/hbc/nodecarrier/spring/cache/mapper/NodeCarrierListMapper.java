package com.hbc.nodecarrier.spring.cache.mapper;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.nodecarrier.cache.domain.NodeCarrierListCacheKey;
import com.hbc.nodecarrier.cache.domain.NodeCarrierListCacheValue;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.hbc.nodecarrier.spring.cache.mapper.NodeCarrierMapper.DATA_MAPPER;

@Component
public class NodeCarrierListMapper implements GenericMapper<
        NodeCarrierListCacheKey, NodeCarrierListCacheValue, String, BaseResponse<List<NodeCarrierResponse>>> {

    @Override
    public NodeCarrierListCacheKey requestToCacheKey(String request) {
        return null;
    }

    @Override
    public String cacheKeyToRequest(NodeCarrierListCacheKey cacheKey) {
        return null;
    }

    @Override
    public NodeCarrierListCacheValue responseToCacheValue(BaseResponse<List<NodeCarrierResponse>> resp) {
        return NodeCarrierListCacheValue.builder()
                .nodeCarrierDetailsList(DATA_MAPPER.convertToNodeCarrierCacheValue(resp.getPayload()))
                .build();
    }

    @Override
    public BaseResponse<List<NodeCarrierResponse>> cacheValueToResponse(NodeCarrierListCacheValue cacheValue) {
        return null;
    }
}
