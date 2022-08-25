package com.hbc.transit.spring.cache.mapper;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.transit.cache.domain.TransitCacheKey;
import com.hbc.transit.cache.domain.TransitCacheValue;
import com.hbc.transit.domain.outbound.TransitResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransitDataMapper implements GenericMapper<
        TransitCacheKey,
        TransitCacheValue,
        String,
        BaseResponse<List<TransitResponse>>> {
    @Override
    public TransitCacheKey requestToCacheKey(String request) {
        return null;
    }

    @Override
    public String cacheKeyToRequest(TransitCacheKey cacheKey) {
        return null;
    }

    @Override
    public TransitCacheValue responseToCacheValue(BaseResponse<List<TransitResponse>> resp) {
        return TransitCacheValue.builder().transitResponseList(resp.getPayload()).build();
    }

    @Override
    public BaseResponse<List<TransitResponse>> cacheValueToResponse(TransitCacheValue cacheValue) {
        return null;
    }
}
