/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.calendar.consumer.dto.CalendarFeedDto;
import com.nextuple.calendar.consumer.mapper.CalendarBatchMapper;
import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.persistence.domain.CalendarDomainDto;
import com.nextuple.calendar.persistence.service.CalendarPersistenceService;
import com.nextuple.common.enums.ActionEnum;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarBatchServiceImpl extends BatchService<CalendarFeedDto> {

  private final CalendarFeign calendarFeign;
  private final CalendarPersistenceService calendarPersistenceService;
  public static final CalendarBatchMapper INSTANCE = Mappers.getMapper(CalendarBatchMapper.class);
  private final TypeReference<BatchRequest<CalendarFeedDto>> calendarTypeReference =
      new TypeReference<>() {};

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.CALENDAR_FEED;
  }

  @Override
  public TypeReference<BatchRequest<CalendarFeedDto>> getTypeReference() {
    return calendarTypeReference;
  }

  @Override
  public String createRecordImpl(CalendarFeedDto payload) {
    return calendarFeign.createCalendar(INSTANCE.toCalendarRequest(payload)).getMessage();
  }

  @Override
  public String updateRecordImpl(CalendarFeedDto payload) throws CommonServiceException {
    handleInvalidAction(ActionEnum.UPDATE);
    return "";
  }

  @Override
  public String deleteRecordImpl(CalendarFeedDto payload) throws CommonServiceException {
    handleInvalidAction(ActionEnum.DELETE);
    return "";
  }

  @Override
  public void checkForOutdatedRecord(BatchRequest<CalendarFeedDto> calendarBatchRequest)
      throws CommonServiceException {
    CalendarFeedDto calendarDto = calendarBatchRequest.getPayload();
    String calendarId = calendarDto.getCalendarId();
    String orgId = calendarDto.getOrgId();
    if (Objects.nonNull(calendarId) && Objects.nonNull(orgId)) {
      Optional<CalendarDomainDto> calendarDomainDto =
          calendarPersistenceService.findCalendarDetailsByCalendarIdAndOrgId(calendarId, orgId);
      if (calendarDomainDto.isPresent()
          && (calendarDomainDto
              .get()
              .getLastModifiedDate()
              .after(calendarBatchRequest.getReceivedTimestamp()))) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            "receivedTimestamp",
            FieldError.builder()
                .rejectedValue(calendarBatchRequest.getReceivedTimestamp())
                .build());
        errorMap.put(
            "lastUpdatedTimestamp",
            FieldError.builder()
                .rejectedValue(calendarDomainDto.get().getLastModifiedDate())
                .build());
        throw new CommonServiceException(
            "Can't process the record as it's outdated", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
      }
    }
  }
}
