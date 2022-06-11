package com.nextuple.common;

import com.nextuple.common.cache.dto.CacheValue;
import com.nextuple.common.cache.dto.key.CacheKey;
import com.nextuple.common.cache.service.GenericCacheService;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractCachedCommonHandler<K extends CacheKey, E extends CacheValue> {

  @Autowired protected GenericCacheService<K, E> cacheService;

  @Autowired MeterRegistry registry;

  protected abstract Logger log();

  protected E create(K key, E value) {
    log().trace("Inserting a new record for {} with value {} ", key, value);
    return cacheService.put(key, value);
  }

  protected E update(K key, E value) {
    log().trace("Updating a new record for {} with value {} ", key, value);
    return cacheService.put(key, value);
  }

  protected E get(K key) {
    E value = cacheService.get(key);
    log().trace("Fetching details for {}", key);
    return value;
  }

  protected void delete(K key) {
    log().trace("Deleting a the record with key {} ", key);
    cacheService.delete(key);
  }
}
