/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.base.PagePayload;
import com.nextuple.dataupload.common.utils.DataUploadUtil;
import com.nextuple.dataupload.domain.dto.CarrierTransitDto;
import com.nextuple.dataupload.domain.mapper.CarrierTransitTimeMapper;
import com.nextuple.dataupload.domain.pojo.CarrierServiceCalendar;
import com.nextuple.postgres.config.ReaderDS;
import com.nextuple.transit.domain.dto.TransitTimeEntriesDto;
import com.nextuple.transit.domain.feign.TransitFeign;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarrierTransitTimeService {

  private final CarrierFeign carrierFeign;

  private final CalendarFeign calendarFeign;

  private final TransitFeign transitFeign;

  public static final CarrierTransitTimeMapper INSTANCE =
      Mappers.getMapper(CarrierTransitTimeMapper.class);

  @ReaderDS
  public PagePayload<CarrierTransitDto> getCarrierTransitTimeList(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {
    PagePayload<CarrierTransitDto> carrierTransitDtoPagePayload = new PagePayload<>();
    List<CarrierTransitDto> responseList = new ArrayList<>();

    PagePayload<CarrierServiceResponse> carrierResponse =
        carrierFeign
            .getCarrierServiceListWithPagination(orgId, pageNo, pageSize, sortBy, sortOrder)
            .getPayload();

    List<CarrierServiceResponse> carrierServiceResponseList = carrierResponse.getData();
    for (CarrierServiceResponse carrierServiceResponse : carrierServiceResponseList) {
      List<CarrierServiceCalendarResponse> carrierServiceCalendarResponseList = new ArrayList<>();
      try {
        carrierServiceCalendarResponseList =
            calendarFeign
                .getCarrierServiceCalendar(
                    carrierServiceResponse.getOrgId(), carrierServiceResponse.getCarrierServiceId())
                .getPayload();
      } catch (RuntimeException e) {
        log.error("Empty Carrier Service Calendar Response List");
      }

      TransitTimeEntriesDto transitTimeEntries =
          transitFeign
              .getTransitTimeEntries(
                  carrierServiceResponse.getOrgId(), carrierServiceResponse.getCarrierServiceId())
              .getPayload();
      Optional<CarrierServiceCalendarResponse> carrierServiceCalendarResponse =
          DataUploadUtil.getActiveCalendarResponse(carrierServiceCalendarResponseList);
      CalendarResponse calendarResponse = null;
      String effectiveDate = null;
      if (carrierServiceCalendarResponse.isPresent()) {
        effectiveDate = carrierServiceCalendarResponse.get().getEffectiveDate();
        try {
          calendarResponse =
              calendarFeign
                  .getCalendar(orgId, carrierServiceCalendarResponse.get().getCalendarId())
                  .getPayload();
        } catch (Exception e) {
          log.error(
              "Active calendar does not exist for carrier service id {} and orgId {} ",
              carrierServiceResponse.getCarrierServiceId(),
              carrierServiceResponse.getOrgId());
        }
      }
      responseList.add(
          setCarrierTransitDto(
              carrierServiceResponse, calendarResponse, effectiveDate, transitTimeEntries));
    }
    carrierTransitDtoPagePayload.setPagination(carrierResponse.getPagination());
    carrierTransitDtoPagePayload.setData(responseList);
    return carrierTransitDtoPagePayload;
  }

  private CarrierTransitDto setCarrierTransitDto(
      CarrierServiceResponse carrierServiceResponse,
      CalendarResponse calendarResponse,
      String effectiveDate,
      TransitTimeEntriesDto transitTimeEntries) {
    CarrierServiceCalendar carrierServiceCalendar = null;
    if (ObjectUtils.isNotEmpty(calendarResponse)) {
      carrierServiceCalendar = INSTANCE.toCalendarService(calendarResponse);

      carrierServiceCalendar.setEffectiveDate(effectiveDate);
    }
    return CarrierTransitDto.builder()
        .orgId(carrierServiceResponse.getOrgId())
        .carrierId(carrierServiceResponse.getCarrierId())
        .carrierServiceId(carrierServiceResponse.getCarrierServiceId())
        .carrierName(carrierServiceResponse.getCarrierName())
        .serviceName(carrierServiceResponse.getServiceName())
        .isCarrierActive(
            ObjectUtils.isNotEmpty(calendarResponse) && transitTimeEntries.getTotalRecords() > 0)
        .isCalendarAssigned(ObjectUtils.isNotEmpty(calendarResponse))
        .carrierServiceCalendar(carrierServiceCalendar)
        .build();
  }
}
