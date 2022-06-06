package com.nextuple.common.cache.mapper;

import com.nextuple.common.cache.dto.CacheValue;
import com.nextuple.common.cache.dto.key.CacheKey;
import com.nextuple.common.dto.Entity;
import com.nextuple.common.dto.key.EntityKey;

public interface GenericCacheAndEntityMapper<CK extends CacheKey, CV extends CacheValue, EK extends EntityKey, ED extends Entity> {

    EK toEntityKey(CK cacheKey);

    ED toEntity(CK cacheKey, CV cacheValue);

    CV toCacheValue(ED entity);

    CK toCacheKey(ED entity);

}
