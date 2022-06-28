package com.hbc.nodecarrier.cache.service;

import com.hbc.core.cache.service.GenericNearCacheService;
import com.hbc.nodecarrier.cache.domain.NodeCarrierCacheKey;
import com.hbc.nodecarrier.cache.domain.NodeCarrierCacheValue;

public interface NodeCarrierNearCacheService
    extends GenericNearCacheService<NodeCarrierCacheKey, NodeCarrierCacheValue> {}
