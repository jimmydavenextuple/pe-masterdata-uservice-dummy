/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.postgres.service;

import com.nextuple.common.domain.DomainBaseEntity;
import com.nextuple.common.domain.key.DomainKey;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.postgres.entity.CommonBaseEntity;
import com.nextuple.postgres.entity.key.BaseEntityKey;
import com.nextuple.postgres.mapper.DomainToEntityMapper;
import com.nextuple.postgres.repository.CommonJpaRepository;
import java.util.Optional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public abstract class CommonPersistenceService<
        DE extends DomainBaseEntity,
        DK extends DomainKey,
        E extends CommonBaseEntity,
        K extends BaseEntityKey,
        R extends CommonJpaRepository<E, K>,
        M extends DomainToEntityMapper<DE, DK, E, K>>
    implements DomainPersistenceService<DE, DK> {

  @Autowired @Getter R repository;

  @Autowired @Getter M mapper;

  @Override
  public DE save(DE domainEntity) {
    return mapper.toDomain(repository.save(mapper.toEntity(domainEntity)));
  }

  @Override
  public Optional<DE> findByKey(DK dk) {
    K k = mapper.toEntityKey(dk);
    Optional<E> entity = repository.findById(k);
    return entity.isPresent()
        ? Optional.ofNullable(mapper.toDomain(entity.get()))
        : Optional.empty();
  }

  @Override
  public void deleteByKey(DK dk) {
    K k = mapper.toEntityKey(dk);
    repository.deleteById(k);
  }

  @Override
  public void delete(DE entity) {
    repository.delete(mapper.toEntity(entity));
  }

  @Override
  public Page<DE> findAll(Pageable pageable) {
    Page<E> page = repository.findAll(pageable);
    return page.map(e -> mapper.toDomain(e));
  }
}
