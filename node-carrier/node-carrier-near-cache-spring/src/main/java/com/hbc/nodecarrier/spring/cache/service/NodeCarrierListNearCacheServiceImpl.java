package com.hbc.nodecarrier.spring.cache.service;

import com.hbc.core.constants.NearCacheConstants;
import com.hbc.core.registry.NearCacheRegistry;
import com.hbc.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.hbc.nodecarrier.cache.domain.NodeCarrierListCacheKey;
import com.hbc.nodecarrier.cache.domain.NodeCarrierListCacheValue;
import com.hbc.nodecarrier.cache.service.NodeCarrierListNearCacheService;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = NodeCarrierListNearCacheServiceImpl.NODE_CARRIER_LIST_CACHE_NAME)
public class NodeCarrierListNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<
        NodeCarrierListCacheKey, NodeCarrierListCacheValue>
    implements NodeCarrierListNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(NodeCarrierListNearCacheServiceImpl.class);
  public static final String NODE_CARRIER_LIST_CACHE_NAME = "node_carrier_list";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.NODE_CARRIER_LIST_ENTITY_NAME,
        NodeCarrierListCacheKey.class.getName(),
        "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.NODE_CARRIER_LIST_ENTITY_NAME;
  }

  @Override
  @Cacheable(cacheManager = "caffeineCacheManager")
  public NodeCarrierListCacheValue get(NodeCarrierListCacheKey key) {
    logger.debug("Inside get NodeCarrierCacheValue");
    return super.get(key);
  }

  @CacheEvict(cacheManager = "caffeineCacheManager", key = "#key")
  @Override
  public void delete(NodeCarrierListCacheKey key) {
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
