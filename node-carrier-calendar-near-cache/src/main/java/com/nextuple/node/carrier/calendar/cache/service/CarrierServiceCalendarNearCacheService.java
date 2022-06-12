package com.nextuple.node.carrier.calendar.cache.service;

import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierServiceCalendarCacheKey;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierServiceCalendarCacheValue;

public interface CarrierServiceCalendarNearCacheService
    extends GenericNearCacheService<
    NodeCarrierServiceCalendarCacheKey, NodeCarrierServiceCalendarCacheValue> {}
