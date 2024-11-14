/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.nodecarrier.spring.cache.service;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.nodecarrier.cache.domain.NodeCarriersCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarriersCacheValue;
import com.nextuple.nodecarrier.cache.service.NodeCarriersNearCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = NodeCarriersNearCacheServiceImpl.NODE_CARRIERS_CACHE_NAME)
@RequiredArgsConstructor
public class NodeCarriersNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<NodeCarriersCacheKey, NodeCarriersCacheValue>
    implements NodeCarriersNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(NodeCarriersNearCacheServiceImpl.class);
  public static final String NODE_CARRIERS_CACHE_NAME = "node_carriers";

  private final NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.NODE_CARRIERS_ENTITY_NAME,
        NodeCarriersCacheKey.class.getName(),
        "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.NODE_CARRIERS_ENTITY_NAME;
  }

  @Override
  public NodeCarriersCacheValue get(NodeCarriersCacheKey key) {
    logger.debug("Inside get NodeCarriersCacheValue");
    return super.get(key);
  }

  @Override
  public boolean allowNull() {
    return true;
  }

  @Override
  public String getCacheName() {
    return NodeCarriersNearCacheServiceImpl.NODE_CARRIERS_CACHE_NAME;
  }
}
