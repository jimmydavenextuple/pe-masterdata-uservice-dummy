/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.service;

import static com.nextuple.sourcing.cost.config.spring.cache.service.SelectorAndCostItineraryNearCacheServiceImpl.SELECTOR_AND_COST_ITINERARY_CACHE_NAME;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.sourcing.cost.config.cache.domain.SelectorAndCostItineraryCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.SelectorAndCostItineraryCacheValue;
import com.nextuple.sourcing.cost.config.cache.service.SelectorAndCostItineraryNearCacheService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = SELECTOR_AND_COST_ITINERARY_CACHE_NAME)
public class SelectorAndCostItineraryNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<
        SelectorAndCostItineraryCacheKey, SelectorAndCostItineraryCacheValue>
    implements SelectorAndCostItineraryNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(SelectorAndCostItineraryNearCacheServiceImpl.class);

  protected static final String SELECTOR_AND_COST_ITINERARY_CACHE_NAME =
      "selector_and_cost_itinerary_mapping";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {

    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.SELECTOR_AND_COST_ITINERARY_ENTITY_NAME,
        SelectorAndCostItineraryCacheKey.class.getName(),
        "full");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.SELECTOR_AND_COST_ITINERARY_ENTITY_NAME;
  }

  @Override
  public SelectorAndCostItineraryCacheValue get(SelectorAndCostItineraryCacheKey key) {
    logger.debug("Inside get SelectorAndCostItineraryCacheValue with cacheKey: {}", key);
    return super.get(key);
  }

  @Override
  public String getCacheName() {
    return SELECTOR_AND_COST_ITINERARY_CACHE_NAME;
  }
}
