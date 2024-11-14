/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.service;

import com.nextuple.calendar.persistence.domain.CarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.key.CarrierServiceCalendarDomainKey;
import com.nextuple.calendar.persistence.entity.CarrierServiceCalendarEntity;
import com.nextuple.calendar.persistence.entity.key.CarrierServiceCalendarKey;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.mapper.CarrierServiceCalendarEntityMapper;
import com.nextuple.calendar.persistence.repository.CarrierServiceCalendarRepository;
import com.nextuple.postgres.service.CommonPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarrierServiceCalendarPersistenceServiceImpl
    extends CommonPersistenceService<
        CarrierServiceCalendarDomainDto,
        CarrierServiceCalendarDomainKey,
        CarrierServiceCalendarEntity,
        CarrierServiceCalendarKey,
        CarrierServiceCalendarRepository,
        CarrierServiceCalendarEntityMapper>
    implements CarrierServiceCalendarPersistenceService {

  @Override
  public CarrierServiceCalendarDomainDto saveCarrierServiceCalendar(
      CarrierServiceCalendarDomainDto carrierServiceCalendarDomainDto)
      throws CalendarDomainException {
    log.debug("Inside saveCarrierServiceEntity() {}", carrierServiceCalendarDomainDto);
    try {
      return save(carrierServiceCalendarDomainDto);
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to create carrier service calendar",
          e,
          carrierServiceCalendarDomainDto.getCalendarId(),
          carrierServiceCalendarDomainDto.getOrgId(),
          null,
          carrierServiceCalendarDomainDto.getCarrierServiceId());
    }
  }

  @Override
  public List<CarrierServiceCalendarDomainDto> getCarrierServiceCalendar(
      String orgId, String carrierServiceId, String shippingStage) throws CalendarDomainException {
    try {
      return getMapper()
          .toDomain(
              getRepository()
                  .findAllCarrierServiceCalendar(orgId, carrierServiceId, shippingStage));

    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch carrier service calendar", e, null, orgId, null, carrierServiceId);
    }
  }

  @Override
  public List<CarrierServiceCalendarDomainDto> getCarrierServiceCalendar(
      String orgId, String carrierServiceId, String carrierServiceOption, String shippingStage)
      throws CalendarDomainException {
    try {
      return getMapper()
          .toDomain(
              getRepository()
                  .findCarrierServiceCalendar(
                      orgId, carrierServiceId, carrierServiceOption, shippingStage));
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch carrier service calendar", e, null, orgId, null, carrierServiceId);
    }
  }

  @Override
  public List<CarrierServiceCalendarDomainDto> getAllCarrierServiceCalendars(Integer limit)
      throws CalendarDomainException {
    try {
      return getMapper().toDomain(getRepository().findAllCarrierServiceCalendarsByLimit(limit));
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch all carrier calendars", e, null, null, null, null);
    }
  }

  @Override
  public List<CarrierServiceCalendarDomainDto> getCarrierServiceCalendarByOrgIdAndCalendarId(
      String calendarId, String orgId) throws CalendarDomainException {
    try {
      return getMapper()
          .toDomain(
              getRepository().findCarrierServiceCalendarByCalendarIdAndOrgId(calendarId, orgId));
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch carrier service calendar list", e, calendarId, orgId, null, null);
    }
  }

  @Override
  public Optional<CarrierServiceCalendarDomainDto>
      findByCalendarIdAndOrgIdAndCarrierServiceIdAndShippingStageAndEffectiveDate(
          String calendarId,
          String orgId,
          String carrierServiceId,
          String shippingStage,
          String effectiveDate) {

    return findByKey(
        CarrierServiceCalendarDomainKey.builder()
            .calendarId(calendarId)
            .orgId(orgId)
            .carrierServiceId(carrierServiceId)
            .shippingStage(shippingStage)
            .effectiveDate(effectiveDate)
            .build());
  }
}
