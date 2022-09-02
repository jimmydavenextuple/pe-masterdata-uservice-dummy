package com.hbc.nodecarrier.cache.service;

import com.hbc.core.cache.service.GenericNearCacheService;
import com.hbc.nodecarrier.cache.domain.NodeCarrierListCacheKey;
import com.hbc.nodecarrier.cache.domain.NodeCarrierListCacheValue;

public interface NodeCarrierListNearCacheService
    extends GenericNearCacheService<NodeCarrierListCacheKey, NodeCarrierListCacheValue> {}
