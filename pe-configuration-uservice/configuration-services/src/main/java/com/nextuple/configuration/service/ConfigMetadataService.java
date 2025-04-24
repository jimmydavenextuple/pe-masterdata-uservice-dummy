/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.configuration.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.configuration.domain.mapper.ConfigMetadataMapper;
import com.nextuple.configuration.inbound.ConfigMetadataBaseRequest;
import com.nextuple.configuration.inbound.ConfigMetadataRequest;
import com.nextuple.configuration.outbound.ConfigMetadataResponse;
import com.nextuple.configuration.persistence.domain.ConfigMetadataDomainDto;
import com.nextuple.configuration.persistence.service.ConfigMetadataPersistenceService;
import com.nextuple.configuration.utils.ConfigurationUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigMetadataService {
  private static final Logger logger = LoggerFactory.getLogger(ConfigMetadataService.class);
  private static final String CONFIG_KEY = "configKey";
  private static final String CONFIG_METADATA_EXCEPTION_MESSAGE =
      "Configuration metadata not found for given configKey";

  private final ConfigMetadataPersistenceService configMetadataPersistenceService;
  private static final ConfigMetadataMapper INSTANCE =
      Mappers.getMapper(ConfigMetadataMapper.class);

  public ConfigMetadataResponse upsertConfigMetadata(ConfigMetadataRequest configMetadataRequest)
      throws PromiseEngineException, CommonServiceException {

    Optional<ConfigMetadataDomainDto> existingMetadataOpt =
        configMetadataPersistenceService.fetchConfigMetadataByConfigKey(
            configMetadataRequest.getConfigKey());
    if (existingMetadataOpt.isPresent()) {
      return processUpdateConfigMetadata(
          configMetadataRequest.getConfigKey(), configMetadataRequest);
    } else {
      return processAddConfigMetadata(configMetadataRequest);
    }
  }

  public ConfigMetadataResponse processAddConfigMetadata(
      ConfigMetadataRequest configMetadataRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processAddConfigMetadata Service --");
    ConfigurationUtil.validateConfigKeyFormat(configMetadataRequest.getConfigKey());
    Optional<ConfigMetadataDomainDto> existingConfigMetadataDomainDto =
        configMetadataPersistenceService.fetchConfigMetadataByConfigKey(
            configMetadataRequest.getConfigKey());
    if (existingConfigMetadataDomainDto.isPresent()) {
      logger.error(
          "Configuration metadata already associated for given configKey : {}",
          configMetadataRequest.getConfigKey());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          CONFIG_KEY,
          FieldError.builder().rejectedValue(configMetadataRequest.getConfigKey()).build());
      throw new CommonServiceException(
          "Configuration metadata already associated for given configKey",
          HttpStatus.BAD_REQUEST,
          0X1771,
          errorMap);
    }
    return INSTANCE.toConfigMetadataResponse(
        configMetadataPersistenceService.saveConfigMetadata(
            INSTANCE.toConfigMetadataDto(configMetadataRequest)));
  }

  public ConfigMetadataResponse getConfigMetadataByConfigKey(String configKey)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processGetConfigMetadataByConfigKey Service --");
    Optional<ConfigMetadataDomainDto> existingConfigMetadataDomainDto =
        configMetadataPersistenceService.fetchConfigMetadataByConfigKey(configKey);
    if (existingConfigMetadataDomainDto.isEmpty()) {
      logger.error(CONFIG_METADATA_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(CONFIG_KEY, FieldError.builder().rejectedValue(configKey).build());
      throw new CommonServiceException(
          CONFIG_METADATA_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST, 0X1771, errorMap);
    }
    return INSTANCE.toConfigMetadataResponse(existingConfigMetadataDomainDto.get());
  }

  public ConfigMetadataResponse processUpdateConfigMetadata(
      String configKey, ConfigMetadataBaseRequest configMetadataBaseRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processUpdateConfigMetadata Service --");
    var existingConfigMetadataDto = fetchConfigMetadataDto(configKey);
    INSTANCE.updateConfigMetadataDto(configMetadataBaseRequest, existingConfigMetadataDto);
    return INSTANCE.toConfigMetadataResponse(
        configMetadataPersistenceService.saveConfigMetadata(existingConfigMetadataDto));
  }

  public ConfigMetadataResponse processDeleteConfigMetadata(String configKey)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside deleteConfigMetadata service");
    var existingConfigMetadataDomainDto = fetchConfigMetadataDto(configKey);
    configMetadataPersistenceService.deleteConfigMetadata(existingConfigMetadataDomainDto);
    return INSTANCE.toConfigMetadataResponse(existingConfigMetadataDomainDto);
  }

  private ConfigMetadataDomainDto fetchConfigMetadataDto(String configKey)
      throws CommonServiceException, PromiseEngineException {
    Optional<ConfigMetadataDomainDto> existingConfigMetadataDomainDto =
        configMetadataPersistenceService.fetchConfigMetadataByConfigKey(configKey);
    if (existingConfigMetadataDomainDto.isEmpty()) {
      logger.error(CONFIG_METADATA_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(CONFIG_KEY, FieldError.builder().rejectedValue(configKey).build());
      throw new CommonServiceException(
          CONFIG_METADATA_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST, 0X1771, errorMap);
    }
    return existingConfigMetadataDomainDto.get();
  }
}
