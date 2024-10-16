/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.service;

import com.nextuple.calendar.persistence.domain.CarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.key.CarrierServiceCalendarDomainKey;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.common.service.DomainPersistenceService;
import java.util.List;
import java.util.Optional;

public interface CarrierServiceCalendarPersistenceService
    extends DomainPersistenceService<
        CarrierServiceCalendarDomainDto, CarrierServiceCalendarDomainKey> {
  CarrierServiceCalendarDomainDto saveCarrierServiceCalendar(
      CarrierServiceCalendarDomainDto carrierServiceCalendarDomainDto)
      throws CalendarDomainException;

  List<CarrierServiceCalendarDomainDto> getCarrierServiceCalendar(
      String orgId, String carrierServiceId, String shippingStage) throws CalendarDomainException;

  List<CarrierServiceCalendarDomainDto> getCarrierServiceCalendar(
      String orgId, String carrierServiceId, String carrierServiceOption, String shippingStage)
      throws CalendarDomainException;

  List<CarrierServiceCalendarDomainDto> getAllCarrierServiceCalendars(Integer limit)
      throws CalendarDomainException;

  List<CarrierServiceCalendarDomainDto> getCarrierServiceCalendarByOrgIdAndCalendarId(
      String calendarId, String orgId) throws CalendarDomainException;

  Optional<CarrierServiceCalendarDomainDto>
      findByCalendarIdAndOrgIdAndCarrierServiceIdAndShippingStageAndEffectiveDate(
          String calendarId,
          String orgId,
          String carrierServiceId,
          String shippingStage,
          String effectiveDate);
}
