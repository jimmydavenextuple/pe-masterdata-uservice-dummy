/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.transit.domain.inbound.TransitBufferReqJobRefRequest;
import com.nextuple.transit.domain.mapper.TransitBufferReqJobRefMapper;
import com.nextuple.transit.domain.outbound.TransitBufferReqJobRefResponse;
import com.nextuple.transit.persistence.domain.TransitBufferReqJobRefDomainDto;
import com.nextuple.transit.persistence.exception.TransitBufferReqJobRefDomainException;
import com.nextuple.transit.persistence.service.TransitBufferReqJobRefPersistenceService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransitBufferReqJobRefService {

  private final TransitBufferReqJobRefPersistenceService transitBufferReqJobRefPersistenceService;

  public static final TransitBufferReqJobRefMapper INSTANCE =
      Mappers.getMapper(TransitBufferReqJobRefMapper.class);

  private static final Logger logger = LoggerFactory.getLogger(TransitBufferReqJobRefService.class);

  public TransitBufferReqJobRefResponse createTransitBufferReqJobRef(
      TransitBufferReqJobRefRequest transitBufferReqJobRefRequest)
      throws TransitBufferReqJobRefDomainException {
    try {
      var transitBufferReqJobRefDomainDto =
          INSTANCE.toTransitBufferReqJobRefDomainDto(transitBufferReqJobRefRequest);
      return INSTANCE.toTransitBufferReqJobRefResponse(
          transitBufferReqJobRefPersistenceService.saveTransitBufferReqJobRefRepository(
              transitBufferReqJobRefDomainDto));
    } catch (Exception e) {
      logger.error("Failed to create transit buffer request job reference");
      throw new TransitBufferReqJobRefDomainException(
          e.getMessage(),
          transitBufferReqJobRefRequest.getTransitBufferReqId(),
          transitBufferReqJobRefRequest.getExtReferenceId());
    }
  }

  public List<TransitBufferReqJobRefResponse> getTransitBufferReqJobRefByExtReferenceId(String id)
      throws TransitBufferReqJobRefDomainException {

    try {
      List<TransitBufferReqJobRefDomainDto> transitBufferReqJobRefDomainList =
          transitBufferReqJobRefPersistenceService.findByExtReferenceId(id);

      if (!transitBufferReqJobRefDomainList.isEmpty()) {
        List<TransitBufferReqJobRefResponse> result = new ArrayList<>();
        transitBufferReqJobRefDomainList.forEach(
            entity -> result.add(INSTANCE.toTransitBufferReqJobRefResponse(entity)));

        return result;
      } else {
        logger.error("Failed to fetch transit buffer request job reference");
        throw new TransitBufferReqJobRefDomainException(
            "Unable to find transit buffer job references with this ID: " + id, null, id);
      }
    } catch (Exception e) {
      logger.error("Failed to fetch transit buffer request job reference");
      throw new TransitBufferReqJobRefDomainException(e.getMessage(), null, id);
    }
  }
}
