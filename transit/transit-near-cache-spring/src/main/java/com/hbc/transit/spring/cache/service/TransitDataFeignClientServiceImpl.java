package com.hbc.transit.spring.cache.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.hbc.transit.cache.domain.TransitCacheKey;
import com.hbc.transit.cache.domain.TransitCacheValue;
import com.hbc.transit.domain.outbound.TransitResponse;
import com.hbc.transit.spring.cache.feign.TransitDataFeignImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransitDataFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        TransitCacheKey, TransitCacheValue, String, BaseResponse<List<TransitResponse>>> {

  @Autowired TransitDataFeignImpl transitFeign;

  private static Logger logger = LoggerFactory.getLogger(TransitDataFeignClientServiceImpl.class);

  @Autowired
  GenericMapper<TransitCacheKey, TransitCacheValue, String, BaseResponse<List<TransitResponse>>>
      transitMapper;

  @Override
  public TransitCacheValue get(TransitCacheKey key) {
    try {
      return transitMapper.responseToCacheValue(
          transitFeign.getTransitDetailsListForDestinationGeoZone(
              key.getOrgId(), key.getDestinationGeozone()));
    } catch (RuntimeException ex) {
      logger.info("exception in transit near cache service: {}", ex);
      return null;
    }
  }
}
