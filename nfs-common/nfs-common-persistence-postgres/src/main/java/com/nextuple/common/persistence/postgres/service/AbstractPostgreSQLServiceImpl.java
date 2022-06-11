package com.nextuple.common.persistence.postgres.service;

import static com.nextuple.common.constants.CommonConstants.*;

import com.nextuple.common.dto.Entity;
import com.nextuple.common.dto.key.EntityKey;
import com.nextuple.common.persistence.postgres.dto.PostgresEntity;
import com.nextuple.common.persistence.postgres.dto.key.PostgresEntityKey;
import com.nextuple.common.persistence.postgres.mapper.GenericEntityMapper;
import com.nextuple.common.service.GenericPersistenceService;
import io.micrometer.core.annotation.Timed;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

public abstract class AbstractPostgreSQLServiceImpl<
        PK extends PostgresEntityKey,
        PV extends PostgresEntity,
        EK extends EntityKey,
        EV extends Entity>
    implements GenericPersistenceService<EK, EV> {
  @Autowired CrudRepository<PV, PK> repository;

  @Autowired GenericEntityMapper<PK, PV, EK, EV> mapper;

  protected abstract Logger log();

  @Override
  @Timed(
      value = "postgres.persistence",
      extraTags = {OPERATION_TAG_NAME, DB_OPERATION_INSERT})
  public EV create(EK key, EV entity) {
    return mapper.dtoToEntity(repository.save(mapper.entityToDto(entity)));
  }

  @Override
  @Timed(
      value = "postgres.persistence",
      extraTags = {OPERATION_TAG_NAME, DB_OPERATION_UPDATE})
  public EV update(EK key, EV entity) {
    return mapper.dtoToEntity(repository.save(mapper.entityToDto(entity)));
  }

  @Override
  @Timed(
      value = "postgres.persistence",
      extraTags = {OPERATION_TAG_NAME, DB_OPERATION_READ})
  public EV get(EK key) {
    return repository
        .findById(mapper.entityKeyToDtoKey(key))
        .map(pv -> mapper.dtoToEntity(pv))
        .orElse(null);
  }

  @Override
  @Timed(
      value = "postgres.persistence",
      extraTags = {OPERATION_TAG_NAME, DB_OPERATION_READ_ALL})
  public Set<EV> getAll(Set<EK> keys) {
    Set<EV> values = new HashSet<>();
    repository
        .findAllById(
            keys.stream().map(ek -> mapper.entityKeyToDtoKey(ek)).collect(Collectors.toList()))
        .forEach(pv -> values.add(mapper.dtoToEntity(pv)));
    return values;
  }

  @Override
  @Timed(
      value = "postgres.persistence",
      extraTags = {OPERATION_TAG_NAME, DB_OPERATION_DELETE})
  public void delete(EK key) {
    repository.deleteById(mapper.entityKeyToDtoKey(key));
  }
}
