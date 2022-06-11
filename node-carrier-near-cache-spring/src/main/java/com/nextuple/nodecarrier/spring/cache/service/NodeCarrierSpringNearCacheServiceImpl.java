package com.nextuple.nodecarrier.spring.cache.service;

import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierCacheValue;
import com.nextuple.nodecarrier.cache.service.NodeCarrierNearCacheService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = NodeCarrierSpringNearCacheServiceImpl.NODE_CARRIER_CACHE_NAME)
public class NodeCarrierSpringNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<NodeCarrierCacheKey, NodeCarrierCacheValue>
    implements NodeCarrierNearCacheService {

  public static final String NODE_CARRIER_CACHE_NAME = "node_carrier";

  @Override
  public NodeCarrierCacheValue get(NodeCarrierCacheKey key) {
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(NodeCarrierCacheKey key) {
    super.delete(key);
  }
}
