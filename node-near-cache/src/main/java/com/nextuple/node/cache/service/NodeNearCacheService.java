package com.nextuple.node.cache.service;

import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.node.cache.domain.NodeCacheKey;
import com.nextuple.node.cache.domain.NodeCacheValue;

public interface NodeNearCacheService
    extends GenericNearCacheService<NodeCacheKey, NodeCacheValue> {}
