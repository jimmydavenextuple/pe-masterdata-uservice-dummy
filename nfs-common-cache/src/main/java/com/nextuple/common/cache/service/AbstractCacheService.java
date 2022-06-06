package com.nextuple.common.cache.service;

import com.nextuple.common.cache.dto.CacheValue;
import com.nextuple.common.cache.dto.key.CacheKey;
import com.nextuple.common.cache.mapper.GenericCacheAndEntityMapper;
import com.nextuple.common.dto.Entity;
import com.nextuple.common.dto.key.EntityKey;
import com.nextuple.common.dto.service.GenericPersistenceService;
import com.nextuple.common.util.ObjectUtil;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.nextuple.common.cache.constants.CacheConstants.*;

@Component
public abstract class AbstractCacheService<CK extends CacheKey, CV extends CacheValue, EK extends EntityKey, ED extends Entity>
        implements GenericCacheService<CK, CV> {

    @Autowired
    GenericBaseCacheService<CK, CV> baseCacheService;

    @Autowired
    GenericPersistenceService<EK, ED> persistenceService;

    @Autowired
    GenericCacheAndEntityMapper<CK, CV, EK, ED> cacheAndEntityMapper;

    @Autowired
    MeterRegistry meterRegistry;

    protected abstract Logger log();

    @Override
    @Timed(value = "cache", extraTags = {OPERATION_TAG_NAME, CACHE_OPERATION_READ})
    public CV get(CK key) {
        Timer coreCacheReadTimer = meterRegistry.timer("cache.core", OPERATION_TAG_NAME, CACHE_OPERATION_READ);
        long start = System.currentTimeMillis();
        CV cv = baseCacheService.get(key);
        coreCacheReadTimer.record(System.currentTimeMillis()-start, TimeUnit.MILLISECONDS);
        if (log().isInfoEnabled()) {
            log().info(String.format("Time taken to fetch from cache is %d for key %s ", System.currentTimeMillis()-start, key));
        }
        if (ObjectUtil.isNull(cv)) {
            long dbTimeStart = System.currentTimeMillis();
            EK entityKey = cacheAndEntityMapper.toEntityKey(key);
            Timer dbTimer = meterRegistry.timer("cache.persistence", OPERATION_TAG_NAME,
                    DB_OPERATION_READ);
            cv = cacheAndEntityMapper.toCacheValue(persistenceService.get(entityKey));
            if (log().isInfoEnabled()) {
                log().info(String.format("Time taken to fetch from db is %d for key %s ", System.currentTimeMillis()-dbTimeStart,
                        entityKey));
            }
            dbTimer.record(System.currentTimeMillis()-dbTimeStart, TimeUnit.MILLISECONDS);
            if (!ObjectUtil.isNull(cv)) {
                put(key, cv, 0, true);
            }
        }
        Timer cacheReadTimer = meterRegistry.timer("cache", OPERATION_TAG_NAME, CACHE_OPERATION_READ);
        cacheReadTimer.record(System.currentTimeMillis()-start, TimeUnit.MILLISECONDS);
        return cv;
    }

    @Override
    public CV put(CK key, CV value) {
        return put(key, value, 0, true);
    }

    @Timed(value = "cache.core", extraTags = {OPERATION_TAG_NAME, CACHE_OPERATION_PUT})
    private CV put(CK key, CV value, int ttlInSeconds, boolean updateDb) {
        //TODO get the default value from configuration/properties. Assume 0 means no ttl
        long start = System.currentTimeMillis();
        if (ttlInSeconds > 0) {
            baseCacheService.putWithTtl(key, value, ttlInSeconds);
        } else {
            baseCacheService.put(key, value);
        }
        long end = System.currentTimeMillis();
        if (log().isInfoEnabled()) {
            log().info(String.format("Time taken to put value into cache is %d for key %s ", end-start, key));
        }
        if (updateDb) {
            updateDb(key, value);
        }
        return value;
    }

    @Timed(value = "cache.persistence", extraTags = {OPERATION_TAG_NAME, DB_OPERATION_UPSERT})
    private void updateDb(CK key, CV value) {
        EK entityKey = cacheAndEntityMapper.toEntityKey(key);
        long start = System.currentTimeMillis();
        //TODO - perf- can we avoid returning
        persistenceService.update(cacheAndEntityMapper.toEntityKey(key), cacheAndEntityMapper.toEntity(key, value));
        long end = System.currentTimeMillis();
        if (log().isInfoEnabled()) {
            log().info(String.format("Time taken to upsert value into db is %d for key %s ", end-start, entityKey));
        }
    }

    @Override
    public CV putWithTtl(CK key, CV value, int ttlInSeconds) {
        return put(key, value, ttlInSeconds, true);
    }

    @Override
    @Timed(value = "cache", extraTags = {OPERATION_TAG_NAME, DB_OPERATION_UPSERT})
    public void delete(CK key) {
        Timer coreCacheTimer = meterRegistry.timer("cache.core", CACHE_KEY, key.toString(), OPERATION_TAG_NAME,
                CACHE_OPERATION_DELETE);
        long start = System.currentTimeMillis();
        baseCacheService.delete(key);
        long end = System.currentTimeMillis();
        coreCacheTimer.record(end-start, TimeUnit.MILLISECONDS);
        if (log().isInfoEnabled()) {
            log().info(String.format("Time taken to delete from the cache is %d for key %s ", end-start, key));
        }
    }

    @Override
    public CV remove(CK key) {
        Timer coreCacheTimer = meterRegistry.timer("cache.core", CACHE_KEY, key.toString(), OPERATION_TAG_NAME, CACHE_OPERATION_REMOVE);
        long start = System.currentTimeMillis();
        CV value = baseCacheService.remove(key);
        long end = System.currentTimeMillis();
        coreCacheTimer.record(end-start, TimeUnit.MILLISECONDS);
        if (log().isInfoEnabled()) {
            log().info(String.format("Time taken to remove from the cache is %d for key %s ", end-start, key));
        }
        return value;
    }
}
