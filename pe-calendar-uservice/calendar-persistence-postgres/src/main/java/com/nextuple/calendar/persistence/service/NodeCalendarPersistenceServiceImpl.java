/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.service;

import com.nextuple.calendar.persistence.domain.NodeCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.key.NodeCalendarDomainKey;
import com.nextuple.calendar.persistence.entity.NodeCalendarEntity;
import com.nextuple.calendar.persistence.entity.key.NodeCalendarKey;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.mapper.NodeCalendarEntityMapper;
import com.nextuple.calendar.persistence.repository.NodeCalendarRepository;
import com.nextuple.postgres.service.CommonPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NodeCalendarPersistenceServiceImpl
    extends CommonPersistenceService<
        NodeCalendarDomainDto,
        NodeCalendarDomainKey,
        NodeCalendarEntity,
        NodeCalendarKey,
        NodeCalendarRepository,
        NodeCalendarEntityMapper>
    implements NodeCalendarPersistenceService {

  @Override
  public NodeCalendarDomainDto saveNodeCalendar(NodeCalendarDomainDto nodeCalendarDomainDto)
      throws CalendarDomainException {
    log.debug("Inside saveNodeCalendarEntity() {}", nodeCalendarDomainDto);
    try {
      return save(nodeCalendarDomainDto);
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to create node calendar",
          e,
          nodeCalendarDomainDto.getCalendarId(),
          nodeCalendarDomainDto.getOrgId(),
          nodeCalendarDomainDto.getNodeId(),
          null);
    }
  }

  @Override
  public List<NodeCalendarDomainDto> getNodeCalendar(String orgId, String nodeId)
      throws CalendarDomainException {
    log.debug("Inside getNodeCalendarByNodeId() for org {} and nodeId {} ", orgId, nodeId);
    try {
      return getMapper().toDomain(getRepository().findByOrgIdAndNodeId(orgId, nodeId));
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch node calendar", e, null, orgId, nodeId, null);
    }
  }

  @Override
  public List<NodeCalendarDomainDto> getAllNodeCalendar(Integer limit)
      throws CalendarDomainException {
    try {
      return getMapper().toDomain(getRepository().findAllNodeCalendarByLimit(limit));
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch all node calendars", e, null, null, null, null);
    }
  }

  @Override
  public List<NodeCalendarDomainDto> getNodeServiceCalendarByOrgIdAndCalendarId(
      String calendarId, String orgId) throws CalendarDomainException {
    try {
      return getMapper()
          .toDomain(getRepository().findNodeCalendarByCalendarIdAndOrgId(calendarId, orgId));
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch node calendar list", e, calendarId, orgId, null, null);
    }
  }

  @Override
  public Optional<NodeCalendarDomainDto> findByCalendarIdAndNodeIdAndOrgIdAndEffectiveDate(
      String calendarId, String nodeId, String orgId, String effectiveDate) {
    return findByKey(
        NodeCalendarDomainKey.builder()
            .orgId(orgId)
            .nodeId(nodeId)
            .calendarId(calendarId)
            .effectiveDate(effectiveDate)
            .build());
  }
}
