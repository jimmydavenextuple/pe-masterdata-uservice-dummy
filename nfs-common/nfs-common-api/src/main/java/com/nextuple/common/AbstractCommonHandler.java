package com.nextuple.common;

import static com.nextuple.common.constants.CommonConstants.DB_OPERATION_READ;
import static com.nextuple.common.constants.CommonConstants.OPERATION_TAG_NAME;

import com.nextuple.common.dto.Entity;
import com.nextuple.common.dto.key.EntityKey;
import com.nextuple.common.service.GenericPersistenceService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractCommonHandler<K extends EntityKey, E extends Entity> {

  @Autowired protected GenericPersistenceService<K, E> persistenceService;

  @Autowired protected MeterRegistry registry;

  protected abstract Logger log();

  public E create(K key, E entity) {
    log().trace("Inserting a new record for {} with value {} ", key, entity);
    return persistenceService.create(key, entity);
  }

  public E update(K key, E entity) {
    log().trace("Updating a new record for {} with value {} ", key, entity);
    return persistenceService.update(key, entity);
  }

  public E get(K key) {
    long start = System.currentTimeMillis();
    E entity = persistenceService.get(key);
    Timer readTimer = registry.timer("core.persistence", OPERATION_TAG_NAME, DB_OPERATION_READ);
    long end = System.currentTimeMillis();
    readTimer.record(end - start, TimeUnit.MILLISECONDS);
    log().trace("Fetching details for {}", key);
    return entity;
  }

  public void delete(K key) {
    log().trace("Deleting a the record with key {} ", key);
    persistenceService.delete(key);
  }
}
