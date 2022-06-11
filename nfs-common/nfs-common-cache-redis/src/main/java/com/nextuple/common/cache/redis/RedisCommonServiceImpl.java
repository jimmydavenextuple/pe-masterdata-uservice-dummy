package com.nextuple.common.cache.redis;

import com.nextuple.common.cache.dto.CacheValue;
import com.nextuple.common.cache.dto.key.CacheKey;
import com.nextuple.common.cache.redis.mapper.GenericCacheRedisCacheMapper;
import com.nextuple.common.cache.service.GenericBaseCacheService;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class RedisCommonServiceImpl<CK extends CacheKey, CV extends CacheValue, RK, RV>
    implements GenericBaseCacheService<CK, CV> {

  @Autowired RedissonClient redissonClient;

  String cacheName;

  protected GenericCacheRedisCacheMapper<CK, CV, RK, RV> cacheMapper;

  protected abstract Logger log();

  public CV get(CK key) {
    return (CV) redissonClient.getMap(cacheName).get(cacheMapper.toRedisCacheKey(key));
  }
}
