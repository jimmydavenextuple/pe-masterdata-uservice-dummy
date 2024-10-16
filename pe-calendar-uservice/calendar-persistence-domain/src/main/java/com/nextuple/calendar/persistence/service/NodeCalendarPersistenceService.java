/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.service;

import com.nextuple.calendar.persistence.domain.NodeCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.key.NodeCalendarDomainKey;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.common.service.DomainPersistenceService;
import java.util.List;
import java.util.Optional;

public interface NodeCalendarPersistenceService
    extends DomainPersistenceService<NodeCalendarDomainDto, NodeCalendarDomainKey> {
  NodeCalendarDomainDto saveNodeCalendar(NodeCalendarDomainDto nodeCalendarDomainDto)
      throws CalendarDomainException;

  List<NodeCalendarDomainDto> getNodeCalendar(String orgId, String nodeId)
      throws CalendarDomainException;

  List<NodeCalendarDomainDto> getAllNodeCalendar(Integer limit) throws CalendarDomainException;

  List<NodeCalendarDomainDto> getNodeServiceCalendarByOrgIdAndCalendarId(
      String calendarId, String orgId) throws CalendarDomainException;

  Optional<NodeCalendarDomainDto> findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(
      String calendarId, String nodeId, String orgId, String effectiveDate);
}
