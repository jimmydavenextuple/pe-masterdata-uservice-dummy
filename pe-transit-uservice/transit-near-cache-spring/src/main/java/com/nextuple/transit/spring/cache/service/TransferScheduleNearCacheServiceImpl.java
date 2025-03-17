/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.service;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.transit.cache.domain.TransferScheduleCacheKey;
import com.nextuple.transit.cache.domain.TransferScheduleCacheValue;
import com.nextuple.transit.cache.service.TransferScheduleNearCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = TransferScheduleNearCacheServiceImpl.TRANSFER_SCHEDULE)
@RequiredArgsConstructor
public class TransferScheduleNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<
        TransferScheduleCacheKey, TransferScheduleCacheValue>
    implements TransferScheduleNearCacheService {
  private static final Logger logger =
      LoggerFactory.getLogger(TransferScheduleNearCacheServiceImpl.class);
  public static final String TRANSFER_SCHEDULE = "transfer_schedules";
  private final NearCacheRegistry nearCacheRegistry;

  @Override
  public String getCacheName() {
    return TRANSFER_SCHEDULE;
  }

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.TRANSFER_SCHEDULE_ENTITY_NAME,
        TransferScheduleCacheKey.class.getName(),
        "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.TRANSFER_SCHEDULE_ENTITY_NAME;
  }

  @Override
  public TransferScheduleCacheValue get(TransferScheduleCacheKey key) {
    logger.debug("Inside get TransferScheduleCacheValue with cache key : {}", key);
    return super.get(key);
  }
}
