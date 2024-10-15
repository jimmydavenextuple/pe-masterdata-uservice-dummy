/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service.impl;

import com.nextuple.postgres.service.CommonPersistenceService;
import com.nextuple.transit.domain.pojo.ProjectedTransitEntity;
import com.nextuple.transit.persistence.domain.TransitDomainDto;
import com.nextuple.transit.persistence.domain.key.TransitDomainKey;
import com.nextuple.transit.persistence.entity.TransitEntity;
import com.nextuple.transit.persistence.entity.key.TransitKey;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.persistence.mapper.TransitEntityMapper;
import com.nextuple.transit.persistence.repository.TransitRepository;
import com.nextuple.transit.persistence.service.TransitPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransitPersistenceServiceImpl
    extends CommonPersistenceService<
        TransitDomainDto,
        TransitDomainKey,
        TransitEntity,
        TransitKey,
        TransitRepository,
        TransitEntityMapper>
    implements TransitPersistenceService {

  private static final Logger logger = LoggerFactory.getLogger(TransitPersistenceServiceImpl.class);

  private static final String ERROR_MESSAGE = "Error while fetching transit list";

  @Override
  public TransitDomainDto saveTransitDomainDto(TransitDomainDto transitDomainDto)
      throws TransitDomainException {
    try {
      return save(transitDomainDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to save transit data");
      throw new TransitDomainException(
          "Unable to save transit data",
          transitDomainDto.getOrgId(),
          transitDomainDto.getSourceGeozone(),
          transitDomainDto.getDestinationGeozone(),
          transitDomainDto.getCarrierServiceId());
    }
  }

  @Override
  public List<TransitDomainDto> filterAndGetTransitDetails(
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId,
      String serviceOption)
      throws TransitDomainException {
    try {
      List<TransitEntity> transitEntites =
          getRepository()
              .findByCarrierServiceIdsWithServiceOption(
                  orgId, sourceGeozone, destinationGeozone, carrierServiceId, serviceOption);
      return getMapper().toDomain(transitEntites);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find transit details");
      throw new TransitDomainException(
          "Error while finding transit details",
          orgId,
          sourceGeozone,
          destinationGeozone,
          carrierServiceId);
    }
  }

  @Override
  public Optional<TransitDomainDto> findTransitDetails(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId)
      throws TransitDomainException {
    try {
      return findByKey(
          TransitDomainKey.builder()
              .orgId(orgId)
              .carrierServiceId(carrierServiceId)
              .sourceGeozone(sourceGeozone)
              .destinationGeozone(destinationGeozone)
              .build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find transit details");
      throw new TransitDomainException(
          "Error while finding transit details",
          orgId,
          sourceGeozone,
          destinationGeozone,
          carrierServiceId);
    }
  }

  @Override
  public void deleteTransitDetails(TransitDomainDto transitDomainDto)
      throws TransitDomainException {
    try {
      delete(transitDomainDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete transit details");
      throw new TransitDomainException(
          "Error while deleting transit details",
          transitDomainDto.getOrgId(),
          transitDomainDto.getSourceGeozone(),
          transitDomainDto.getDestinationGeozone(),
          transitDomainDto.getCarrierServiceId());
    }
  }

  @Override
  public List<TransitDomainDto> fetchTransitList(
      String orgId, String destinationGeozone, List<String> sourceGeozones)
      throws TransitDomainException {
    try {
      List<TransitEntity> transitEntities =
          getRepository()
              .findByOrgIdAndDestinationGeozoneAndSourceGeoZones(
                  orgId, destinationGeozone, sourceGeozones);
      return getMapper().toDomain(transitEntities);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to fetch transit list");
      throw new TransitDomainException(ERROR_MESSAGE, orgId, null, destinationGeozone, null);
    }
  }

  @Override
  public List<String> fetchDestinationGeozones(
      String orgId, String sourceGeozone, List<String> carrierServiceIds)
      throws TransitDomainException {
    try {
      return getRepository()
          .findByOrgIdAndSourceGeozoneAndCarrierServiceIds(orgId, sourceGeozone, carrierServiceIds);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to fetch transit list");
      throw new TransitDomainException(ERROR_MESSAGE, orgId, sourceGeozone, null, null);
    }
  }

  @Override
  public Integer fetchTransitEntitiesCount(String orgId, String carrierServiceId)
      throws TransitDomainException {
    try {
      return getRepository().findTransitCountByOrgIdAndCarrierServiceId(orgId, carrierServiceId);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to fetch transit entities count");
      throw new TransitDomainException(
          "Error while fetching transit entities count", orgId, null, null, carrierServiceId);
    }
  }

  @Override
  public List<TransitDomainDto> fetchTransitListForDestinationGeoZone(
      String orgId, String destinationGeozone) throws TransitDomainException {
    try {
      List<TransitEntity> transitEntities =
          getRepository().findByOrgIdAndDestinationGeozone(orgId, destinationGeozone);
      return getMapper().toDomain(transitEntities);
    } catch (Exception e) {
      logger.error(
          String.valueOf(e),
          "Unable to fetch transit list for orgId: {} and destination geozone: {}",
          orgId,
          destinationGeozone);
      throw new TransitDomainException(ERROR_MESSAGE, orgId, null, destinationGeozone, null);
    }
  }

  @Override
  public List<ProjectedTransitEntity> fetchTransitListForDestinationGeoZones(
      String orgId, String carrierServiceId, List<String> destinationGeozones)
      throws TransitDomainException {
    try {
      return getRepository()
          .findByOrgIdAndCarrierServiceIdAndDestinationGeozoneIn(
              orgId, carrierServiceId, destinationGeozones);
    } catch (Exception e) {
      logger.error(
          String.valueOf(e),
          "Unable to fetch transit list for orgId: {} and destination geozones",
          orgId);
      throw new TransitDomainException(
          "Error while fetching transit entity list", orgId, null, null, carrierServiceId);
    }
  }

  @Override
  public List<String> fetchDistinctSourceGeoZones(String orgId, String carrierServiceId)
      throws TransitDomainException {
    try {
      return getRepository().findDistinctSourceGeoZones(orgId, carrierServiceId);
    } catch (Exception e) {
      logger.error(
          String.valueOf(e),
          "Unable to fetch distinct source geozones list for orgId: {} and carrierServiceId: {}",
          orgId,
          carrierServiceId);
      throw new TransitDomainException(
          "Unable to fetch distinct source geozones list for orgId and carrierServiceId",
          orgId,
          null,
          null,
          carrierServiceId);
    }
  }

  @Override
  public List<String> fetchDistinctDestinationGeoZones(String orgId, String carrierServiceId)
      throws TransitDomainException {
    try {
      return getRepository().findDistinctDestinationGeoZones(orgId, carrierServiceId);
    } catch (Exception e) {
      logger.error(
          String.valueOf(e),
          "Unable to fetch distinct destination geozones list for orgId: {} and carrierServiceId: {}",
          orgId,
          carrierServiceId);
      throw new TransitDomainException(
          "Unable to fetch distinct destination geozones list for given orgId and carrierServiceId",
          orgId,
          null,
          null,
          carrierServiceId);
    }
  }
}
