/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.service;

import static com.nextuple.transit.spring.cache.service.TransitDataNearCacheServiceImpl.TRANSIT_CACHE_NAME;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.transit.cache.domain.TransitCacheKey;
import com.nextuple.transit.cache.domain.TransitCacheValue;
import com.nextuple.transit.cache.service.TransitDataNearCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = TRANSIT_CACHE_NAME)
// Added this
@RequiredArgsConstructor
public class TransitDataNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<TransitCacheKey, TransitCacheValue>
    implements TransitDataNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(TransitDataNearCacheServiceImpl.class);

  public static final String TRANSIT_CACHE_NAME = "transit";

  private final NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.TRANSIT_ENTITY_NAME, TransitCacheKey.class.getName(), "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.TRANSIT_ENTITY_NAME;
  }

  @Override
  public TransitCacheValue get(TransitCacheKey key) {
    logger.debug("Inside get TransitCacheValue");
    return super.get(key);
  }

  @Override
  public String getCacheName() {
    return TRANSIT_CACHE_NAME;
  }
}
