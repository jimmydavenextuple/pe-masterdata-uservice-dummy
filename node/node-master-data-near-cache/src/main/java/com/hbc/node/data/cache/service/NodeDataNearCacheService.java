package com.hbc.node.data.cache.service;

import com.hbc.core.cache.service.GenericNearCacheService;
import com.hbc.node.data.cache.domain.NodeDataCacheKey;
import com.hbc.node.data.cache.domain.NodeDataCacheValue;

public interface NodeDataNearCacheService
    extends GenericNearCacheService<NodeDataCacheKey, NodeDataCacheValue> {}
