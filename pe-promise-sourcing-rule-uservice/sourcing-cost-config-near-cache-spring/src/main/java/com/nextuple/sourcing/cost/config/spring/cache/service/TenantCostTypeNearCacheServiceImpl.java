/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.service;

import static com.nextuple.sourcing.cost.config.spring.cache.service.TenantCostTypeNearCacheServiceImpl.TENANT_COST_TYPE_CACHE_NAME;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.sourcing.cost.config.cache.domain.TenantCostTypeCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.TenantCostTypeCacheValue;
import com.nextuple.sourcing.cost.config.cache.service.TenantCostTypeNearCacheService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = TENANT_COST_TYPE_CACHE_NAME)
public class TenantCostTypeNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<
        TenantCostTypeCacheKey, TenantCostTypeCacheValue>
    implements TenantCostTypeNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(TenantCostTypeNearCacheServiceImpl.class);

  protected static final String TENANT_COST_TYPE_CACHE_NAME = "tenant_cost_type";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {

    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.TENANT_COST_TYPE_ENTITY_NAME,
        TenantCostTypeCacheKey.class.getName(),
        "full");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.TENANT_COST_TYPE_ENTITY_NAME;
  }

  @Override
  public TenantCostTypeCacheValue get(TenantCostTypeCacheKey key) {
    logger.debug("Inside get TenantCostTypeCacheValue with cacheKey: {}", key);
    return super.get(key);
  }

  @Override
  public String getCacheName() {
    return TENANT_COST_TYPE_CACHE_NAME;
  }
}
