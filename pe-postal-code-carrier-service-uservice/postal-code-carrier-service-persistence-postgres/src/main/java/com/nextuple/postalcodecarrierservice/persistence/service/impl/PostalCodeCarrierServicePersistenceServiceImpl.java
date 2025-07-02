/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postalcodecarrierservice.persistence.service.impl;

import com.nextuple.postalcodecarrierservice.persistence.domain.PostalCodeCarrierServiceDomainDto;
import com.nextuple.postalcodecarrierservice.persistence.domain.key.PostalCodeCarrierServiceDomainKey;
import com.nextuple.postalcodecarrierservice.persistence.entity.PostalCodeCarrierServiceEntity;
import com.nextuple.postalcodecarrierservice.persistence.entity.key.PostalCodeCarrierServiceKey;
import com.nextuple.postalcodecarrierservice.persistence.mapper.PostalCodeCarrierServiceEntityMapper;
import com.nextuple.postalcodecarrierservice.persistence.repository.PostalCodeCarrierServiceRepository;
import com.nextuple.postalcodecarrierservice.persistence.service.PostalCodeCarrierServicePersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostalCodeCarrierServicePersistenceServiceImpl
    implements PostalCodeCarrierServicePersistenceService {

  private final PostalCodeCarrierServiceRepository repository;
  private final PostalCodeCarrierServiceEntityMapper mapper;

  @Override
  public PostalCodeCarrierServiceDomainDto save(PostalCodeCarrierServiceDomainDto domainDto) {
    log.debug("Saving postal code carrier service: {}", domainDto);
    PostalCodeCarrierServiceEntity entity = mapper.domainToEntity(domainDto);
    PostalCodeCarrierServiceEntity savedEntity = repository.save(entity);
    return mapper.entityToDomain(savedEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<PostalCodeCarrierServiceDomainDto> findById(
      PostalCodeCarrierServiceDomainKey key) {
    log.debug("Finding postal code carrier service by key: {}", key);
    PostalCodeCarrierServiceKey entityKey =
        PostalCodeCarrierServiceKey.builder()
            .zipcode(key.getZipcode())
            .carrierServiceId(key.getCarrierServiceId())
            .build();

    return repository.findById(entityKey).map(mapper::entityToDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PostalCodeCarrierServiceDomainDto> findAll(Pageable pageable) {
    log.debug("Finding all postal code carrier services with pageable: {}", pageable);
    return repository.findAll(pageable).map(mapper::entityToDomain);
  }

  @Override
  public void deleteById(PostalCodeCarrierServiceDomainKey key) {
    log.debug("Deleting postal code carrier service by key: {}", key);
    PostalCodeCarrierServiceKey entityKey =
        PostalCodeCarrierServiceKey.builder()
            .zipcode(key.getZipcode())
            .carrierServiceId(key.getCarrierServiceId())
            .build();

    repository.deleteById(entityKey);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsById(PostalCodeCarrierServiceDomainKey key) {
    log.debug("Checking if postal code carrier service exists by key: {}", key);
    PostalCodeCarrierServiceKey entityKey =
        PostalCodeCarrierServiceKey.builder()
            .zipcode(key.getZipcode())
            .carrierServiceId(key.getCarrierServiceId())
            .build();

    return repository.existsById(entityKey);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PostalCodeCarrierServiceDomainDto> findByZipcode(String zipcode) {
    log.debug("Finding postal code carrier services by zipcode: {}", zipcode);
    List<PostalCodeCarrierServiceEntity> entities = repository.findByZipcode(zipcode);
    return mapper.entitiesToDomains(entities);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PostalCodeCarrierServiceDomainDto> findByCarrierServiceId(String carrierServiceId) {
    log.debug("Finding postal code carrier services by carrier service id: {}", carrierServiceId);
    List<PostalCodeCarrierServiceEntity> entities =
        repository.findByCarrierServiceId(carrierServiceId);
    return mapper.entitiesToDomains(entities);
  }
}
