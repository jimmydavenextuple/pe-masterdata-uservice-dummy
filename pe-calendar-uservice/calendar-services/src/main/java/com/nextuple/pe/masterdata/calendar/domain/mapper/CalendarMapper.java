/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.domain.mapper;

import com.nextuple.calendar.domain.dto.CarrierCalendarCacheKeyDto;
import com.nextuple.calendar.domain.dto.NodeCalendarCacheKeyDto;
import com.nextuple.calendar.domain.dto.NodeCarrierCalendarCacheKeyDto;
import com.nextuple.calendar.domain.inbound.CalendarRequest;
import com.nextuple.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.inbound.NodeCalendarRequest;
import com.nextuple.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.calendar.persistence.domain.CalendarDomainDto;
import com.nextuple.calendar.persistence.domain.CarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.NodeCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.NodeCarrierServiceCalendarDomainDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CalendarMapper {

  CalendarDomainDto convertToCalendarDomainDto(CalendarRequest calendarRequest);

  CalendarResponse convertToCalendarResponse(CalendarDomainDto calendarDomainDto);

  NodeCalendarDomainDto convertToNodeCalendarDomainDto(NodeCalendarRequest nodeCalendarRequest);

  NodeCalendarResponse convertToNodeCalendarResponse(NodeCalendarDomainDto nodeCalendarDomainDto);

  List<NodeCalendarResponse> convertToNodeCalendarResponseList(
      List<NodeCalendarDomainDto> nodeCalendarDomainDtoList);

  CarrierServiceCalendarDomainDto convertToCarrierServiceCalendarDomainDto(
      CarrierServiceCalendarRequest carrierServiceCalendarRequest);

  CarrierServiceCalendarResponse convertToCarrierServiceCalendarResponse(
      CarrierServiceCalendarDomainDto carrierServiceCalendarDomainDto);

  List<CarrierServiceCalendarResponse> convertToCarrierServiceCalendarResponseList(
      List<CarrierServiceCalendarDomainDto> carrierServiceCalendarDomainDtos);

  NodeCarrierServiceCalendarDomainDto convertToNodeCarrierServiceCalendarDomainDto(
      NodeCarrierServiceCalendarRequest nodeCarrierServiceCalendarRequest);

  NodeCarrierServiceCalendarResponse convertToNodeCarrierServiceCalendarResponse(
      NodeCarrierServiceCalendarDomainDto nodeCarrierServiceCalendarDomainDto);

  List<NodeCarrierServiceCalendarResponse> convertToNodeCarrierServiceCalendarResponseList(
      List<NodeCarrierServiceCalendarDomainDto> nodeCarrierServiceCalendarDomainDtoList);

  List<NodeCalendarCacheKeyDto> convertToNodeCalendarCacheKeyDtoList(
      List<NodeCalendarDomainDto> nodeCalendarDomainDtoList);

  List<CarrierCalendarCacheKeyDto> convertToCarrierCalendarCacheKeyDtoList(
      List<CarrierServiceCalendarDomainDto> carrierServiceCalendarDomainDtoList);

  List<NodeCarrierCalendarCacheKeyDto> convertToNodeCarrierCalendarCacheKeyDtoList(
      List<NodeCarrierServiceCalendarDomainDto> nodeCarrierServiceCalendarDomainDtoList);
}
