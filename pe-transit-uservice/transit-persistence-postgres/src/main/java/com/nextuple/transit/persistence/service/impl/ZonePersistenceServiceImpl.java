/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service.impl;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.postgres.service.CommonPersistenceService;
import com.nextuple.transit.persistence.domain.ZoneDomainDto;
import com.nextuple.transit.persistence.domain.key.ZoneDomainKey;
import com.nextuple.transit.persistence.entity.ZoneEntity;
import com.nextuple.transit.persistence.entity.key.ZoneKey;
import com.nextuple.transit.persistence.mapper.ZoneEntityMapper;
import com.nextuple.transit.persistence.repository.ZoneRepository;
import com.nextuple.transit.persistence.service.ZonePersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ZonePersistenceServiceImpl
    extends CommonPersistenceService<
        ZoneDomainDto, ZoneDomainKey, ZoneEntity, ZoneKey, ZoneRepository, ZoneEntityMapper>
    implements ZonePersistenceService {
  private static final Logger logger = LoggerFactory.getLogger(ZonePersistenceServiceImpl.class);

  @Override
  public ZoneDomainDto saveZone(ZoneDomainDto zoneDomainDto) throws PromiseEngineException {
    logger.debug("-- inside saveZone persistence service--");
    try {
      return save(zoneDomainDto);
    } catch (Exception e) {
      logger.error("Unable to save zone", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER, ExceptionCodeMapping.DAO_SAVE_FAILED, "Unable to save zone");
    }
  }

  @Override
  public Optional<ZoneDomainDto> fetchZoneDetails(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId)
      throws PromiseEngineException {
    logger.debug("-- inside fetchZoneDetails persistence service --");
    try {
      return findByKey(
          ZoneDomainKey.builder()
              .orgId(orgId)
              .sourceGeozone(sourceGeozone)
              .destinationGeozone(destinationGeozone)
              .carrierServiceId(carrierServiceId)
              .build());
    } catch (Exception e) {
      logger.error("Unable to find zone", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER, ExceptionCodeMapping.DAO_FIND_FAILED, "Unable to find zone");
    }
  }

  @Override
  public ZoneDomainDto deleteZoneDetails(ZoneDomainDto zoneDomainDto)
      throws PromiseEngineException {
    logger.debug("-- inside deleteZoneDetails persistence service --");
    try {
      this.delete(zoneDomainDto);
      return zoneDomainDto;
    } catch (Exception e) {
      logger.error("Unable to delete zone", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete zone");
    }
  }

  @Override
  public List<ZoneDomainDto> fetchZoneByOrgIdAndDestGeozone(String orgId, String destinationGeozone)
      throws PromiseEngineException {
    logger.debug("-- inside fetchZoneByOrgIdAndDestGeozone persistence service --");
    try {
      List<ZoneEntity> entity =
          getRepository().findByOrgIdAndDestinationGeozone(orgId, destinationGeozone);
      return getMapper().toDomain(entity);
    } catch (Exception e) {
      logger.error("Unable to fetch zones list", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch zones list");
    }
  }
}
