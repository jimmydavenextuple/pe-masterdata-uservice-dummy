/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.service;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.nextuple.calendar.persistence.domain.CalendarDomainDto;
import com.nextuple.calendar.persistence.domain.key.CalendarDomainKey;
import com.nextuple.calendar.persistence.entity.CalendarEntity;
import com.nextuple.calendar.persistence.entity.key.CalendarKey;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.mapper.CalendarEntityMapper;
import com.nextuple.calendar.persistence.repository.CalendarRepository;
import com.nextuple.postgres.service.CommonPersistenceService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarPersistenceServiceImpl
    extends CommonPersistenceService<
        CalendarDomainDto,
        CalendarDomainKey,
        CalendarEntity,
        CalendarKey,
        CalendarRepository,
        CalendarEntityMapper>
    implements CalendarPersistenceService {

  @Override
  public CalendarDomainDto saveCalendar(CalendarDomainDto calendarDomainDto)
      throws CalendarDomainException {
    log.debug("Inside saveCalendarEntity()");
    try {
      return save(calendarDomainDto);
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to create calendar",
          e,
          calendarDomainDto.getCalendarId(),
          calendarDomainDto.getOrgId(),
          null,
          null);
    }
  }

  @Override
  public CalendarDomainDto getCalendar(String orgId, String calendarId)
      throws CalendarDomainException {
    log.debug("Inside getCalendar()");
    try {
      return getMapper().toDomain(getRepository().findByCalendarIdAndOrgId(calendarId, orgId));
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch calendar", e, calendarId, orgId, null, null);
    }
  }

  @Override
  public Optional<CalendarDomainDto> findCalendarDetailsByCalendarIdAndOrgId(
      String calendarId, String orgId) {
    return findByKey(CalendarDomainKey.builder().orgId(orgId).calendarId(calendarId).build());
  }

  @Override
  public Page<CalendarDomainDto> findCalendarListByOrgId(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws CalendarDomainException {
    try {
      Pageable pageable = null;
      if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending());
      } else {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());
      }
      return getRepository().findAllCalendarsByOrgId(orgId, pageable).map(getMapper()::toDomain);
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch calendar list", e, null, orgId, null, null);
    }
  }
}
