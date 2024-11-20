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
import com.nextuple.configuration.persistence.domain.ConfigMetadataDomainDto;
import com.nextuple.configuration.persistence.domain.key.ConfigMetadataDomainKey;
import com.nextuple.configuration.persistence.entity.ConfigMetadataEntity;
import com.nextuple.configuration.persistence.entity.key.ConfigMetadataKey;
import com.nextuple.configuration.persistence.mapper.ConfigMetadataEntityMapper;
import com.nextuple.configuration.persistence.repository.ConfigMetadataRepository;
import com.nextuple.configuration.persistence.service.ConfigMetadataPersistenceService;
import com.nextuple.postgres.service.CommonPersistenceService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigMetadataPersistenceServiceImpl
    extends CommonPersistenceService<
        ConfigMetadataDomainDto,
        ConfigMetadataDomainKey,
        ConfigMetadataEntity,
        ConfigMetadataKey,
        ConfigMetadataRepository,
        ConfigMetadataEntityMapper>
    implements ConfigMetadataPersistenceService {
  private static final Logger logger =
      LoggerFactory.getLogger(ConfigMetadataPersistenceServiceImpl.class);

  @Override
  public ConfigMetadataDomainDto saveConfigMetadata(ConfigMetadataDomainDto configMetadataDomainDto)
      throws PromiseEngineException {
    logger.debug("-- inside saveConfigMetadata domain --");
    try {
      return save(configMetadataDomainDto);
    } catch (Exception e) {
      logger.error("Unable to save configuration metadata", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save configuration metadata");
    }
  }

  @Override
  public Optional<ConfigMetadataDomainDto> fetchConfigMetadataByConfigKey(String configKey)
      throws PromiseEngineException {
    logger.debug("-- inside fetchConfigMetadata domain --");
    try {
      return getRepository().findByConfigKey(configKey).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to fetch configuration metadata", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch configuration metadata");
    }
  }

  @Override
  public void deleteConfigMetadata(ConfigMetadataDomainDto configMetadataDomainDto)
      throws PromiseEngineException {
    logger.debug("-- inside deleteConfigMetadata domain --");
    try {
      delete(configMetadataDomainDto);
    } catch (Exception e) {
      logger.error("Unable to delete configuration metadata", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete configuration metadata");
    }
  }
}
