package com.nextuple.nodecarrier.spring.cache.service;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierCacheValue;
import com.nextuple.nodecarrier.cache.service.NodeCarrierNearCacheService;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = NodeCarrierSpringNearCacheServiceImpl.NODE_CARRIER_CACHE_NAME)
public class NodeCarrierSpringNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<NodeCarrierCacheKey, NodeCarrierCacheValue>
    implements NodeCarrierNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(NodeCarrierSpringNearCacheServiceImpl.class);
  public static final String NODE_CARRIER_CACHE_NAME = "node_carrier";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.NODE_CARRIER_ENTITY_NAME,
        NodeCarrierCacheKey.class.getName(),
        "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.NODE_CARRIER_ENTITY_NAME;
  }

  @Override
  public NodeCarrierCacheValue get(NodeCarrierCacheKey key) {
    logger.debug("Inside get NodeCarrierCacheValue");
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(NodeCarrierCacheKey key) {
    logger.debug("Inside delete method of nodeCarrierCache");
    super.delete(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", allEntries = true)
  @Override
  public void deleteAll() {
    logger.debug("Inside deleteAll method of nodeCarrierCache");
    super.deleteAll();
  }
}
