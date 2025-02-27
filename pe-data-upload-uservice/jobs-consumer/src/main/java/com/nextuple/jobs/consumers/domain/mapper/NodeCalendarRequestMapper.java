/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.domain.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextuple.calendar.domain.inbound.CalendarRequest;
import com.nextuple.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.inbound.NodeCalendarRequest;
import com.nextuple.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.pojo.ExceptionDays;
import com.nextuple.jobs.framework.common.domain.pojo.CalendarDataUpload;
import com.nextuple.jobs.framework.common.domain.pojo.CarrierCalendarUpload;
import com.nextuple.jobs.framework.common.domain.pojo.NodeCalendarUpload;
import com.nextuple.jobs.framework.common.domain.pojo.PickUpCalendarUpload;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.util.StringUtils;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodeCalendarRequestMapper {

  NodeCalendarRequest convertToNodeCalendarRequest(NodeCalendarUpload nodeCalendarUpload);

  CarrierServiceCalendarRequest convertToCarrierServiceCalendarRequest(
      CarrierCalendarUpload carrierCalendarUpload);

  @Mapping(target = "exceptionDays", ignore = true)
  CalendarRequest convertToCalendarRequest(CalendarDataUpload calendarDataUpload)
      throws JsonProcessingException;

  @AfterMapping
  default void convertExceptionDays(
          CalendarDataUpload source, @MappingTarget CalendarRequest.CalendarRequestBuilder<?, ?> target)
      throws JsonProcessingException {
    var mapper = new ObjectMapper();
    target.exceptionDays(
        StringUtils.hasLength(source.getExceptionDays())
            ? mapper.readValue(
                source.getExceptionDays(), new TypeReference<List<ExceptionDays>>() {})
            : null);
  }

  NodeCarrierServiceCalendarRequest convertToNodeCarrierServiceCalendarRequest(
      PickUpCalendarUpload pickUpCalendarUpload);
}
