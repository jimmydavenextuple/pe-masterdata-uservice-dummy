/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.rule.spring.cache.service;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.sourcing.rule.cache.domain.SourcingConstraintCacheKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingConstraintCacheValue;
import com.nextuple.sourcing.rule.cache.service.SourcingConstraintNearCacheService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = SourcingConstraintNearCacheServiceImpl.SOURCING_CONSTRAINT_CACHE_NAME)
public class SourcingConstraintNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<
        SourcingConstraintCacheKey, SourcingConstraintCacheValue>
    implements SourcingConstraintNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(SourcingConstraintNearCacheServiceImpl.class);

  public static final String SOURCING_CONSTRAINT_CACHE_NAME = "sourcing_constraints";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.SOURCING_CONSTRAINT_ENTITY_NAME,
        SourcingConstraintCacheKey.class.getName(),
        "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.SOURCING_CONSTRAINT_ENTITY_NAME;
  }

  @Override
  public SourcingConstraintCacheValue get(SourcingConstraintCacheKey key) {
    logger.debug("Inside get SourcingConstraintCacheValue");
    return super.get(key);
  }

  @Override
  public String getCacheName() {
    return SourcingConstraintNearCacheServiceImpl.SOURCING_CONSTRAINT_CACHE_NAME;
  }
}
