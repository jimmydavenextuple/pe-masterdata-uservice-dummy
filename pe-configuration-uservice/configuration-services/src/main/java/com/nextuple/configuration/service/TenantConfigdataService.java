/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.configuration.service;

import static com.nextuple.common.constants.ConfigKeyConstants.CONFIG_KEYS_MULTIPLE_VALUES_NOT_ALLOWED;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.configuration.domain.mapper.TenantConfigdataMapper;
import com.nextuple.configuration.inbound.TenantConfigdataRequest;
import com.nextuple.configuration.inbound.TenantConfigdataUpdateRequest;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import com.nextuple.configuration.persistence.domain.ConfigMetadataDomainDto;
import com.nextuple.configuration.persistence.domain.TenantConfigdataDomainDto;
import com.nextuple.configuration.persistence.service.ConfigMetadataPersistenceService;
import com.nextuple.configuration.persistence.service.TenantConfigdataPersistenceService;
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
public class TenantConfigdataService {
  private static final Logger logger = LoggerFactory.getLogger(TenantConfigdataService.class);

  private final TenantConfigdataPersistenceService tenantConfigdataPersistenceService;

  private final ConfigMetadataPersistenceService configMetadataPersistenceService;
  private static final TenantConfigdataMapper INSTANCE =
      Mappers.getMapper(TenantConfigdataMapper.class);
  private static final String ORG_ID = "orgId";
  private static final String CONFIG_KEY = "configKey";
  private static final String TENANT_CONFIG_DATA_EXCEPTION_MESSAGE =
      "Tenant configuration data not found";

  private static final String CONFIG_VAL_FORMAT_ERROR_MSG =
      "The config value for %s can not contain comma separated string";

  public TenantConfigdataResponse processAddTenantConfigdata(
      TenantConfigdataRequest tenantConfigdataRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processAddTenantConfigdata Service --");
    ConfigurationUtil.validateConfigKeyFormat(tenantConfigdataRequest.getConfigKey());
    Optional<TenantConfigdataDomainDto> existingTenantConfigdataDomainDto =
        tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
            tenantConfigdataRequest.getOrgId(), tenantConfigdataRequest.getConfigKey());
    validateConfigValueFormat(tenantConfigdataRequest);
    if (existingTenantConfigdataDomainDto.isPresent()) {
      logger.error(
          "Tenant configuration data already associated for given orgId :{} and configKey : {}",
          tenantConfigdataRequest.getOrgId(),
          tenantConfigdataRequest.getConfigKey());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID, FieldError.builder().rejectedValue(tenantConfigdataRequest.getOrgId()).build());
      errorMap.put(
          CONFIG_KEY,
          FieldError.builder().rejectedValue(tenantConfigdataRequest.getConfigKey()).build());
      throw new CommonServiceException(
          "Tenant configuration data already associated for given orgId and configKey",
          HttpStatus.BAD_REQUEST,
          0X1771,
          errorMap);
    }
    return INSTANCE.toTenantConfigdataResponse(
        tenantConfigdataPersistenceService.saveTenantConfigdata(
            INSTANCE.toTenantConfigdataDomainDto(tenantConfigdataRequest)));
  }

  public TenantConfigdataResponse processGetTenantConfigdataByOrgIdAndConfigKey(
      String orgId, String configKey) throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processGetTenantConfigdataByOrgIdAndConfigKey Service --");
    Optional<TenantConfigdataDomainDto> existingTenantConfigdataDomainDto =
        tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
            orgId, configKey);
    if (existingTenantConfigdataDomainDto.isEmpty()) {
      Optional<ConfigMetadataDomainDto> defaultConfigMetadataDomainDto =
          configMetadataPersistenceService.fetchConfigMetadataByConfigKey(configKey);
      if (defaultConfigMetadataDomainDto.isEmpty()) {
        logger.error(TENANT_CONFIG_DATA_EXCEPTION_MESSAGE);
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
        errorMap.put(CONFIG_KEY, FieldError.builder().rejectedValue(configKey).build());
        throw new CommonServiceException(
            TENANT_CONFIG_DATA_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST, 0X1771, errorMap);
      }
      return TenantConfigdataResponse.builder()
          .orgId(orgId)
          .configKey(configKey)
          .configValue(defaultConfigMetadataDomainDto.get().getDefaultConfigValue())
          .build();
    }
    return INSTANCE.toTenantConfigdataResponse(existingTenantConfigdataDomainDto.get());
  }

  public TenantConfigdataResponse processUpdateTenantConfigdata(
      String orgId, String configKey, TenantConfigdataUpdateRequest tenantConfigdataUpdateRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processUpdateTenantConfigdata Service --");
    validateConfigValueFormat(
        TenantConfigdataRequest.builder()
            .orgId(orgId)
            .configValue(tenantConfigdataUpdateRequest.getConfigValue())
            .configKey(configKey)
            .build());
    var existingTenantConfigdataDomainDto =
        getTenantConfigdataByOrgIdAndConfigKey(orgId, configKey);
    INSTANCE.updateTenantConfigdata(
        tenantConfigdataUpdateRequest, existingTenantConfigdataDomainDto);
    return INSTANCE.toTenantConfigdataResponse(
        tenantConfigdataPersistenceService.saveTenantConfigdata(existingTenantConfigdataDomainDto));
  }

  public TenantConfigdataResponse processDeleteTenantConfigdata(String orgId, String configKey)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processDeleteTenantConfigData Service --");
    var existingTenantConfigdataDomainDto =
        getTenantConfigdataByOrgIdAndConfigKey(orgId, configKey);
    tenantConfigdataPersistenceService.deleteTenantConfigdata(existingTenantConfigdataDomainDto);
    return INSTANCE.toTenantConfigdataResponse(existingTenantConfigdataDomainDto);
  }

  private TenantConfigdataDomainDto getTenantConfigdataByOrgIdAndConfigKey(
      String orgId, String configKey) throws PromiseEngineException, CommonServiceException {
    Optional<TenantConfigdataDomainDto> existingTenantConfigdataDomainDto =
        tenantConfigdataPersistenceService.fetchTenantConfigdataByOrgIdAndConfigKey(
            orgId, configKey);
    if (existingTenantConfigdataDomainDto.isEmpty()) {
      logger.error(TENANT_CONFIG_DATA_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CONFIG_KEY, FieldError.builder().rejectedValue(configKey).build());
      throw new CommonServiceException(
          TENANT_CONFIG_DATA_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST, 0X1771, errorMap);
    }
    return existingTenantConfigdataDomainDto.get();
  }

  void validateConfigValueFormat(TenantConfigdataRequest tenantConfigdataRequest)
      throws CommonServiceException {
    if (CONFIG_KEYS_MULTIPLE_VALUES_NOT_ALLOWED.contains(tenantConfigdataRequest.getConfigKey())
        && tenantConfigdataRequest.getConfigValue().contains(",")) {
      logger.error(
          String.format(CONFIG_VAL_FORMAT_ERROR_MSG, tenantConfigdataRequest.getConfigKey()));
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID, FieldError.builder().rejectedValue(tenantConfigdataRequest.getOrgId()).build());
      errorMap.put(
          CONFIG_KEY,
          FieldError.builder().rejectedValue(tenantConfigdataRequest.getConfigKey()).build());
      throw new CommonServiceException(
          String.format(CONFIG_VAL_FORMAT_ERROR_MSG, tenantConfigdataRequest.getConfigKey()),
          HttpStatus.BAD_REQUEST,
          0X1771,
          errorMap);
    }
  }
}
