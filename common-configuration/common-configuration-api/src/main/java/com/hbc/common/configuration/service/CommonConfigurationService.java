package com.hbc.common.configuration.service;

import com.hbc.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.hbc.common.configuration.api.domain.inbound.CreateCommonConfigurationRequest;
import com.hbc.common.configuration.api.domain.inbound.UpdateCommonConfigurationRequest;
import com.hbc.common.configuration.domain.CommonConfigurationDomain;
import com.hbc.common.configuration.domain.entity.CommonConfiguration;
import com.hbc.common.configuration.mapper.CommonConfigMapper;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.exception.PromiseEngineException;
import com.hbc.common.response.error.FieldError;
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
public class CommonConfigurationService {

  public static final CommonConfigMapper INSTANCE = Mappers.getMapper(CommonConfigMapper.class);
  private static final Logger logger = LoggerFactory.getLogger(CommonConfigurationService.class);
  private static final String ORG_ID = "orgId";
  private static final String KEY = "key";
  private static final String TYPE = "type";

  private static final String COMMON_CONFIG_NOT_FOUND_ERROR_MSG =
      "Common Configuration not found for given details";
  private final CommonConfigurationDomain configurationDomain;

  public CommonConfigurationDto fetchValue(String orgId, String type, String key)
      throws PromiseEngineException {

    Optional<CommonConfiguration> globalConfiguration =
        configurationDomain.getCommonConfiguration(orgId, type, key);
    if (globalConfiguration.isEmpty()) {
      return CommonConfigurationDto.builder()
          .key(key)
          .orgId(orgId)
          .type(type)
          .value("UNDEFINED")
          .build();
    }
    return INSTANCE.toCommonMasterConfigurationDto(globalConfiguration.get());
  }

  public CommonConfigurationDto createCommonConfig(
      CreateCommonConfigurationRequest createCommonConfigurationRequest)
      throws PromiseEngineException, CommonServiceException {
    Optional<CommonConfiguration> globalConfiguration =
        configurationDomain.getCommonConfiguration(
            createCommonConfigurationRequest.getOrgId(),
            createCommonConfigurationRequest.getType(),
            createCommonConfigurationRequest.getKey());

    if (globalConfiguration.isPresent()) {
      logger.error(
          "Common Configuration already exists for orgId:{} , type:{}, key:{}",
          createCommonConfigurationRequest.getOrgId(),
          createCommonConfigurationRequest.getType(),
          createCommonConfigurationRequest.getKey());
      Map<String, FieldError> errorMap = new HashMap<>();

      errorMap.put(
          ORG_ID,
          FieldError.builder().rejectedValue(createCommonConfigurationRequest.getOrgId()).build());
      errorMap.put(
          TYPE,
          FieldError.builder().rejectedValue(createCommonConfigurationRequest.getType()).build());
      errorMap.put(
          KEY,
          FieldError.builder().rejectedValue(createCommonConfigurationRequest.getKey()).build());
      throw new CommonServiceException(
          "Common Configuration already exists for the given details",
          HttpStatus.BAD_REQUEST,
          0x1772,
          errorMap);
    }

    return INSTANCE.toCommonMasterConfigurationDto(
        configurationDomain.saveCommonConfiguration(
            INSTANCE.toCommonMasterConfiguration(createCommonConfigurationRequest)));
  }

  public CommonConfigurationDto updateCommonConfiguration(
      String orgId, String type, String key, UpdateCommonConfigurationRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {

    Optional<CommonConfiguration> globalConfiguration =
        configurationDomain.getCommonConfiguration(orgId, type, key);

    if (globalConfiguration.isEmpty()) {
      logger.error(COMMON_CONFIG_NOT_FOUND_ERROR_MSG);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(TYPE, FieldError.builder().rejectedValue(type).build());
      errorMap.put(KEY, FieldError.builder().rejectedValue(key).build());
      throw new CommonServiceException(
          COMMON_CONFIG_NOT_FOUND_ERROR_MSG, HttpStatus.NOT_FOUND, 0x1773, errorMap);
    }
    return INSTANCE.toCommonMasterConfigurationDto(
        configurationDomain.saveCommonConfiguration(
            INSTANCE.toCommonMasterConfiguration(orgId, type, key, baseRequest)));
  }

  public CommonConfigurationDto deleteCommonConfiguration(String orgId, String type, String key)
      throws PromiseEngineException, CommonServiceException {

    Optional<CommonConfiguration> globalConfiguration =
        configurationDomain.getCommonConfiguration(orgId, type, key);

    if (globalConfiguration.isEmpty()) {
      logger.error(COMMON_CONFIG_NOT_FOUND_ERROR_MSG);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(TYPE, FieldError.builder().rejectedValue(type).build());
      errorMap.put(KEY, FieldError.builder().rejectedValue(key).build());
      throw new CommonServiceException(
          COMMON_CONFIG_NOT_FOUND_ERROR_MSG, HttpStatus.NOT_FOUND, 0x1773, errorMap);
    }
    configurationDomain.deleteCommonConfiguration(globalConfiguration.get());
    return INSTANCE.toCommonMasterConfigurationDto(globalConfiguration.get());
  }
}
