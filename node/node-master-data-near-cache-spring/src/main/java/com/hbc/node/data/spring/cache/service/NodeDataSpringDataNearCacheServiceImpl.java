package com.hbc.node.data.spring.cache.service;

import static com.hbc.node.data.spring.cache.service.NodeDataSpringDataNearCacheServiceImpl.NODE_CACHE_NAME;

import com.hbc.core.constants.NearCacheConstants;
import com.hbc.core.registry.NearCacheRegistry;
import com.hbc.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.hbc.node.data.cache.domain.NodeDataCacheKey;
import com.hbc.node.data.cache.domain.NodeDataCacheValue;
import com.hbc.node.data.cache.service.NodeDataNearCacheService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = NODE_CACHE_NAME)
public class NodeDataSpringDataNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<NodeDataCacheKey, NodeDataCacheValue>
    implements NodeDataNearCacheService {

  public static final String NODE_CACHE_NAME = "node";

  @Autowired NearCacheRegistry registry;

  @PostConstruct
  @Override
  public void selfRegister() {
    registry.registerNearCacheEntity(
        NearCacheConstants.NODE_ENTITY_NAME, NodeDataCacheKey.class.getName(), "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.NODE_ENTITY_NAME;
  }

  @Override
  public NodeDataCacheValue get(NodeDataCacheKey key) {
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(NodeDataCacheKey key) {
    super.delete(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", allEntries = true)
  @Override
  public void deleteAll() {
    super.deleteAll();
  }
}
