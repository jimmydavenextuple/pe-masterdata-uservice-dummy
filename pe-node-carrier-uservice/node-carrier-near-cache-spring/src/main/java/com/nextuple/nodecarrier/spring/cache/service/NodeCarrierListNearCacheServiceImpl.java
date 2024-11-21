/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.nodecarrier.spring.cache.service;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierListCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierListCacheValue;
import com.nextuple.nodecarrier.cache.service.NodeCarrierListNearCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = NodeCarrierListNearCacheServiceImpl.NODE_CARRIER_LIST_CACHE_NAME)
@RequiredArgsConstructor
public class NodeCarrierListNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<
        NodeCarrierListCacheKey, NodeCarrierListCacheValue>
    implements NodeCarrierListNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(NodeCarrierListNearCacheServiceImpl.class);
  public static final String NODE_CARRIER_LIST_CACHE_NAME = "node_carrier_list";

  private final NearCacheRegistry nearCacheRegistry;

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
  public NodeCarrierListCacheValue get(NodeCarrierListCacheKey key) {
    logger.debug("Inside get NodeCarrierCacheValue");
    return super.get(key);
  }

  @Override
  public boolean allowNull() {
    return true;
  }

  @Override
  public String getCacheName() {
    return NodeCarrierListNearCacheServiceImpl.NODE_CARRIER_LIST_CACHE_NAME;
  }
}
