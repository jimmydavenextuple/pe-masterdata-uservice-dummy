/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.spring.cache.service;

import static com.nextuple.carrier.spring.cache.service.CarrierSpringNearCacheServiceImpl.CARRIER_CACHE_NAME;

import com.nextuple.carrier.cache.domain.CarrierCacheKey;
import com.nextuple.carrier.cache.domain.CarrierCacheValue;
import com.nextuple.carrier.cache.service.CarrierNearCacheService;
import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = CARRIER_CACHE_NAME)
// Added this
@RequiredArgsConstructor
public class CarrierSpringNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<CarrierCacheKey, CarrierCacheValue>
    implements CarrierNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(CarrierSpringNearCacheServiceImpl.class);
  public static final String CARRIER_CACHE_NAME = "carrier";

  private final NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.CARRIER_ENTITY_NAME, CarrierCacheKey.class.getName(), "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.CARRIER_ENTITY_NAME;
  }

  @Override
  public CarrierCacheValue get(CarrierCacheKey key) {
    logger.debug("Inside get CarrierCacheValue");
    return super.get(key);
  }

  @Override
  public String getCacheName() {
    return CARRIER_CACHE_NAME;
  }
}
