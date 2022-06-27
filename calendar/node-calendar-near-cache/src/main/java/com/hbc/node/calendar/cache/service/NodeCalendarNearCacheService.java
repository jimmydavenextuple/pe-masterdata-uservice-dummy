package com.hbc.node.calendar.cache.service;

import com.hbc.core.cache.service.GenericNearCacheService;
import com.hbc.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.hbc.node.calendar.cache.domain.NodeCalendarCacheValue;

public interface NodeCalendarNearCacheService
    extends GenericNearCacheService<NodeCalendarCacheKey, NodeCalendarCacheValue> {}
