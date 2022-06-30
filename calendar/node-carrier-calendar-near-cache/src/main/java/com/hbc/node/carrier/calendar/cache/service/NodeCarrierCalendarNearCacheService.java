package com.hbc.node.carrier.calendar.cache.service;

import com.hbc.core.cache.service.GenericNearCacheService;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheKey;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheValue;

public interface NodeCarrierCalendarNearCacheService
    extends GenericNearCacheService<NodeCarrierCalendarCacheKey, NodeCarrierCalendarCacheValue> {}
