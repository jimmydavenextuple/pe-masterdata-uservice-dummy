/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service.impl;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.postgres.service.CommonPersistenceService;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainDto;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainRequest;
import com.nextuple.transit.persistence.domain.key.TransferScheduleDomainKey;
import com.nextuple.transit.persistence.entity.TransferScheduleEntity;
import com.nextuple.transit.persistence.entity.key.TransferScheduleKey;
import com.nextuple.transit.persistence.mapper.TransferScheduleEntityMapper;
import com.nextuple.transit.persistence.repository.TransferScheduleRepository;
import com.nextuple.transit.persistence.service.TransferSchedulePersistenceService;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferSchedulePersistenceServiceImpl
    extends CommonPersistenceService<
        TransferScheduleDomainDto,
        TransferScheduleDomainKey,
        TransferScheduleEntity,
        TransferScheduleKey,
        TransferScheduleRepository,
        TransferScheduleEntityMapper>
    implements TransferSchedulePersistenceService {
  private static final Logger logger =
      LoggerFactory.getLogger(TransferSchedulePersistenceServiceImpl.class);

  @Override
  public TransferScheduleDomainDto saveTransferSchedule(
      TransferScheduleDomainDto transferScheduleDomainDto) throws PromiseEngineException {
    try {
      return save(transferScheduleDomainDto);
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save transfer schedule " + e.getMessage());
    }
  }

  @Override
  public List<TransferScheduleDomainDto> fetchUpcomingTransferSchedules(
      String orgId, String dropOffNodeId) {
    List<TransferScheduleEntity> transferScheduleEntities =
        getRepository().findByDropoffNodeIdAndOrgId(dropOffNodeId, orgId);
    return getMapper().toDomain(transferScheduleEntities);
  }

  @Override
  public TransferScheduleDomainDto deleteTransferSchedule(
      String orgId, String sourceNodeId, String dropOffNodeId, Date startTime)
      throws PromiseEngineException, CommonServiceException {
    Optional<TransferScheduleEntity> entityOptional =
        getRepository()
            .findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgId(
                sourceNodeId, dropOffNodeId, startTime, orgId);
    if (entityOptional.isEmpty()) {
      logger.error("Unable to find transfer schedule");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put("orgId", FieldError.builder().rejectedValue(orgId).build());
      errorMap.put("sourceNodeId", FieldError.builder().rejectedValue(sourceNodeId).build());
      errorMap.put("dropoffNodeId", FieldError.builder().rejectedValue(dropOffNodeId).build());
      errorMap.put("startTime", FieldError.builder().rejectedValue(startTime).build());
      throw new CommonServiceException(
          "Transfer Schedule not found for given orgId, sourceNodeId, dropoffNodeId and startTime",
          HttpStatus.NOT_FOUND,
          0X2771,
          errorMap);
    }
    TransferScheduleDomainDto domainDto = getMapper().toDomain(entityOptional.get());
    delete(domainDto);
    return domainDto;
  }

  @Override
  public Page<TransferScheduleResponse> fetchTransferSchedulesList(
      String orgId, FetchTransferScheduleRequest request, Pageable pageable)
      throws PromiseEngineException {
    try {
      Page<TransferScheduleEntity> transferScheduleEntities =
          getRepository().findFilteredTransferSchedules(orgId, request, pageable);
      List<TransferScheduleResponse> transferScheduleResponses =
          transferScheduleEntities.getContent().stream()
              .map(getMapper()::convertToTransferScheduleResponseFromEntity)
              .toList();
      return new PageImpl<>(
          transferScheduleResponses, pageable, transferScheduleEntities.getTotalElements());
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch transfer schedule " + e.getMessage());
    }
  }

  @Override
  public List<TransferScheduleDomainDto> fetchTransferSchedulesInRange(
      TransferScheduleDomainRequest request) {
    List<TransferScheduleEntity> entities = getRepository().findTransferSchedulesInRange(request);
    return getMapper().toDomain(entities);
  }
}
