/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service.impl;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.postgres.service.CommonPersistenceService;
import com.nextuple.transit.persistence.domain.TransitBufferV2DomainDto;
import com.nextuple.transit.persistence.domain.key.TransitBufferV2DomainKey;
import com.nextuple.transit.persistence.entity.TransitBufferV2Entity;
import com.nextuple.transit.persistence.entity.key.TransitBufferV2Key;
import com.nextuple.transit.persistence.mapper.TransitBufferV2EntityMapper;
import com.nextuple.transit.persistence.repository.TransitBufferV2Repository;
import com.nextuple.transit.persistence.service.TransitBufferV2PersistenceService;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransitBufferV2PersistenceServiceImpl
    extends CommonPersistenceService<
        TransitBufferV2DomainDto,
        TransitBufferV2DomainKey,
        TransitBufferV2Entity,
        TransitBufferV2Key,
        TransitBufferV2Repository,
        TransitBufferV2EntityMapper>
    implements TransitBufferV2PersistenceService {
  private static final String ERROR_WHILE_FETCHING_TRANSIT_BUFFERS =
      "Error while fetching transit buffers";
  private static final String ERROR_WHILE_UPDATING_TRANSIT_BUFFERS =
      "Error while updating transit buffers";
  private static final String ERROR_WHILE_DELETING = "Error while deleting transit buffer";
  private static final String ERROR_WHILE_CREATING = "Error while creating transit buffer";

  @Override
  public List<TransitBufferV2DomainDto> fetchTransitBuffersByOrgIdAndDestinationGeozone(
      String orgId,
      String destinationGeozone,
      LocalDate requestDate,
      LocalDate requestDatePlusHorizon)
      throws CommonServiceException {
    try {
      List<TransitBufferV2Entity> applicableBuffersList =
          getRepository()
              .findApplicableBuffers(
                  orgId, destinationGeozone, requestDate, requestDatePlusHorizon);
      return getMapper().toDomain(applicableBuffersList);
    } catch (Exception e) {
      log.error(ERROR_WHILE_FETCHING_TRANSIT_BUFFERS + ", {}", String.valueOf(e));
      throw new CommonServiceException(
          ERROR_WHILE_FETCHING_TRANSIT_BUFFERS, HttpStatus.INTERNAL_SERVER_ERROR, 0x1777, null);
    }
  }

  @Override
  public List<TransitBufferV2DomainDto>
      fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
          String orgId, String destinationGeozone, String sourceGeozone, String carrierServiceId)
          throws CommonServiceException {
    try {
      List<TransitBufferV2Entity> transitEntities =
          getRepository()
              .findByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
                  orgId, destinationGeozone, sourceGeozone, carrierServiceId);
      return getMapper().toDomain(transitEntities);
    } catch (Exception e) {
      log.error(ERROR_WHILE_FETCHING_TRANSIT_BUFFERS + ", {}", String.valueOf(e));
      throw new CommonServiceException(
          ERROR_WHILE_FETCHING_TRANSIT_BUFFERS, HttpStatus.INTERNAL_SERVER_ERROR, 0x1777, null);
    }
  }

  @Override
  public Optional<TransitBufferV2DomainDto>
      fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndTransitBufferConfigRequestId(
          String orgId,
          String destinationGeozone,
          String sourceGeozone,
          String carrierServiceId,
          Long transitBufferConfigRequestId)
          throws CommonServiceException {
    try {
      return getRepository()
          .findByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndTransitBufferConfigRequestId(
              orgId,
              destinationGeozone,
              sourceGeozone,
              carrierServiceId,
              transitBufferConfigRequestId)
          .map(getMapper()::toDomain);
    } catch (Exception e) {
      log.error(ERROR_WHILE_FETCHING_TRANSIT_BUFFERS + ", {}", String.valueOf(e));
      throw new CommonServiceException(
          ERROR_WHILE_FETCHING_TRANSIT_BUFFERS, HttpStatus.INTERNAL_SERVER_ERROR, 0x1777, null);
    }
  }

  @Override
  public TransitBufferV2DomainDto saveTransitBuffer(
      TransitBufferV2DomainDto transitBufferV2DomainDto) throws CommonServiceException {
    try {
      return save(transitBufferV2DomainDto);
    } catch (Exception e) {
      log.error("{}, {}", ERROR_WHILE_CREATING, String.valueOf(e));
      throw new CommonServiceException(
          ERROR_WHILE_CREATING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1780, null);
    }
  }

  @Override
  public void deleteTransitBufferEntityByIdAndOrgId(Long id, String orgId)
      throws CommonServiceException {
    try {
      getRepository().deleteByIdAndOrgId(id, orgId);
    } catch (Exception e) {
      log.error("{}, {}", ERROR_WHILE_DELETING, String.valueOf(e));
      throw new CommonServiceException(
          ERROR_WHILE_DELETING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1779, null);
    }
  }

  @Override
  public void deleteTransitBuffer(TransitBufferV2DomainDto transitBufferV2DomainDto)
      throws CommonServiceException {
    try {
      delete(transitBufferV2DomainDto);
    } catch (Exception e) {
      log.error("{}, {}", ERROR_WHILE_DELETING, String.valueOf(e));
      throw new CommonServiceException(
          ERROR_WHILE_DELETING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1779, null);
    }
  }

  @Override
  public Optional<TransitBufferV2DomainDto> fetchTransitBufferByOrgIdAndId(String orgId, Long id)
      throws CommonServiceException {
    try {
      return getRepository().findByOrgIdAndId(orgId, id).map(getMapper()::toDomain);
    } catch (Exception e) {
      log.error(ERROR_WHILE_FETCHING_TRANSIT_BUFFERS + ", {}", String.valueOf(e));
      throw new CommonServiceException(
          ERROR_WHILE_FETCHING_TRANSIT_BUFFERS, HttpStatus.INTERNAL_SERVER_ERROR, 0x1777, null);
    }
  }

  @Override
  public List<TransitBufferV2DomainDto> fetchTransitBufferById(Long transitBufferConfigRequestId)
      throws CommonServiceException {
    try {
      return getMapper()
          .toDomain(
              getRepository().findByTransitBufferConfigRequestId(transitBufferConfigRequestId));
    } catch (Exception e) {
      log.error(ERROR_WHILE_FETCHING_TRANSIT_BUFFERS + ", {}", String.valueOf(e));
      throw new CommonServiceException(
          ERROR_WHILE_FETCHING_TRANSIT_BUFFERS, HttpStatus.INTERNAL_SERVER_ERROR, 0x1777, null);
    }
  }

  @Override
  public TransitBufferV2DomainDto updateTransitBuffer(TransitBufferV2DomainDto domainDto)
      throws CommonServiceException {
    try {
      return save(domainDto);
    } catch (Exception e) {
      log.error(ERROR_WHILE_UPDATING_TRANSIT_BUFFERS + ", {}", String.valueOf(e));
      throw new CommonServiceException(
          ERROR_WHILE_UPDATING_TRANSIT_BUFFERS, HttpStatus.INTERNAL_SERVER_ERROR, 0x1778, null);
    }
  }

  @Override
  public Optional<TransitBufferV2DomainDto>
      fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndBufferStartDateAndBufferEndDate(
          String orgId,
          String destinationGeozone,
          String sourceGeozone,
          String carrierServiceId,
          Date bufferStartDays,
          Date bufferEndDays)
          throws CommonServiceException {
    try {
      return getRepository()
          .findByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndBufferStartDateAndBufferEndDate(
              orgId,
              destinationGeozone,
              sourceGeozone,
              carrierServiceId,
              bufferStartDays,
              bufferEndDays)
          .map(getMapper()::toDomain);
    } catch (Exception e) {
      log.error(ERROR_WHILE_FETCHING_TRANSIT_BUFFERS + ", {}", String.valueOf(e));
      throw new CommonServiceException(
          ERROR_WHILE_FETCHING_TRANSIT_BUFFERS, HttpStatus.INTERNAL_SERVER_ERROR, 0x1777, null);
    }
  }
}
