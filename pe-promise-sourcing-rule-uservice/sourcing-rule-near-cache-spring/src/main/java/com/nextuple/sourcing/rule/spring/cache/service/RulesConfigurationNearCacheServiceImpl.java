/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.rule.spring.cache.service;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.sourcing.rule.cache.domain.RulesConfigurationKey;
import com.nextuple.sourcing.rule.cache.domain.RulesConfigurationValue;
import com.nextuple.sourcing.rule.cache.service.RuleConfigurationNearCacheService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@CacheConfig(cacheNames = RulesConfigurationNearCacheServiceImpl.RULES_CONFIG_CACHE_NAME)
public class RulesConfigurationNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<
        RulesConfigurationKey, RulesConfigurationValue>
    implements RuleConfigurationNearCacheService {
  public static final String RULES_CONFIG_CACHE_NAME = "rules_config";
  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.RULES_CONFIGURATION_ENTITY_NAME,
        RulesConfigurationKey.class.getName(),
        "full");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.RULES_CONFIGURATION_ENTITY_NAME;
  }

  @Override
  public RulesConfigurationValue get(RulesConfigurationKey key) {
    log.debug("Inside get RulesConfigurationValue");
    return super.get(key);
  }

  @Override
  public String getCacheName() {
    return RULES_CONFIG_CACHE_NAME;
  }
}
