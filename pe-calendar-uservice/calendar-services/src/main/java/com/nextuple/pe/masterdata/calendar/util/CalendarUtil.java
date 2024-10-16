/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.util;

import static com.nextuple.common.constants.CommonConstants.ORG_ID;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.calendar.domain.pojo.ExceptionDays;
import com.nextuple.calendar.persistence.domain.CalendarDomainDto;
import com.nextuple.calendar.persistence.domain.CarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.NodeCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.NodeCarrierServiceCalendarDomainDto;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

public class CalendarUtil {
  private CalendarUtil() {}

  public static List<CalendarDaysStatusInfo> getListOfCalendarDaysStatusInfos(
      Optional<String> fromDate, int numDays) {
    List<CalendarDaysStatusInfo> calendarDaysStatusInfoList = new ArrayList<>();
    for (var i = -1; i < numDays; i++) {
      calendarDaysStatusInfoList.add(
          CalendarDaysStatusInfo.builder()
              .date(DateUtil.addDaysToGivenDate(fromDate, i))
              .isActive(Boolean.TRUE)
              .build());
    }
    return calendarDaysStatusInfoList;
  }

  public static Map<String, String> getNextNodeCalendarIds(
      String startDate, String endDate, List<NodeCalendarDomainDto> nodeCalendarDomainDtoList) {
    return nodeCalendarDomainDtoList.stream()
        .filter(
            x ->
                x.getEffectiveDate().compareTo(startDate) > 0
                    && x.getEffectiveDate().compareTo(endDate) <= 0)
        .collect(
            Collectors.toMap(
                NodeCalendarDomainDto::getEffectiveDate, NodeCalendarDomainDto::getCalendarId));
  }

  public static Map<String, String> getNextCarrierCalendarIds(
      String startDate,
      String endDate,
      List<CarrierServiceCalendarDomainDto> carrierServiceCalendarDomainDtoList) {
    return carrierServiceCalendarDomainDtoList.stream()
        .filter(
            x ->
                x.getEffectiveDate().compareTo(startDate) > 0
                    && x.getEffectiveDate().compareTo(endDate) <= 0)
        .collect(
            Collectors.toMap(
                CarrierServiceCalendarDomainDto::getEffectiveDate,
                CarrierServiceCalendarDomainDto::getCalendarId));
  }

  public static Map<String, String> getNextNodeCarrierCalendarIds(
      String startDate,
      String endDate,
      List<NodeCarrierServiceCalendarDomainDto> nodeCarrierServiceCalendarDomainDtoList) {
    return nodeCarrierServiceCalendarDomainDtoList.stream()
        .filter(
            x ->
                x.getEffectiveDate().compareTo(startDate) > 0
                    && x.getEffectiveDate().compareTo(endDate) <= 0)
        .collect(
            Collectors.toMap(
                NodeCarrierServiceCalendarDomainDto::getEffectiveDate,
                NodeCarrierServiceCalendarDomainDto::getCalendarId));
  }

  public static void checkCalendarDomainDtoIsEmpty(
      String orgId, String currentCalendarId, CalendarDomainDto calendarDomainDto)
      throws CommonServiceException {
    if (ObjectUtils.isEmpty(calendarDomainDto)) {
      throw new CommonServiceException(
          "No Calendar with calendarId = "
              + currentCalendarId
              + " found that exist with the association calendar ",
          HttpStatus.BAD_REQUEST,
          0xfffff5,
          Map.of(ORG_ID, FieldError.builder().rejectedValue(orgId).build()));
    }
  }

  public static List<String> getExceptionDaysForCalendar(List<ExceptionDays> exceptionDays) {
    return CollectionUtils.isEmpty(exceptionDays)
        ? new ArrayList<>()
        : exceptionDays.stream().map(ExceptionDays::getDate).collect(Collectors.toList());
  }
}
