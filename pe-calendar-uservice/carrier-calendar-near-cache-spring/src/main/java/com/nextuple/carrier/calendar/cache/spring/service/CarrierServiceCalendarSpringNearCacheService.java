/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.calendar.cache.spring.service;

import com.nextuple.carrier.calendar.cache.domain.CarrierServiceCalendarCacheKey;
import com.nextuple.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.nextuple.carrier.calendar.cache.service.CarrierServiceCalendarNearCacheService;
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
@CacheConfig(cacheNames = CarrierServiceCalendarSpringNearCacheService.CARRIER_CALENDAR_CACHE_NAME)
// Added this
@RequiredArgsConstructor
public class CarrierServiceCalendarSpringNearCacheService
    extends AbstractGenericSpringLocalCacheServiceImpl<
        CarrierServiceCalendarCacheKey, CarrierServiceCalendarCacheValue>
    implements CarrierServiceCalendarNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(CarrierServiceCalendarSpringNearCacheService.class);

  public static final String CARRIER_CALENDAR_CACHE_NAME = "carrier_calendar";

  private final NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.CARRIER_CALENDAR_ENTITY_NAME,
        CarrierServiceCalendarCacheKey.class.getName(),
        "full");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.CARRIER_CALENDAR_ENTITY_NAME;
  }

  @Override
  public CarrierServiceCalendarCacheValue get(CarrierServiceCalendarCacheKey key) {
    logger.debug("Inside get CarrierServiceCalendarCacheValue");
    return super.get(key);
  }

  @Override
  public String getCacheName() {
    return CarrierServiceCalendarSpringNearCacheService.CARRIER_CALENDAR_CACHE_NAME;
  }
}
