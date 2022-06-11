package com.nextuple.nodde.spring.cache.service;

import static com.nextuple.nodde.spring.cache.service.NodeSpringNearCacheServiceImpl.NODE_CACHE_NAME;

import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.node.cache.domain.NodeCacheKey;
import com.nextuple.node.cache.domain.NodeCacheValue;
import com.nextuple.node.cache.service.NodeNearCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import com.nextuple.core.NearCacheConstants;
import com.nextuple.core.NearCacheRegistry;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@CacheConfig(cacheNames = NODE_CACHE_NAME)
public class NodeSpringNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<NodeCacheKey, NodeCacheValue>
    implements NodeNearCacheService {

  public static final String NODE_CACHE_NAME = "node";

  @Autowired
  NearCacheRegistry registry;

  @PostConstruct
  @Override
  public void selfRegister() {
    registry.registerNearCacheEntity(
            NearCacheConstants.NODE_ENTITY_NAME, NodeCacheKey.class.getName());
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.NODE_ENTITY_NAME;
  }

  @Override
  public NodeCacheValue get(NodeCacheKey key) {
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(NodeCacheKey key) {
    super.delete(key);
  }
}
