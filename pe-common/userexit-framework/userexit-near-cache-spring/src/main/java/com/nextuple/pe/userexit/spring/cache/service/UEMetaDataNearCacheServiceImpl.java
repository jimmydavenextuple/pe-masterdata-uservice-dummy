/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.userexit.spring.cache.service;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.pe.userexit.cache.domain.UEMetaDataCacheKey;
import com.nextuple.pe.userexit.cache.domain.UEMetaDataCacheValue;
import com.nextuple.pe.userexit.cache.service.UEMetaDataNearCacheService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = UEMetaDataNearCacheServiceImpl.UE_META_DATA_CACHE_NAME)
public class UEMetaDataNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<UEMetaDataCacheKey, UEMetaDataCacheValue>
    implements UEMetaDataNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(UEMetaDataNearCacheServiceImpl.class);

  public static final String UE_META_DATA_CACHE_NAME = "ue_metadata";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.UE_META_DATA_ENTITY_NAME, UEMetaDataCacheKey.class.getName(), "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.UE_META_DATA_ENTITY_NAME;
  }

  @Override
  public UEMetaDataCacheValue get(UEMetaDataCacheKey key) {
    logger.debug("Inside get UEMetaDataCacheValue");
    return super.get(key);
  }

  @Override
  public String getCacheName() {
    return UEMetaDataNearCacheServiceImpl.UE_META_DATA_CACHE_NAME;
  }
}
