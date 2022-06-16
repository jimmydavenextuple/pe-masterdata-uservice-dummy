package com.nextuple.nodecarrier.cache.service;

import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierCacheValue;

public interface NodeCarrierNearCacheService
    extends GenericNearCacheService<NodeCarrierCacheKey, NodeCarrierCacheValue> {}
