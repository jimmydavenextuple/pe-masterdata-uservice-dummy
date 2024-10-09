/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.carrier.consumer.dto.CarrierFeedDto;
import com.nextuple.carrier.consumer.mapper.CarrierBatchMapper;
import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.carrier.persistence.domain.CarrierServiceDomainDto;
import com.nextuple.carrier.persistence.exception.CarrierServiceDomainException;
import com.nextuple.carrier.persistence.service.CarrierServicePersistenceService;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.service.BatchService;
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
public class CarrierBatchServiceImpl extends BatchService<CarrierFeedDto> {
  private final CarrierFeign carrierFeign;
  private final CarrierServicePersistenceService carrierServicePersistenceService;
  private final TypeReference<BatchRequest<CarrierFeedDto>> carrierTypeReference =
      new TypeReference<>() {};

  private static final Logger logger = LoggerFactory.getLogger(CarrierBatchServiceImpl.class);

  public static final CarrierBatchMapper INSTANCE = Mappers.getMapper(CarrierBatchMapper.class);

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.CARRIER_FEED;
  }

  @Override
  public TypeReference<BatchRequest<CarrierFeedDto>> getTypeReference() {
    return carrierTypeReference;
  }

  @Override
  public String createRecordImpl(CarrierFeedDto payload) {
    return carrierFeign.createCarrierService(INSTANCE.toCarrierRequest(payload)).getMessage();
  }

  @Override
  public String updateRecordImpl(CarrierFeedDto payload) {
    return carrierFeign
        .updateCarrierServiceDetails(
            payload.getCarrierId(),
            payload.getCarrierServiceId(),
            payload.getOrgId(),
            INSTANCE.toCarrierUpdateRequest(payload))
        .getMessage();
  }

  @Override
  public String deleteRecordImpl(CarrierFeedDto payload) {
    return carrierFeign
        .deleteCarrierService(
            payload.getCarrierId(), payload.getCarrierServiceId(), payload.getOrgId())
        .getMessage();
  }

  @Override
  public void checkForOutdatedRecord(BatchRequest<CarrierFeedDto> carrierBatchRequest)
      throws CommonServiceException {
    CarrierFeedDto carrierDto = carrierBatchRequest.getPayload();
    String carrierId = carrierDto.getCarrierId();
    String carrierServiceId = carrierDto.getCarrierServiceId();
    String orgId = carrierDto.getOrgId();
    if (Objects.nonNull(carrierId) && Objects.nonNull(orgId)) {
      try {
        Optional<CarrierServiceDomainDto> carrierServiceDomainDto =
            carrierServicePersistenceService.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
                carrierId, carrierServiceId, orgId);
        if (carrierServiceDomainDto.isPresent()
            && (carrierServiceDomainDto
                .get()
                .getLastModifiedDate()
                .after(carrierBatchRequest.getReceivedTimestamp()))) {
          Map<String, FieldError> errorMap = new HashMap<>();
          errorMap.put(
              "receivedTimestamp",
              FieldError.builder()
                  .rejectedValue(carrierBatchRequest.getReceivedTimestamp())
                  .build());
          errorMap.put(
              "lastUpdatedTimestamp",
              FieldError.builder()
                  .rejectedValue(carrierServiceDomainDto.get().getLastModifiedDate())
                  .build());
          throw new CommonServiceException(
              "Can't process the record as it's outdated",
              HttpStatus.BAD_REQUEST,
              0x1771,
              errorMap);
        }
      } catch (CarrierServiceDomainException e) {
        logger.debug(
            "Cannot check for outdated record as the given carrier does not exist for the details carrierId:{} carrierServiceId:{} orgId:{}",
            carrierId,
            carrierServiceId,
            orgId);
      }
    }
  }
}
