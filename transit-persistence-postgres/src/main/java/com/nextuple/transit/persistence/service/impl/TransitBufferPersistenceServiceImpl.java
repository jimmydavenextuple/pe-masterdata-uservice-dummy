/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service.impl;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.postgres.service.CommonPersistenceService;
import com.nextuple.transit.persistence.domain.TransitBufferDomainDto;
import com.nextuple.transit.persistence.domain.key.TransitDomainKey;
import com.nextuple.transit.persistence.entity.TransitBufferEntity;
import com.nextuple.transit.persistence.entity.key.TransitKey;
import com.nextuple.transit.persistence.mapper.TransitBufferEntityMapper;
import com.nextuple.transit.persistence.repository.TransitBufferRepository;
import com.nextuple.transit.persistence.service.TransitBufferPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransitBufferPersistenceServiceImpl
    extends CommonPersistenceService<
        TransitBufferDomainDto,
        TransitDomainKey,
        TransitBufferEntity,
        TransitKey,
        TransitBufferRepository,
        TransitBufferEntityMapper>
    implements TransitBufferPersistenceService {
  private static final Logger logger =
      LoggerFactory.getLogger(TransitBufferPersistenceServiceImpl.class);

  @Override
  public List<TransitBufferDomainDto> findByOrgIdAndDestinationGeozone(
      String orgId, String destinationGeozone) throws CommonServiceException {
    try {
      List<TransitBufferEntity> transitBufferEntities =
          getRepository().findByOrgIdAndDestinationGeozone(orgId, destinationGeozone);
      return getMapper().toDomain(transitBufferEntities);
    } catch (Exception e) {
      logger.error("{}, {}", "ERROR_WHILE_FETCHING", String.valueOf(e));
      throw new CommonServiceException(
          "ERROR_WHILE_FETCHING", HttpStatus.INTERNAL_SERVER_ERROR, 0x1780, null);
    }
  }

  @Override
  public Optional<TransitBufferDomainDto>
      findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
          String orgId, String carrierServiceId, String sourceGeozone, String destinationGeozone)
          throws CommonServiceException {
    try {
      return findByKey(
          TransitDomainKey.builder()
              .orgId(orgId)
              .carrierServiceId(carrierServiceId)
              .sourceGeozone(sourceGeozone)
              .destinationGeozone(destinationGeozone)
              .build());
    } catch (Exception e) {
      logger.error("{}, {}", "ERROR_WHILE_FETCHING", String.valueOf(e));
      throw new CommonServiceException(
          "ERROR_WHILE_FETCHING", HttpStatus.INTERNAL_SERVER_ERROR, 0x1780, null);
    }
  }

  @Override
  public TransitBufferDomainDto deleteTransitBuffer(TransitBufferDomainDto transitBufferDomainDto)
      throws CommonServiceException {
    try {
      delete(transitBufferDomainDto);
      return transitBufferDomainDto;
    } catch (Exception e) {
      logger.error("{}, {}", "ERROR_WHILE_DELETING", String.valueOf(e));
      throw new CommonServiceException(
          "ERROR_WHILE_DELETING", HttpStatus.INTERNAL_SERVER_ERROR, 0x1780, null);
    }
  }

  @Override
  public List<TransitBufferDomainDto> findByTransitBufferConfigRequestId(
      Long transitBufferConfigRequestId) throws CommonServiceException {
    try {
      return getMapper()
          .toDomain(
              getRepository().findByTransitBufferConfigRequestId(transitBufferConfigRequestId));
    } catch (Exception e) {
      logger.error("{}, {}", "ERROR_WHILE_FETCHING", String.valueOf(e));
      throw new CommonServiceException(
          "ERROR_WHILE_FETCHING", HttpStatus.INTERNAL_SERVER_ERROR, 0x1780, null);
    }
  }

  @Override
  public TransitBufferDomainDto saveTransitBuffer(TransitBufferDomainDto transitBufferDomainDto)
      throws CommonServiceException {
    try {
      return save(transitBufferDomainDto);
    } catch (Exception e) {
      logger.error("{}, {}", "ERROR_WHILE_CREATING", String.valueOf(e));
      throw new CommonServiceException(
          "ERROR_WHILE_CREATING", HttpStatus.INTERNAL_SERVER_ERROR, 0x1780, null);
    }
  }
}
