/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.calendar.consumer.dto.PickupCalendarFeedDto;
import com.nextuple.calendar.consumer.mapper.PickupCalendarBatchMapper;
import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.persistence.domain.NodeCarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.service.NodeCarrierServiceCalendarPersistenceService;
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
public class PickupCalendarBatchServiceImpl extends BatchService<PickupCalendarFeedDto> {

  private final CalendarFeign calendarFeign;
  private final NodeCarrierServiceCalendarPersistenceService
      nodeCarrierServiceCalendarPersistenceService;
  public static final PickupCalendarBatchMapper INSTANCE =
      Mappers.getMapper(PickupCalendarBatchMapper.class);
  private final TypeReference<BatchRequest<PickupCalendarFeedDto>> pickupCalendarTypeReference =
      new TypeReference<>() {};

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.PICKUP_CALENDAR_FEED;
  }

  @Override
  public TypeReference<BatchRequest<PickupCalendarFeedDto>> getTypeReference() {
    return pickupCalendarTypeReference;
  }

  @Override
  public String createRecordImpl(PickupCalendarFeedDto payload) {
    return calendarFeign
        .createNodeCarrierServiceCalendar(INSTANCE.toPickupCalendarRequest(payload))
        .getMessage();
  }

  @Override
  public String updateRecordImpl(PickupCalendarFeedDto payload) throws CommonServiceException {
    handleInvalidAction(ActionEnum.UPDATE);
    return "";
  }

  @Override
  public String deleteRecordImpl(PickupCalendarFeedDto payload) throws CommonServiceException {
    handleInvalidAction(ActionEnum.DELETE);
    return "";
  }

  @Override
  public void checkForOutdatedRecord(BatchRequest<PickupCalendarFeedDto> pickupCalendarBatchRequest)
      throws CommonServiceException {
    PickupCalendarFeedDto pickupCalendarDto = pickupCalendarBatchRequest.getPayload();
    String calendarId = pickupCalendarDto.getCalendarId();
    String nodeId = pickupCalendarDto.getNodeId();
    String orgId = pickupCalendarDto.getOrgId();
    String carrierServiceId = pickupCalendarDto.getCarrierServiceId();
    String effectiveDate = pickupCalendarDto.getEffectiveDate();
    if (Objects.nonNull(calendarId) && Objects.nonNull(orgId)) {
      Optional<NodeCarrierServiceCalendarDomainDto> pickupCalendarDomainDto =
          nodeCarrierServiceCalendarPersistenceService
              .findByCalendarIdAndOrgIdAndNodeIdAndCarrierServiceIdAndEffectiveDate(
                  calendarId, orgId, nodeId, carrierServiceId, effectiveDate);
      if (pickupCalendarDomainDto.isPresent()
          && (pickupCalendarDomainDto
              .get()
              .getLastModifiedDate()
              .after(pickupCalendarBatchRequest.getReceivedTimestamp()))) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            "receivedTimestamp",
            FieldError.builder()
                .rejectedValue(pickupCalendarBatchRequest.getReceivedTimestamp())
                .build());
        errorMap.put(
            "lastUpdatedTimestamp",
            FieldError.builder()
                .rejectedValue(pickupCalendarDomainDto.get().getLastModifiedDate())
                .build());
        throw new CommonServiceException(
            "Can't process the record as it's outdated", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
      }
    }
  }
}
