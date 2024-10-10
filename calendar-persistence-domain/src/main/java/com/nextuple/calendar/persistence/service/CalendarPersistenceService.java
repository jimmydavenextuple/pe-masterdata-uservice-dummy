/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.service;

import com.nextuple.calendar.persistence.domain.CalendarDomainDto;
import com.nextuple.calendar.persistence.domain.key.CalendarDomainKey;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.common.service.DomainPersistenceService;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface CalendarPersistenceService
    extends DomainPersistenceService<CalendarDomainDto, CalendarDomainKey> {
  CalendarDomainDto saveCalendar(CalendarDomainDto calendarDomainDto)
      throws CalendarDomainException;

  CalendarDomainDto getCalendar(String orgId, String calendarId) throws CalendarDomainException;

  Optional<CalendarDomainDto> findCalendarDetailsByCalendarIdAndOrgId(
      String calendarId, String orgId);

  Page<CalendarDomainDto> findCalendarListByOrgId(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws CalendarDomainException;
}
