/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.enums.ActionEnum;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.service.BatchService;
import com.nextuple.transit.consumer.dto.TransitBufferFeedDto;
import com.nextuple.transit.consumer.mapper.TransitBufferBatchMapper;
import com.nextuple.transit.domain.feign.TransitBufferV2Feign;
import com.nextuple.transit.persistence.domain.TransitBufferV2DomainDto;
import com.nextuple.transit.persistence.service.TransitBufferV2PersistenceService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransitBufferBatchServiceImpl extends BatchService<TransitBufferFeedDto> {

  private final TransitBufferV2Feign transitBufferFeign;
  private final TransitBufferV2PersistenceService transitBufferV2PersistenceService;
  public static final TransitBufferBatchMapper INSTANCE =
      Mappers.getMapper(TransitBufferBatchMapper.class);
  private final TypeReference<BatchRequest<TransitBufferFeedDto>> transitBufferTypeReference =
      new TypeReference<>() {};

  @Override
  public TypeReference<BatchRequest<TransitBufferFeedDto>> getTypeReference() {
    return transitBufferTypeReference;
  }

  @Override
  public String createRecordImpl(TransitBufferFeedDto payload) {
    return transitBufferFeign
        .createTransitBuffer(INSTANCE.toTransitBufferRequest(payload))
        .getMessage();
  }

  @Override
  public String updateRecordImpl(TransitBufferFeedDto payload) throws CommonServiceException {
    handleInvalidAction(ActionEnum.UPDATE);
    return "";
  }

  @Override
  public String deleteRecordImpl(TransitBufferFeedDto payload) {
    return transitBufferFeign
        .deleteTransitBufferRecord(INSTANCE.toTransitBufferDeleteRequest(payload))
        .getMessage();
  }

  @Override
  public void checkForOutdatedRecord(BatchRequest<TransitBufferFeedDto> transitBufferBatchRequest)
      throws CommonServiceException {
    TransitBufferFeedDto transitBufferDto = transitBufferBatchRequest.getPayload();
    String orgId = transitBufferDto.getOrgId();
    String carrierServiceId = transitBufferDto.getCarrierServiceId();
    if (Objects.nonNull(carrierServiceId) && Objects.nonNull(orgId)) {
      Optional<TransitBufferV2DomainDto> transitBufferV2DomainDto =
          transitBufferV2PersistenceService
              .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndBufferStartDateAndBufferEndDate(
                  orgId,
                  transitBufferDto.getDestinationGeozone(),
                  transitBufferDto.getSourceGeozone(),
                  carrierServiceId,
                  transitBufferDto.getBufferStartDate(),
                  transitBufferDto.getBufferEndDate());
      if (transitBufferV2DomainDto.isPresent()
          && (transitBufferV2DomainDto
              .get()
              .getLastModifiedDate()
              .after(transitBufferBatchRequest.getReceivedTimestamp()))) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            "receivedTimestamp",
            FieldError.builder()
                .rejectedValue(transitBufferBatchRequest.getReceivedTimestamp())
                .build());
        errorMap.put(
            "lastUpdatedTimestamp",
            FieldError.builder()
                .rejectedValue(transitBufferV2DomainDto.get().getLastModifiedDate())
                .build());
        throw new CommonServiceException(
            "Can't process the record as it's outdated", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
      }
    }
  }

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.TRANSIT_BUFFER_FEED;
  }
}
