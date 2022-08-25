package com.hbc.transit.spring.cache.service;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.hbc.transit.cache.domain.TransitCacheKey;
import com.hbc.transit.cache.domain.TransitCacheValue;
import com.hbc.transit.domain.feign.TransitFeign;
import com.hbc.transit.domain.outbound.TransitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransitDataFeignClientServiceImpl extends AbstractGenericFeignClientServiceImpl<
        TransitCacheKey, TransitCacheValue, String, BaseResponse<List<TransitResponse>>> {

    @Autowired
    TransitFeign transitFeign;

    @Autowired
    GenericMapper<TransitCacheKey, TransitCacheValue, String, BaseResponse<List<TransitResponse>>>
            transitMapper;

    @Override
    public TransitCacheValue get(TransitCacheKey key) {
        try {
            return transitMapper.responseToCacheValue(
                    transitFeign.getTransitDetailsListForDestinationGeoZone(
                            key.getOrgId(),
                            key.getDestinationGeozone()));
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
