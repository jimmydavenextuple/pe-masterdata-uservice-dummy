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
import com.nextuple.nodecarrier.cache.domain.NodeServiceOptionBufferCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeServiceOptionBufferCacheValue;
import com.nextuple.nodecarrier.cache.service.NodeServiceOptionBufferNearCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(
    cacheNames = NodeServiceOptionBufferNearCacheServiceImpl.NODE_SERVICE_OPTION_BUFFER_CACHE_NAME)
// Added this
@RequiredArgsConstructor
public class NodeServiceOptionBufferNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<
        NodeServiceOptionBufferCacheKey, NodeServiceOptionBufferCacheValue>
    implements NodeServiceOptionBufferNearCacheService {
  private static final Logger logger =
      LoggerFactory.getLogger(NodeServiceOptionBufferNearCacheServiceImpl.class);
  public static final String NODE_SERVICE_OPTION_BUFFER_CACHE_NAME = "node_service_option_buffers";

  private final NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.NODE_SERVICE_OPTION_BUFFER_ENTITY_NAME,
        NodeServiceOptionBufferCacheKey.class.getName(),
        "full");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.NODE_SERVICE_OPTION_BUFFER_ENTITY_NAME;
  }

  @Override
  public NodeServiceOptionBufferCacheValue get(NodeServiceOptionBufferCacheKey key) {
    logger.debug("Inside get NodeServiceOptionBufferCacheValue");
    return super.get(key);
  }

  @Override
  public String getCacheName() {
    return NodeServiceOptionBufferNearCacheServiceImpl.NODE_SERVICE_OPTION_BUFFER_CACHE_NAME;
  }
}
