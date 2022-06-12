package com.nextuple.node.calendar.cache.service;

import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheValue;

public interface NodeCalendarNearCacheService
    extends GenericNearCacheService<NodeCalendarCacheKey, NodeCalendarCacheValue> {}
