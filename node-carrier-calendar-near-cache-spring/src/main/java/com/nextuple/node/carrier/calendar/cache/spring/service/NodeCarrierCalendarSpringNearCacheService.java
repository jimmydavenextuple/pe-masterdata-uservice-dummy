/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.calendar.cache.spring.service;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheKey;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheValue;
import com.nextuple.node.carrier.calendar.cache.service.NodeCarrierCalendarNearCacheService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(
    cacheNames = NodeCarrierCalendarSpringNearCacheService.NODE_CARRIER_CALENDAR_CACHE_NAME)
// Added this
@AllArgsConstructor
public class NodeCarrierCalendarSpringNearCacheService
    extends AbstractGenericSpringLocalCacheServiceImpl<
        NodeCarrierCalendarCacheKey, NodeCarrierCalendarCacheValue>
    implements NodeCarrierCalendarNearCacheService {

  private static final Logger logger =
      LoggerFactory.getLogger(NodeCarrierCalendarSpringNearCacheService.class);
  public static final String NODE_CARRIER_CALENDAR_CACHE_NAME = "node_carrier_calendar";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.NODE_CARRIER_CALENDAR_ENTITY_NAME,
        NodeCarrierCalendarCacheKey.class.getName(),
        "full");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.NODE_CARRIER_CALENDAR_ENTITY_NAME;
  }

  @Override
  public NodeCarrierCalendarCacheValue get(NodeCarrierCalendarCacheKey key) {
    logger.debug("Inside get NodeCarrierCalendarCacheValue");
    return super.get(key);
  }

  @Override
  public String getCacheName() {
    return NodeCarrierCalendarSpringNearCacheService.NODE_CARRIER_CALENDAR_CACHE_NAME;
  }
}
