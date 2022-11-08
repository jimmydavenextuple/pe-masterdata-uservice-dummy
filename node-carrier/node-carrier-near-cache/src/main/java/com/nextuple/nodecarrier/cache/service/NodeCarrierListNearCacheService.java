package com.nextuple.nodecarrier.cache.service;

import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierListCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierListCacheValue;

public interface NodeCarrierListNearCacheService
    extends GenericNearCacheService<NodeCarrierListCacheKey, NodeCarrierListCacheValue> {}
