package com.nextuple.nodde.spring.cache.service;

import static com.nextuple.nodde.spring.cache.service.NodeSpringNearCacheServiceImpl.NODE_CACHE_NAME;

import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.node.cache.domain.NodeCacheKey;
import com.nextuple.node.cache.domain.NodeCacheValue;
import com.nextuple.node.cache.service.NodeNearCacheService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = NODE_CACHE_NAME)
public class NodeSpringNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<NodeCacheKey, NodeCacheValue>
    implements NodeNearCacheService {

  public static final String NODE_CACHE_NAME = "node";

  @Override
  public NodeCacheValue get(NodeCacheKey key) {
    return super.get(key);
  }
}
