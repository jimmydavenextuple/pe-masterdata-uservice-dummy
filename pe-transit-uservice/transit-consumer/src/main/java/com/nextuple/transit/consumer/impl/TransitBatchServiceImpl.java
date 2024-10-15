/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.service.BatchService;
import com.nextuple.transit.consumer.dto.TransitFeedDto;
import com.nextuple.transit.consumer.mapper.TransitBatchMapper;
import com.nextuple.transit.domain.feign.TransitFeign;
import com.nextuple.transit.persistence.domain.TransitDomainDto;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.persistence.service.TransitPersistenceService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransitBatchServiceImpl extends BatchService<TransitFeedDto> {

  private final TransitFeign transitFeign;
  private final TransitPersistenceService transitPersistenceService;
  public static final TransitBatchMapper INSTANCE = Mappers.getMapper(TransitBatchMapper.class);
  private final TypeReference<BatchRequest<TransitFeedDto>> transitTypeReference =
      new TypeReference<>() {};
  private static final Logger logger = LoggerFactory.getLogger(TransitBatchServiceImpl.class);

  @Override
  public TypeReference<BatchRequest<TransitFeedDto>> getTypeReference() {
    return transitTypeReference;
  }

  @Override
  public String createRecordImpl(TransitFeedDto payload) {
    return transitFeign.addTransitData(INSTANCE.toTransitRequest(payload)).getMessage();
  }

  @Override
  public String updateRecordImpl(TransitFeedDto payload) {
    return transitFeign
        .updateTransitData(
            payload.getOrgId(),
            payload.getSourceGeozone(),
            payload.getDestinationGeozone(),
            payload.getCarrierServiceId(),
            INSTANCE.toTransitUpdateRequest(payload))
        .getMessage();
  }

  @Override
  public String deleteRecordImpl(TransitFeedDto payload) {
    return transitFeign
        .deleteTransitDetails(
            payload.getOrgId(),
            payload.getSourceGeozone(),
            payload.getDestinationGeozone(),
            payload.getCarrierServiceId())
        .getMessage();
  }

  public void checkForOutdatedRecord(BatchRequest<TransitFeedDto> transitBatchRequest)
      throws CommonServiceException {
    TransitFeedDto transitDto = transitBatchRequest.getPayload();
    String orgId = transitDto.getOrgId();
    String carrierServiceId = transitDto.getCarrierServiceId();
    if (Objects.nonNull(carrierServiceId) && Objects.nonNull(orgId)) {
      try {
        Optional<TransitDomainDto> transitDomainDto =
            transitPersistenceService.findTransitDetails(
                orgId,
                transitDto.getSourceGeozone(),
                transitDto.getDestinationGeozone(),
                carrierServiceId);
        if (transitDomainDto.isPresent()
            && (transitDomainDto
                .get()
                .getLastModifiedDate()
                .after(transitBatchRequest.getReceivedTimestamp()))) {
          Map<String, FieldError> errorMap = new HashMap<>();
          errorMap.put(
              "receivedTimestamp",
              FieldError.builder()
                  .rejectedValue(transitBatchRequest.getReceivedTimestamp())
                  .build());
          errorMap.put(
              "lastUpdatedTimestamp",
              FieldError.builder()
                  .rejectedValue(transitDomainDto.get().getLastModifiedDate())
                  .build());
          throw new CommonServiceException(
              "Can't process the record as it's outdated",
              HttpStatus.BAD_REQUEST,
              0x1771,
              errorMap);
        }
      } catch (TransitDomainException e) {
        logger.debug(
            "Cannot check for outdated record as the given transit does not exist for the details orgId:{} sourceGeoZone:{} destinationGeoZone:{} carrierServiceId:{}",
            orgId,
            transitDto.getSourceGeozone(),
            transitDto.getDestinationGeozone(),
            carrierServiceId);
      }
    }
  }

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.TRANSIT_FEED;
  }
}
