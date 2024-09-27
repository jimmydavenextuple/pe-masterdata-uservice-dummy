/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
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
import com.nextuple.configuration.domain.ConfigMetadataDomain;
import com.nextuple.configuration.domain.TenantConfigdataDomain;
import com.nextuple.configuration.domain.entity.ConfigMetadataEntity;
import com.nextuple.configuration.domain.entity.TenantConfigdataEntity;
import com.nextuple.configuration.domain.mapper.TenantConfigdataMapper;
import com.nextuple.configuration.inbound.TenantConfigdataRequest;
import com.nextuple.configuration.inbound.TenantConfigdataUpdateRequest;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
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

  private final TenantConfigdataDomain tenantConfigdataDomain;
  private final ConfigMetadataDomain configMetadataDomain;
  private static final TenantConfigdataMapper INSTANCE =
      Mappers.getMapper(TenantConfigdataMapper.class);
  private static final String ORG_ID = "orgId";
  private static final String CONFIG_KEY = "configKey";

  private static final String TENANT_CONFIG_DATA_EXCEPTION_MESSAGE =
      "Tenant configuration data not found";

  public TenantConfigdataResponse processAddTenantConfigdata(
      TenantConfigdataRequest tenantConfigdataRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processAddTenantConfigdata Service --");

    ConfigurationUtil.validateConfigKeyFormat(tenantConfigdataRequest.getConfigKey());

    Optional<TenantConfigdataEntity> existingTenantConfigdataEntity =
        tenantConfigdataDomain.fetchTenantConfigdataByOrgIdAndConfigKey(
            tenantConfigdataRequest.getOrgId(), tenantConfigdataRequest.getConfigKey());
    if (existingTenantConfigdataEntity.isPresent()) {
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

    var tenantConfigdataEntity = INSTANCE.toTenantConfigdataEntity(tenantConfigdataRequest);

    return INSTANCE.toTenantConfigdataResponse(
        tenantConfigdataDomain.saveTenantConfigdata(tenantConfigdataEntity));
  }

  public TenantConfigdataResponse processGetTenantConfigdataByOrgIdAndConfigKey(
      String orgId, String configKey) throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processGetTenantConfigdataByOrgIdAndConfigKey Service --");
    Optional<TenantConfigdataEntity> tenantConfigdataEntity =
        tenantConfigdataDomain.fetchTenantConfigdataByOrgIdAndConfigKey(orgId, configKey);
    if (tenantConfigdataEntity.isEmpty()) {
      Optional<ConfigMetadataEntity> defaultConfigdataEntity =
          configMetadataDomain.fetchConfigMetadataByConfigKey(configKey);
      if (defaultConfigdataEntity.isEmpty()) {
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
          .configValue(defaultConfigdataEntity.get().getDefaultConfigValue())
          .build();
    }
    return INSTANCE.toTenantConfigdataResponse(tenantConfigdataEntity.get());
  }

  public TenantConfigdataResponse processUpdateTenantConfigdata(
      String orgId, String configKey, TenantConfigdataUpdateRequest tenantConfigdataUpdateRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processUpdateTenantConfigdata Service --");

    var tenantConfigdataEntity = getTenantConfigdataByOrgIdAndConfigKey(orgId, configKey);

    INSTANCE.updateTenantConfigdata(tenantConfigdataUpdateRequest, tenantConfigdataEntity);
    return INSTANCE.toTenantConfigdataResponse(
        tenantConfigdataDomain.saveTenantConfigdata(tenantConfigdataEntity));
  }

  public TenantConfigdataResponse processDeleteTenantConfigdata(String orgId, String configKey)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processDeleteTenantConfigData Service --");

    var tenantConfigdataEntity = getTenantConfigdataByOrgIdAndConfigKey(orgId, configKey);
    var tenantConfigdataResponse = INSTANCE.toTenantConfigdataResponse(tenantConfigdataEntity);
    tenantConfigdataDomain.deleteTenantConfigdata(tenantConfigdataEntity);
    return tenantConfigdataResponse;
  }

  private TenantConfigdataEntity getTenantConfigdataByOrgIdAndConfigKey(
      String orgId, String configKey) throws PromiseEngineException, CommonServiceException {
    Optional<TenantConfigdataEntity> tenantConfigdataEntity =
        tenantConfigdataDomain.fetchTenantConfigdataByOrgIdAndConfigKey(orgId, configKey);
    if (tenantConfigdataEntity.isEmpty()) {
      logger.error(TENANT_CONFIG_DATA_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CONFIG_KEY, FieldError.builder().rejectedValue(configKey).build());
      throw new CommonServiceException(
          TENANT_CONFIG_DATA_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST, 0X1771, errorMap);
    }
    return tenantConfigdataEntity.get();
  }
}
