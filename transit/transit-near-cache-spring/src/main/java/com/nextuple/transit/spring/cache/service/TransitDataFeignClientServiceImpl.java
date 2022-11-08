package com.nextuple.transit.spring.cache.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.transit.cache.domain.TransitCacheKey;
import com.nextuple.transit.cache.domain.TransitCacheValue;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.nextuple.transit.spring.cache.feign.TransitDataFeignImpl;
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
