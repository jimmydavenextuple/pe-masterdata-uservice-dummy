/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.configuration.persistence.service.impl;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.configuration.persistence.domain.TenantConfigdataDomainDto;
import com.nextuple.configuration.persistence.domain.key.TenantConfigdataDomainKey;
import com.nextuple.configuration.persistence.entity.TenantConfigdataEntity;
import com.nextuple.configuration.persistence.entity.key.TenantConfigdataKey;
import com.nextuple.configuration.persistence.mapper.TenantConfigdataEntityMapper;
import com.nextuple.configuration.persistence.repository.TenantConfigdataRepository;
import com.nextuple.configuration.persistence.service.TenantConfigdataPersistenceService;
import com.nextuple.postgres.service.CommonPersistenceService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantConfigdataPersistenceServiceImpl
    extends CommonPersistenceService<
        TenantConfigdataDomainDto,
        TenantConfigdataDomainKey,
        TenantConfigdataEntity,
        TenantConfigdataKey,
        TenantConfigdataRepository,
        TenantConfigdataEntityMapper>
    implements TenantConfigdataPersistenceService {
  private static final Logger logger =
      LoggerFactory.getLogger(TenantConfigdataPersistenceServiceImpl.class);

  @Override
  public TenantConfigdataDomainDto saveTenantConfigdata(
      TenantConfigdataDomainDto tenantConfigdataDomainDto) throws PromiseEngineException {
    logger.debug("-- inside saveTenantConfigdata domain --");
    try {
      return save(tenantConfigdataDomainDto);
    } catch (Exception e) {
      logger.error("Unable to save tenant configuration data");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save tenant configuration data");
    }
  }

  @Override
  public Optional<TenantConfigdataDomainDto> fetchTenantConfigdataByOrgIdAndConfigKey(
      String orgId, String configKey) throws PromiseEngineException {
    logger.debug("-- inside fetchTenantConfigdataByOrgIdAndConfigKey domain --");
    try {
      return getRepository().findByOrgIdAndConfigKey(orgId, configKey).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to fetch tenant configuration data by orgId and configKey", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch tenant configuration data by orgId and configKey");
    }
  }

  @Override
  public void deleteTenantConfigdata(TenantConfigdataDomainDto tenantConfigdataDomainDto)
      throws PromiseEngineException {
    logger.debug("-- inside deleteTenantConfigdata domain --");
    try {
      delete(tenantConfigdataDomainDto);
    } catch (Exception e) {
      logger.error("Unable to delete tenant configuration data", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to delete tenant configuration data");
    }
  }
}
