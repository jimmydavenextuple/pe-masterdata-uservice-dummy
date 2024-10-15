/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.cache.spring.service;

import static com.nextuple.postal.code.timezone.cache.spring.service.PostalCodeTimezoneNearCacheServiceImpl.POSTAL_CODE_TIMEZONE_CACHE_NAME;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheValue;
import com.nextuple.postal.code.timezone.cache.service.PostalCodeTimezoneNearCacheService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = POSTAL_CODE_TIMEZONE_CACHE_NAME)
@AllArgsConstructor
public class PostalCodeTimezoneNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<
        PostalCodeTimezoneCacheKey, PostalCodeTimezoneCacheValue>
    implements PostalCodeTimezoneNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(PostalCodeTimezoneNearCacheServiceImpl.class);

  public static final String POSTAL_CODE_TIMEZONE_CACHE_NAME = "postal_code_timezone";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.POSTAL_CODE_TIME_ZONE_ENTITY_NAME,
        PostalCodeTimezoneCacheKey.class.getName(),
        "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.POSTAL_CODE_TIME_ZONE_ENTITY_NAME;
  }

  @Override
  public PostalCodeTimezoneCacheValue get(PostalCodeTimezoneCacheKey key) {
    logger.debug("Inside get ZipCodeTimezoneCacheValue");
    return super.get(key);
  }

  @Override
  public String getCacheName() {
    return POSTAL_CODE_TIMEZONE_CACHE_NAME;
  }
}
