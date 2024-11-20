/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.calendar.consumer.dto.CarrierServiceCalendarFeedDto;
import com.nextuple.calendar.consumer.mapper.CarrierServiceCalendarBatchMapper;
import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.persistence.domain.CarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.service.CarrierServiceCalendarPersistenceService;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.service.BatchService;
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
public class CarrierServiceCalendarBatchServiceImpl
    extends BatchService<CarrierServiceCalendarFeedDto> {

  private final CalendarFeign calendarFeign;
  private final CarrierServiceCalendarPersistenceService carrierServiceCalendarPersistenceService;
  public static final CarrierServiceCalendarBatchMapper INSTANCE =
      Mappers.getMapper(CarrierServiceCalendarBatchMapper.class);
  private final TypeReference<BatchRequest<CarrierServiceCalendarFeedDto>>
      carrierServiceCalendarTypeReference = new TypeReference<>() {};

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.CARRIER_SERVICE_CALENDAR_FEED;
  }

  @Override
  public TypeReference<BatchRequest<CarrierServiceCalendarFeedDto>> getTypeReference() {
    return carrierServiceCalendarTypeReference;
  }

  @Override
  public String createRecordImpl(CarrierServiceCalendarFeedDto payload) {
    return calendarFeign
        .createCarrierServiceCalendar(INSTANCE.toCarrierServiceCalendarRequest(payload))
        .getMessage();
  }

  @Override
  public String updateRecordImpl(CarrierServiceCalendarFeedDto payload)
      throws CommonServiceException {
    handleInvalidAction(ActionEnum.UPDATE);
    return "";
  }

  @Override
  public String deleteRecordImpl(CarrierServiceCalendarFeedDto payload)
      throws CommonServiceException {
    handleInvalidAction(ActionEnum.DELETE);
    return "";
  }

  @Override
  public void checkForOutdatedRecord(
      BatchRequest<CarrierServiceCalendarFeedDto> carrierServiceCalendarBatchRequest)
      throws CommonServiceException {
    CarrierServiceCalendarFeedDto carrierServiceCalendarDto =
        carrierServiceCalendarBatchRequest.getPayload();
    String calendarId = carrierServiceCalendarDto.getCalendarId();
    String orgId = carrierServiceCalendarDto.getOrgId();
    String carrierServiceId = carrierServiceCalendarDto.getCarrierServiceId();
    String shippingStage = carrierServiceCalendarDto.getShippingStage();
    String effectiveDate = carrierServiceCalendarDto.getEffectiveDate();
    if (Objects.nonNull(calendarId) && Objects.nonNull(orgId)) {
      Optional<CarrierServiceCalendarDomainDto> carrierServiceCalendarDomainDto =
          carrierServiceCalendarPersistenceService
              .findByCalendarIdAndOrgIdAndCarrierServiceIdAndShippingStageAndEffectiveDate(
                  calendarId, orgId, carrierServiceId, shippingStage, effectiveDate);
      if (carrierServiceCalendarDomainDto.isPresent()
          && (carrierServiceCalendarDomainDto
              .get()
              .getLastModifiedDate()
              .after(carrierServiceCalendarBatchRequest.getReceivedTimestamp()))) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            "receivedTimestamp",
            FieldError.builder()
                .rejectedValue(carrierServiceCalendarBatchRequest.getReceivedTimestamp())
                .build());
        errorMap.put(
            "lastUpdatedTimestamp",
            FieldError.builder()
                .rejectedValue(carrierServiceCalendarDomainDto.get().getLastModifiedDate())
                .build());
        throw new CommonServiceException(
            "Can't process the record as it's outdated", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
      }
    }
  }
}
