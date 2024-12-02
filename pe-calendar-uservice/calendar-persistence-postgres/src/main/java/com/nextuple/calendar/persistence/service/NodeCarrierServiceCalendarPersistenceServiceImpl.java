/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.service;

import com.nextuple.calendar.persistence.domain.NodeCarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.domain.key.NodeCarrierServiceCalendarDomainKey;
import com.nextuple.calendar.persistence.entity.NodeCarrierServiceCalendarEntity;
import com.nextuple.calendar.persistence.entity.key.NodeCarrierServiceCalendarKey;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.mapper.NodeCarrierServiceCalendarEntityMapper;
import com.nextuple.calendar.persistence.repository.NodeCarrierServiceCalendarRepository;
import com.nextuple.postgres.service.CommonPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NodeCarrierServiceCalendarPersistenceServiceImpl
    extends CommonPersistenceService<
        NodeCarrierServiceCalendarDomainDto,
        NodeCarrierServiceCalendarDomainKey,
        NodeCarrierServiceCalendarEntity,
        NodeCarrierServiceCalendarKey,
        NodeCarrierServiceCalendarRepository,
        NodeCarrierServiceCalendarEntityMapper>
    implements NodeCarrierServiceCalendarPersistenceService {

  @Override
  public NodeCarrierServiceCalendarDomainDto saveNodeCarrierServiceCalendar(
      NodeCarrierServiceCalendarDomainDto nodeCarrierServiceCalendarDomainDto)
      throws CalendarDomainException {
    log.debug("Inside saveCarrierServiceEntity() {}", nodeCarrierServiceCalendarDomainDto);
    try {
      return save(nodeCarrierServiceCalendarDomainDto);
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to create node carrier service calendar",
          e,
          nodeCarrierServiceCalendarDomainDto.getCalendarId(),
          nodeCarrierServiceCalendarDomainDto.getOrgId(),
          nodeCarrierServiceCalendarDomainDto.getNodeId(),
          nodeCarrierServiceCalendarDomainDto.getCarrierServiceId());
    }
  }

  @Override
  public List<NodeCarrierServiceCalendarDomainDto> getNodeCarrierServiceCalendar(
      String orgId, String nodeId, String carrierServiceId) throws CalendarDomainException {
    try {
      return getMapper()
          .toDomain(
              getRepository().findAllNodeCarrierServiceCalendar(orgId, nodeId, carrierServiceId));
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch node carrier service calendar",
          e,
          null,
          orgId,
          nodeId,
          carrierServiceId);
    }
  }

  @Override
  public List<NodeCarrierServiceCalendarDomainDto> getNodeCarrierServiceCalendar(
      String orgId, String nodeId, String carrierServiceId, String carrierServiceOption)
      throws CalendarDomainException {
    try {
      return getMapper()
          .toDomain(
              getRepository()
                  .findNodeCarrierServiceCalendar(
                      orgId, nodeId, carrierServiceId, carrierServiceOption));
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch node carrier service calendar",
          e,
          null,
          orgId,
          nodeId,
          carrierServiceId);
    }
  }

  @Override
  public List<NodeCarrierServiceCalendarDomainDto> getNodeCarrierServiceCalendar(
      String orgId, String nodeId) throws CalendarDomainException {
    try {
      return getMapper()
          .toDomain(
              getRepository()
                  .findNodeCarrierServiceCalendarByOrgIdAndNodeIdForDistCarrierServiceId(
                      orgId, nodeId));
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch node carrier service calendars", e, null, orgId, nodeId, null);
    }
  }

  @Override
  public List<NodeCarrierServiceCalendarDomainDto> getAllNodeCarrierServiceCalendars(Integer limit)
      throws CalendarDomainException {
    try {
      return getMapper().toDomain(getRepository().findAllNodeCarrierServiceCalendars(limit));
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch all node carrier service calendars", e, null, null, null, null);
    }
  }

  @Override
  public List<NodeCarrierServiceCalendarDomainDto> getAllNodeCarrierServiceCalendarsByOrgId(
      String orgId) throws CalendarDomainException {
    try {
      return getMapper().toDomain(getRepository().findAllNodeCarrierServiceCalendarsByOrgId(orgId));
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch all node carrier service calendars", e, null, null, null, null);
    }
  }

  @Override
  public Optional<NodeCarrierServiceCalendarDomainDto>
      findByCalendarIdAndOrgIdAndNodeIdAndCarrierServiceIdAndEffectiveDate(
          String calendarId,
          String orgId,
          String nodeId,
          String carrierServiceId,
          String effectiveDate) {
    return findByKey(
        NodeCarrierServiceCalendarDomainKey.builder()
            .orgId(orgId)
            .nodeId(nodeId)
            .carrierServiceId(carrierServiceId)
            .calendarId(calendarId)
            .effectiveDate(effectiveDate)
            .build());
  }
}
