package com.hbc.common.configuration.service;

import com.hbc.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.hbc.common.configuration.api.domain.inbound.CreateCommonConfigurationRequest;
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

    Optional<CommonConfiguration> commonConfiguration =
        configurationDomain.getCommonConfiguration(orgId, type, key);
    if (commonConfiguration.isEmpty()) {
      return CommonConfigurationDto.builder()
          .key(key)
          .orgId(orgId)
          .type(type)
          .value("UNDEFINED")
          .build();
    }
    return INSTANCE.toCommonMasterConfigurationDto(commonConfiguration.get());
  }

  public CommonConfigurationDto createCommonConfig(
      CreateCommonConfigurationRequest createCommonConfigurationRequest)
      throws PromiseEngineException {

    return INSTANCE.toCommonMasterConfigurationDto(
        configurationDomain.saveCommonConfiguration(
            INSTANCE.toCommonMasterConfiguration(createCommonConfigurationRequest)));
  }

  public CommonConfigurationDto updateCommonConfiguration(
      CreateCommonConfigurationRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {
    String orgId = baseRequest.getOrgId();
    String type = baseRequest.getType();
    String key = baseRequest.getKey();
    Optional<CommonConfiguration> commonConfiguration =
        configurationDomain.getCommonConfiguration(orgId, type, key);

    if (commonConfiguration.isEmpty()) {
      throwException(orgId, type, key);
    }
    return INSTANCE.toCommonMasterConfigurationDto(
        configurationDomain.saveCommonConfiguration(
            INSTANCE.toCommonMasterConfiguration(baseRequest)));
  }

  public CommonConfigurationDto deleteCommonConfiguration(String orgId, String type, String key)
      throws PromiseEngineException, CommonServiceException {

    Optional<CommonConfiguration> commonConfiguration =
        configurationDomain.getCommonConfiguration(orgId, type, key);

    if (commonConfiguration.isEmpty()) {
      throwException(orgId, type, key);
    }
    configurationDomain.deleteCommonConfiguration(commonConfiguration.get());
    return INSTANCE.toCommonMasterConfigurationDto(commonConfiguration.get());
  }

  private static void throwException(String orgId, String type, String key)
      throws CommonServiceException {
    logger.error(COMMON_CONFIG_NOT_FOUND_ERROR_MSG);
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(TYPE, FieldError.builder().rejectedValue(type).build());
    errorMap.put(KEY, FieldError.builder().rejectedValue(key).build());
    throw new CommonServiceException(
        COMMON_CONFIG_NOT_FOUND_ERROR_MSG, HttpStatus.NOT_FOUND, 0x1774, errorMap);
  }
}
