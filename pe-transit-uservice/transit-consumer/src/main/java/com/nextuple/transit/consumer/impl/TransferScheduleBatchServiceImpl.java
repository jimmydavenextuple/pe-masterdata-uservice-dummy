/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.transit.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.service.BatchService;
import com.nextuple.transit.consumer.dto.TransferScheduleDto;
import com.nextuple.transit.consumer.mapper.TransferScheduleBatchMapper;
import com.nextuple.transit.domain.feign.TransferScheduleFeign;
import com.nextuple.transit.persistence.entity.TransferScheduleEntity;
import com.nextuple.transit.persistence.repository.TransferScheduleRepository;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferScheduleBatchServiceImpl extends BatchService<TransferScheduleDto> {
  private final TransferScheduleFeign transferScheduleFeign;
  private final TransferScheduleRepository transferScheduleRepository;

  private final TypeReference<BatchRequest<TransferScheduleDto>> transferScheduleTypeReference =
      new TypeReference<>() {};

  private static final Logger logger =
      LoggerFactory.getLogger(TransferScheduleBatchServiceImpl.class);

  // create and implement a mapper
  public static final TransferScheduleBatchMapper INSTANCE =
      Mappers.getMapper(TransferScheduleBatchMapper.class);

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.TRANSFER_SCHEDULE;
  }

  @Override
  public TypeReference<BatchRequest<TransferScheduleDto>> getTypeReference() {
    return transferScheduleTypeReference;
  }

  @Override
  public String createRecordImpl(TransferScheduleDto payload) throws CommonServiceException {
    return transferScheduleFeign
        .createTransferSchedule(INSTANCE.toTransferScheduleCreateRequest(payload))
        .getMessage();
  }

  @Override
  public String updateRecordImpl(TransferScheduleDto payload) throws CommonServiceException {
    handleInvalidAction(ActionEnum.UPDATE);
    return "";
  }

  @Override
  public String deleteRecordImpl(TransferScheduleDto payload) throws CommonServiceException {
    return transferScheduleFeign
        .deleteTransferSchedule(INSTANCE.toTransferScheduleRequest(payload))
        .getMessage();
  }

  @Override
  public void checkForOutdatedRecord(
      BatchRequest<TransferScheduleDto> transferScheduleDtoBatchRequest)
      throws CommonServiceException {
    TransferScheduleDto transferScheduleDto = transferScheduleDtoBatchRequest.getPayload();
    String orgId = transferScheduleDto.getOrgId();
    String sourceNodeId = transferScheduleDto.getSourceNodeId();
    String dropOffNodeId = transferScheduleDto.getDropoffNodeId();
    DateTime startDateTime = transferScheduleDto.getStartTime();
    Date startTime = Objects.nonNull(startDateTime) ? startDateTime.toDate() : null;
    String rule = transferScheduleDto.getRule();
    if (validateRequiredFieldsProvided(orgId, sourceNodeId, dropOffNodeId, startTime)) {
      Optional<TransferScheduleEntity> transferScheduleEntity;
      if (Objects.nonNull(rule)) {
        transferScheduleEntity =
            transferScheduleRepository
                .findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgIdAndRule(
                    sourceNodeId, dropOffNodeId, startTime, orgId, rule);
      } else {
        transferScheduleEntity =
            transferScheduleRepository.findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgId(
                sourceNodeId, dropOffNodeId, startTime, orgId);
      }
      if (checkForBatchRequestExpired(transferScheduleDtoBatchRequest, transferScheduleEntity)) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            "receivedTimestamp",
            FieldError.builder()
                .rejectedValue(transferScheduleDtoBatchRequest.getReceivedTimestamp())
                .build());
        errorMap.put(
            "lastUpdatedTimestamp",
            FieldError.builder()
                .rejectedValue(transferScheduleEntity.get().getLastModifiedDate())
                .build());
        throw new CommonServiceException(
            "Can't process the record as it's outdated", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
      }
    }
  }

  private static boolean checkForBatchRequestExpired(
      BatchRequest<TransferScheduleDto> transferScheduleDtoBatchRequest,
      Optional<TransferScheduleEntity> transferScheduleEntity) {
    return transferScheduleEntity.isPresent()
        && (transferScheduleEntity
            .get()
            .getLastModifiedDate()
            .after(transferScheduleDtoBatchRequest.getReceivedTimestamp()));
  }

  private static boolean validateRequiredFieldsProvided(
      String orgId, String sourceNodeId, String dropOffNodeId, Date startTime) {
    return Objects.nonNull(orgId)
        && Objects.nonNull(sourceNodeId)
        && Objects.nonNull(dropOffNodeId)
        && Objects.nonNull(startTime);
  }
}
