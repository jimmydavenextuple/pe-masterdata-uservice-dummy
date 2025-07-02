/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postalcodecarrierservice.service.impl;

import com.nextuple.postalcodecarrierservice.domain.dto.PostalCodeCarrierServiceCacheKeyDto;
import com.nextuple.postalcodecarrierservice.domain.dto.PostalCodeCarrierServiceDto;
import com.nextuple.postalcodecarrierservice.domain.inbound.PostalCodeCarrierServiceRequest;
import com.nextuple.postalcodecarrierservice.domain.mapper.PostalCodeCarrierServiceMapper;
import com.nextuple.postalcodecarrierservice.persistence.domain.PostalCodeCarrierServiceDomainDto;
import com.nextuple.postalcodecarrierservice.persistence.domain.key.PostalCodeCarrierServiceDomainKey;
import com.nextuple.postalcodecarrierservice.persistence.service.PostalCodeCarrierServicePersistenceService;
import com.nextuple.postalcodecarrierservice.service.PostalCodeCarrierService;
import java.util.List;
import java.util.stream.Collectors;
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
public class PostalCodeCarrierServiceImpl implements PostalCodeCarrierService {

  private final PostalCodeCarrierServicePersistenceService persistenceService;
  private final PostalCodeCarrierServiceMapper mapper;

  @Override
  public PostalCodeCarrierServiceDto createPostalCodeCarrierService(
      PostalCodeCarrierServiceRequest request) {
    log.info(
        "Creating postal code carrier service for zipcode: {} and carrier service: {}",
        request.getZipcode(),
        request.getCarrierServiceId());

    // Check if already exists
    PostalCodeCarrierServiceDomainKey key =
        PostalCodeCarrierServiceDomainKey.builder()
            .zipcode(request.getZipcode())
            .carrierServiceId(request.getCarrierServiceId())
            .build();

    if (persistenceService.existsById(key)) {
      throw new IllegalArgumentException(
          String.format(
              "Postal code carrier service already exists for zipcode: %s and carrier service: %s",
              request.getZipcode(), request.getCarrierServiceId()));
    }

    PostalCodeCarrierServiceDomainDto domainDto = mapper.requestToDomain(request);
    PostalCodeCarrierServiceDomainDto savedDomainDto = persistenceService.save(domainDto);

    log.info(
        "Successfully created postal code carrier service with zipcode: {} and carrier service: {}",
        savedDomainDto.getZipcode(),
        savedDomainDto.getCarrierServiceId());

    return mapper.domainToDto(savedDomainDto);
  }

  @Override
  @Transactional(readOnly = true)
  public PostalCodeCarrierServiceDto getPostalCodeCarrierService(
      String zipcode, String carrierServiceId) {
    log.info(
        "Getting postal code carrier service for zipcode: {} and carrier service: {}",
        zipcode,
        carrierServiceId);

    PostalCodeCarrierServiceDomainKey key =
        PostalCodeCarrierServiceDomainKey.builder()
            .zipcode(zipcode)
            .carrierServiceId(carrierServiceId)
            .build();

    return persistenceService
        .findById(key)
        .map(mapper::domainToDto)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    String.format(
                        "Postal code carrier service not found for zipcode: %s and carrier service: %s",
                        zipcode, carrierServiceId)));
  }

  @Override
  public PostalCodeCarrierServiceDto updatePostalCodeCarrierService(
      String zipcode, String carrierServiceId, PostalCodeCarrierServiceRequest request) {
    log.info(
        "Updating postal code carrier service for zipcode: {} and carrier service: {}",
        zipcode,
        carrierServiceId);

    PostalCodeCarrierServiceDomainKey key =
        PostalCodeCarrierServiceDomainKey.builder()
            .zipcode(zipcode)
            .carrierServiceId(carrierServiceId)
            .build();

    PostalCodeCarrierServiceDomainDto existingDomainDto =
        persistenceService
            .findById(key)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        String.format(
                            "Postal code carrier service not found for zipcode: %s and carrier service: %s",
                            zipcode, carrierServiceId)));

    // Update fields
    existingDomainDto.setIsRedZone(request.getIsRedZone());
    existingDomainDto.setRedZoneReason(request.getRedZoneReason());

    PostalCodeCarrierServiceDomainDto updatedDomainDto = persistenceService.save(existingDomainDto);

    log.info(
        "Successfully updated postal code carrier service for zipcode: {} and carrier service: {}",
        zipcode,
        carrierServiceId);

    return mapper.domainToDto(updatedDomainDto);
  }

  @Override
  public void deletePostalCodeCarrierService(String zipcode, String carrierServiceId) {
    log.info(
        "Deleting postal code carrier service for zipcode: {} and carrier service: {}",
        zipcode,
        carrierServiceId);

    PostalCodeCarrierServiceDomainKey key =
        PostalCodeCarrierServiceDomainKey.builder()
            .zipcode(zipcode)
            .carrierServiceId(carrierServiceId)
            .build();

    if (!persistenceService.existsById(key)) {
      throw new IllegalArgumentException(
          String.format(
              "Postal code carrier service not found for zipcode: %s and carrier service: %s",
              zipcode, carrierServiceId));
    }

    persistenceService.deleteById(key);

    log.info(
        "Successfully deleted postal code carrier service for zipcode: {} and carrier service: {}",
        zipcode,
        carrierServiceId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PostalCodeCarrierServiceDto> getPostalCodeCarrierServices(Pageable pageable) {
    log.info("Getting postal code carrier services with pageable: {}", pageable);

    return persistenceService.findAll(pageable).map(mapper::domainToDto);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PostalCodeCarrierServiceDto> getPostalCodeCarrierServicesByZipcode(String zipcode) {
    log.info("Getting postal code carrier services by zipcode: {}", zipcode);

    List<PostalCodeCarrierServiceDomainDto> domainDtos = persistenceService.findByZipcode(zipcode);
    return mapper.domainsToDtos(domainDtos);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PostalCodeCarrierServiceDto> getPostalCodeCarrierServicesByCarrierServiceId(
      String carrierServiceId) {
    log.info("Getting postal code carrier services by carrier service id: {}", carrierServiceId);

    List<PostalCodeCarrierServiceDomainDto> domainDtos =
        persistenceService.findByCarrierServiceId(carrierServiceId);
    return mapper.domainsToDtos(domainDtos);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PostalCodeCarrierServiceCacheKeyDto> getAllCacheKeys() {
    log.info("Getting all cache keys for postal code carrier services");

    return persistenceService.findAll(Pageable.unpaged()).getContent().stream()
        .map(
            domainDto ->
                PostalCodeCarrierServiceCacheKeyDto.builder()
                    .zipcode(domainDto.getZipcode())
                    .carrierServiceId(domainDto.getCarrierServiceId())
                    .build())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsPostalCodeCarrierService(String zipcode, String carrierServiceId) {
    log.info(
        "Checking if postal code carrier service exists for zipcode: {} and carrier service: {}",
        zipcode,
        carrierServiceId);

    PostalCodeCarrierServiceDomainKey key =
        PostalCodeCarrierServiceDomainKey.builder()
            .zipcode(zipcode)
            .carrierServiceId(carrierServiceId)
            .build();

    return persistenceService.existsById(key);
  }
}
