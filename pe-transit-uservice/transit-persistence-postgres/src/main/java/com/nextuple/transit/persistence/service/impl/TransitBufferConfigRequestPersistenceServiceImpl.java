/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service.impl;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.postgres.service.CommonPersistenceService;
import com.nextuple.transit.persistence.domain.TransitBufferConfigRequestDomainDto;
import com.nextuple.transit.persistence.domain.key.TransitBufferConfigRequestDomainKey;
import com.nextuple.transit.persistence.entity.TransitBufferConfigRequestEntity;
import com.nextuple.transit.persistence.entity.key.TransitBufferConfigRequestKey;
import com.nextuple.transit.persistence.mapper.TransitBufferConfigRequestEntityMapper;
import com.nextuple.transit.persistence.repository.TransitBufferConfigRequestRepository;
import com.nextuple.transit.persistence.service.TransitBufferConfigRequestPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransitBufferConfigRequestPersistenceServiceImpl
    extends CommonPersistenceService<
        TransitBufferConfigRequestDomainDto,
        TransitBufferConfigRequestDomainKey,
        TransitBufferConfigRequestEntity,
        TransitBufferConfigRequestKey,
        TransitBufferConfigRequestRepository,
        TransitBufferConfigRequestEntityMapper>
    implements TransitBufferConfigRequestPersistenceService {

  private static final Logger logger =
      LoggerFactory.getLogger(TransitBufferConfigRequestPersistenceServiceImpl.class);

  @Override
  public TransitBufferConfigRequestDomainDto saveTransitBufferConfigRequest(
      TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto)
      throws CommonServiceException {
    try {
      return save(transitBufferConfigRequestDomainDto);
    } catch (Exception e) {
      logger.error("{}, {}", "ERROR_WHILE_SAVING", String.valueOf(e));
      throw new CommonServiceException(
          "ERROR_WHILE_SAVING", HttpStatus.INTERNAL_SERVER_ERROR, 0x1780, null);
    }
  }

  @Override
  public Optional<TransitBufferConfigRequestDomainDto> findById(Long id)
      throws CommonServiceException {
    try {
      return getRepository().findById(id).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("{}, {}", "ERROR_WHILE_FETCHING", String.valueOf(e));
      throw new CommonServiceException(
          "ERROR_WHILE_FETCHING", HttpStatus.INTERNAL_SERVER_ERROR, 0x1780, null);
    }
  }

  @Override
  public List<TransitBufferConfigRequestDomainDto> findByOrgIdAndCarrierServiceId(
      String orgId, String carrierServiceId, List<String> status) throws CommonServiceException {
    try {
      List<TransitBufferConfigRequestEntity> transitBufferConfigRequestEntities =
          getRepository().findByOrgIdAndCarrierServiceId(orgId, carrierServiceId, status);
      return getMapper().toDomain(transitBufferConfigRequestEntities);
    } catch (Exception e) {
      logger.error("{}, {}", "ERROR_WHILE_FETCHING", String.valueOf(e));
      throw new CommonServiceException(
          "ERROR_WHILE_FETCHING", HttpStatus.INTERNAL_SERVER_ERROR, 0x1780, null);
    }
  }
}
