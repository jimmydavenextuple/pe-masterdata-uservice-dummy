/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service.impl;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.postgres.service.CommonPersistenceService;
import com.nextuple.transit.persistence.domain.TransitBufferReqJobRefDomainDto;
import com.nextuple.transit.persistence.domain.key.TransitBufferReqJobRefDomainKey;
import com.nextuple.transit.persistence.entity.TransitBufferReqJobRefEntity;
import com.nextuple.transit.persistence.entity.key.TransitBufferReqJobRefKey;
import com.nextuple.transit.persistence.mapper.TransitBufferReqJobRefEntityMapper;
import com.nextuple.transit.persistence.repository.TransitBufferReqJobRefRepository;
import com.nextuple.transit.persistence.service.TransitBufferReqJobRefPersistenceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransitBufferReqJobRefPersistenceServiceImpl
    extends CommonPersistenceService<
        TransitBufferReqJobRefDomainDto,
        TransitBufferReqJobRefDomainKey,
        TransitBufferReqJobRefEntity,
        TransitBufferReqJobRefKey,
        TransitBufferReqJobRefRepository,
        TransitBufferReqJobRefEntityMapper>
    implements TransitBufferReqJobRefPersistenceService {

  private static final String ERROR_WHILE_CREATING =
      "Error while creating transit buffer req job ref";

  @Override
  public TransitBufferReqJobRefDomainDto saveTransitBufferReqJobRefRepository(
      TransitBufferReqJobRefDomainDto transitBufferReqJobRefDomainDto)
      throws CommonServiceException {
    try {
      return save(transitBufferReqJobRefDomainDto);
    } catch (Exception e) {
      log.error("{}, {}", ERROR_WHILE_CREATING, String.valueOf(e));
      throw new CommonServiceException(
          ERROR_WHILE_CREATING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1780, null);
    }
  }

  @Override
  public List<TransitBufferReqJobRefDomainDto> findByExtReferenceId(String extReferenceId)
      throws CommonServiceException {
    try {
      List<TransitBufferReqJobRefEntity> transitBufferReqJobRefEntities =
          getRepository().findByExtReferenceId(extReferenceId);
      return getMapper().toDomain(transitBufferReqJobRefEntities);
    } catch (Exception e) {
      log.error("{}, {}", ERROR_WHILE_CREATING, String.valueOf(e));
      throw new CommonServiceException(
          ERROR_WHILE_CREATING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1780, null);
    }
  }
}
