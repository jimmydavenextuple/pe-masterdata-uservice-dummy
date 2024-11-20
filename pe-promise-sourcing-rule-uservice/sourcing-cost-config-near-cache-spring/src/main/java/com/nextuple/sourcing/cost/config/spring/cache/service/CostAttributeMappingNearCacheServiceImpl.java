/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.service;

import static com.nextuple.sourcing.cost.config.spring.cache.service.CostAttributeMappingNearCacheServiceImpl.COST_ATTRIBUTE_MAPPING_CACHE_NAME;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.sourcing.cost.config.cache.domain.CostAttributeMappingCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostAttributeMappingCacheValue;
import com.nextuple.sourcing.cost.config.cache.service.CostAttributeMappingNearCacheService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = COST_ATTRIBUTE_MAPPING_CACHE_NAME)
public class CostAttributeMappingNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<
        CostAttributeMappingCacheKey, CostAttributeMappingCacheValue>
    implements CostAttributeMappingNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(CostAttributeMappingNearCacheServiceImpl.class);

  public static final String COST_ATTRIBUTE_MAPPING_CACHE_NAME = "cost_attribute_mapping";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.COST_ATTRIBUTE_MAPPING_ENTITY_NAME,
        CostAttributeMappingCacheKey.class.getName(),
        "full");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.COST_ATTRIBUTE_MAPPING_ENTITY_NAME;
  }

  @Override
  public CostAttributeMappingCacheValue get(CostAttributeMappingCacheKey key) {
    logger.debug("Inside get CostAttributeMappingCacheValue with cacheKey: {}", key);
    return super.get(key);
  }

  @Override
  public String getCacheName() {
    return COST_ATTRIBUTE_MAPPING_CACHE_NAME;
  }
}
