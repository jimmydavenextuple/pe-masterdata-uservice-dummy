/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.spring.cache.service;

import static com.nextuple.weightage.configuration.spring.cache.service.WeightageConfigurationSpringNearCacheServiceImpl.WEIGHTAGE_CONFIGURATION;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.weightage.configuration.cache.domain.WeightageConfigurationCacheKey;
import com.nextuple.weightage.configuration.cache.domain.WeightageConfigurationCacheValue;
import com.nextuple.weightage.configuration.cache.service.WeightageConfigurationNearCacheService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = WEIGHTAGE_CONFIGURATION)
public class WeightageConfigurationSpringNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<
        WeightageConfigurationCacheKey, WeightageConfigurationCacheValue>
    implements WeightageConfigurationNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(WeightageConfigurationSpringNearCacheServiceImpl.class);

  public static final String WEIGHTAGE_CONFIGURATION = "weightage_configuration";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.WEIGHTAGE_CONFIGURATION_ENTITY_NAME,
        WeightageConfigurationCacheKey.class.getName(),
        "full");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.WEIGHTAGE_CONFIGURATION_ENTITY_NAME;
  }

  @Override
  public WeightageConfigurationCacheValue get(WeightageConfigurationCacheKey key) {
    logger.debug("Inside get WeightageConfigurationCacheValue");
    return super.get(key);
  }

  @Override
  public String getCacheName() {
    return WEIGHTAGE_CONFIGURATION;
  }
}
