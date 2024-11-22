/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.rule.spring.cache.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.sourcing.rule.cache.domain.GroupDefinitionRuleCacheKey;
import com.nextuple.sourcing.rule.cache.domain.GroupDefinitionRuleCacheValue;
import com.nextuple.sourcing.rule.cache.service.GroupDefinitionRuleNearCacheService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = GroupDefinitionRuleNearCacheServiceImpl.GROUP_DEFINITION_RULE_CACHE_NAME)
public class GroupDefinitionRuleNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<
        GroupDefinitionRuleCacheKey, GroupDefinitionRuleCacheValue>
    implements GroupDefinitionRuleNearCacheService {

  public static final String GROUP_DEFINITION_RULE_CACHE_NAME = "group_definition_rule";
  private static final Logger logger =
      LoggerFactory.getLogger(GroupDefinitionRuleNearCacheServiceImpl.class);
  @Autowired NearCacheRegistry nearCacheRegistry;

  @Override
  public String getCacheName() {
    return GROUP_DEFINITION_RULE_CACHE_NAME;
  }

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.GROUP_DEFINITION_RULE_ENTITY_NAME,
        GroupDefinitionRuleCacheKey.class.getName(),
        "full");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.GROUP_DEFINITION_RULE_ENTITY_NAME;
  }

  @Override
  public GroupDefinitionRuleCacheValue get(GroupDefinitionRuleCacheKey key) {
    logger.debug("Inside get GroupDefinitionRuleCacheValue for key:", key);
    return super.get(key);
  }
}
