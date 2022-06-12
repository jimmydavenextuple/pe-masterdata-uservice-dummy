package com.nextuple.node.carrier.calendar.cache.service;

import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheKey;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheValue;

public interface NodeCarrierCalendarNearCacheService
    extends GenericNearCacheService<NodeCarrierCalendarCacheKey, NodeCarrierCalendarCacheValue> {}
