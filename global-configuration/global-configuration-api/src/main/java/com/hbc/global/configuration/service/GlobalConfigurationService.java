package com.hbc.global.configuration.service;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.exception.PromiseEngineException;
import com.hbc.common.response.error.FieldError;
import com.hbc.global.configuration.api.domain.dto.GlobalConfigurationDto;
import com.hbc.global.configuration.api.domain.inbound.CreateGlobalConfigurationRequest;
import com.hbc.global.configuration.api.domain.inbound.UpdateGlobalConfigurationRequest;
import com.hbc.global.configuration.domain.GlobalConfigurationDomain;
import com.hbc.global.configuration.domain.entity.GlobalConfiguration;
import com.hbc.global.configuration.mapper.GlobalConfigMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GlobalConfigurationService {

  private static final Logger logger = LoggerFactory.getLogger(GlobalConfigurationService.class);
  private final GlobalConfigurationDomain globalConfigurationDomain;
  private static final String ORG_ID = "orgId";
  private static final String KEY = "key";
  private static final String TYPE = "type";

  private static final String GLOBAL_CONFIG_NOT_FOUND_ERROR_MSG =
      "Global Configuration not found for given details";

  public static final GlobalConfigMapper INSTANCE = Mappers.getMapper(GlobalConfigMapper.class);

  public GlobalConfigurationDto fetchValue(String orgId, String type, String key)
      throws PromiseEngineException {

    Optional<GlobalConfiguration> globalConfiguration =
        globalConfigurationDomain.getGlobalConfiguration(orgId, type, key);
    if (globalConfiguration.isEmpty()) {
      return GlobalConfigurationDto.builder()
          .key(key)
          .orgId(orgId)
          .type(type)
          .value("UNDEFINED")
          .build();
    }
    return INSTANCE.toGlobalConfigurationDto(globalConfiguration.get());
  }

  public GlobalConfigurationDto createGlobalConfig(
      CreateGlobalConfigurationRequest globalConfigurationRequest)
      throws PromiseEngineException, CommonServiceException {
    Optional<GlobalConfiguration> globalConfiguration =
        globalConfigurationDomain.getGlobalConfiguration(
            globalConfigurationRequest.getOrgId(),
            globalConfigurationRequest.getType(),
            globalConfigurationRequest.getKey());

    if (globalConfiguration.isPresent()) {
      logger.error(
          "Global Configuration already exists for orgId:{} , type:{}, key:{}",
          globalConfigurationRequest.getOrgId(),
          globalConfigurationRequest.getType(),
          globalConfigurationRequest.getKey());
      Map<String, FieldError> errorMap = new HashMap<>();

      errorMap.put(
          ORG_ID,
          FieldError.builder().rejectedValue(globalConfigurationRequest.getOrgId()).build());
      errorMap.put(
          TYPE, FieldError.builder().rejectedValue(globalConfigurationRequest.getType()).build());
      errorMap.put(
          KEY, FieldError.builder().rejectedValue(globalConfigurationRequest.getKey()).build());
      throw new CommonServiceException(
          "Global Configuration already exists for the given details",
          HttpStatus.BAD_REQUEST,
          0x1772,
          errorMap);
    }

    return INSTANCE.toGlobalConfigurationDto(
        globalConfigurationDomain.saveGlobalConfiguration(
            INSTANCE.toGlobalConfiguration(globalConfigurationRequest)));
  }

  public GlobalConfigurationDto updateGlobalConfiguration(
      String orgId, String type, String key, UpdateGlobalConfigurationRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {

    Optional<GlobalConfiguration> globalConfiguration =
        globalConfigurationDomain.getGlobalConfiguration(orgId, type, key);

    if (globalConfiguration.isEmpty()) {
      logger.error(GLOBAL_CONFIG_NOT_FOUND_ERROR_MSG);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(TYPE, FieldError.builder().rejectedValue(type).build());
      errorMap.put(KEY, FieldError.builder().rejectedValue(key).build());
      throw new CommonServiceException(
          GLOBAL_CONFIG_NOT_FOUND_ERROR_MSG, HttpStatus.NOT_FOUND, 0x1773, errorMap);
    }
    return INSTANCE.toGlobalConfigurationDto(
        globalConfigurationDomain.saveGlobalConfiguration(
            INSTANCE.toGlobalConfiguration(orgId, type, key, baseRequest)));
  }

  public GlobalConfigurationDto deleteGlobalConfiguration(String orgId, String type, String key)
      throws PromiseEngineException, CommonServiceException {

    Optional<GlobalConfiguration> globalConfiguration =
        globalConfigurationDomain.getGlobalConfiguration(orgId, type, key);

    if (globalConfiguration.isEmpty()) {
      logger.error(GLOBAL_CONFIG_NOT_FOUND_ERROR_MSG);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(TYPE, FieldError.builder().rejectedValue(type).build());
      errorMap.put(KEY, FieldError.builder().rejectedValue(key).build());
      throw new CommonServiceException(
          GLOBAL_CONFIG_NOT_FOUND_ERROR_MSG, HttpStatus.NOT_FOUND, 0x1773, errorMap);
    }
    globalConfigurationDomain.deleteGlobalConfiguration(globalConfiguration.get());
    return INSTANCE.toGlobalConfigurationDto(globalConfiguration.get());
  }
}
