package com.hbc.common.cache.mapper;

import com.hbc.common.cache.dto.CacheValue;
import com.hbc.common.cache.dto.key.CacheKey;
import com.hbc.common.dto.Entity;
import com.hbc.common.dto.key.EntityKey;

public interface GenericCacheAndEntityMapper<
    CK extends CacheKey, CV extends CacheValue, EK extends EntityKey, ED extends Entity> {

  EK toEntityKey(CK cacheKey);

  ED toEntity(CK cacheKey, CV cacheValue);

  CV toCacheValue(ED entity);

  CK toCacheKey(ED entity);
}
