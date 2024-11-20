/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.calendar.consumer.dto.NodeCalendarFeedDto;
import com.nextuple.calendar.consumer.mapper.NodeCalendarBatchMapper;
import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.persistence.domain.NodeCalendarDomainDto;
import com.nextuple.calendar.persistence.service.NodeCalendarPersistenceService;
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
public class NodeCalendarBatchServiceImpl extends BatchService<NodeCalendarFeedDto> {

  private final CalendarFeign calendarFeign;
  private final NodeCalendarPersistenceService nodeCalendarPersistenceService;
  public static final NodeCalendarBatchMapper INSTANCE =
      Mappers.getMapper(NodeCalendarBatchMapper.class);
  private final TypeReference<BatchRequest<NodeCalendarFeedDto>> nodeCalendarTypeReference =
      new TypeReference<>() {};

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.NODE_CALENDAR_FEED;
  }

  @Override
  public TypeReference<BatchRequest<NodeCalendarFeedDto>> getTypeReference() {
    return nodeCalendarTypeReference;
  }

  @Override
  public String createRecordImpl(NodeCalendarFeedDto payload) {
    return calendarFeign.createNodeCalendar(INSTANCE.toNodeCalendarRequest(payload)).getMessage();
  }

  @Override
  public String updateRecordImpl(NodeCalendarFeedDto payload) throws CommonServiceException {
    handleInvalidAction(ActionEnum.UPDATE);
    return "";
  }

  @Override
  public String deleteRecordImpl(NodeCalendarFeedDto payload) throws CommonServiceException {
    handleInvalidAction(ActionEnum.DELETE);
    return "";
  }

  @Override
  public void checkForOutdatedRecord(BatchRequest<NodeCalendarFeedDto> nodeCalendarBatchRequest)
      throws CommonServiceException {
    NodeCalendarFeedDto nodeCalendarDto = nodeCalendarBatchRequest.getPayload();
    String calendarId = nodeCalendarDto.getCalendarId();
    String nodeId = nodeCalendarDto.getNodeId();
    String orgId = nodeCalendarDto.getOrgId();
    String effectiveDate = nodeCalendarDto.getEffectiveDate();
    if (Objects.nonNull(calendarId) && Objects.nonNull(orgId)) {
      Optional<NodeCalendarDomainDto> nodeCalendarDomainDto =
          nodeCalendarPersistenceService.findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(
              calendarId, nodeId, orgId, effectiveDate);
      if (nodeCalendarDomainDto.isPresent()
          && (nodeCalendarDomainDto
              .get()
              .getLastModifiedDate()
              .after(nodeCalendarBatchRequest.getReceivedTimestamp()))) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            "receivedTimestamp",
            FieldError.builder()
                .rejectedValue(nodeCalendarBatchRequest.getReceivedTimestamp())
                .build());
        errorMap.put(
            "lastUpdatedTimestamp",
            FieldError.builder()
                .rejectedValue(nodeCalendarDomainDto.get().getLastModifiedDate())
                .build());
        throw new CommonServiceException(
            "Can't process the record as it's outdated", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
      }
    }
  }
}
