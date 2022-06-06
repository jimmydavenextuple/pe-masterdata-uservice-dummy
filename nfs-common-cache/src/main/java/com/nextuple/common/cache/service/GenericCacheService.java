package com.nextuple.common.cache.service;

import com.nextuple.common.cache.dto.CacheValue;
import com.nextuple.common.cache.dto.key.CacheKey;

import java.util.Set;

public interface GenericCacheService<K extends CacheKey, V extends CacheValue> {

    V get(K key);

    Set<V> getAll(Set<K> keys);

    V put(K key, V value);

    V putWithTtl(K key, V value, int ttlInSeconds);

    void delete(K key);

    V remove(K key);
}
