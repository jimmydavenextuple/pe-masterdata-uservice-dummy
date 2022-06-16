package com.nextuple.node.data.cache.service;

import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.node.data.cache.domain.NodeDataCacheKey;
import com.nextuple.node.data.cache.domain.NodeDataCacheValue;

public interface NodeDataNearCacheService
    extends GenericNearCacheService<NodeDataCacheKey, NodeDataCacheValue> {}
