package com.nextuple.pe.userexit.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import com.nextuple.common.userexit.domain.dto.UserExitMetaDataDto;
import com.nextuple.common.userexit.domain.entity.UserExitConfigData;
import com.nextuple.common.userexit.domain.entity.UserExitMetaData;
import com.nextuple.pe.userexit.domain.UserExitDomain;
import com.nextuple.pe.userexit.domain.inbound.CreateConfigDataRequest;
import com.nextuple.pe.userexit.domain.inbound.CreateMetaDataRequest;
import com.nextuple.pe.userexit.domain.inbound.UpdateConfigDataRequest;
import com.nextuple.pe.userexit.domain.inbound.UpdateMetaDataRequest;
import com.nextuple.pe.userexit.domain.mapper.DataMapper;
import com.nextuple.pe.userexit.domain.outbound.ConfigDataResponse;
import com.nextuple.pe.userexit.domain.outbound.MetaDataResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserExitDataService {
  private final UserExitDomain userExitDomain;
  private static final Logger logger = LoggerFactory.getLogger(UserExitDataService.class);
  private static final DataMapper INSTANCE = Mappers.getMapper(DataMapper.class);
  private static final String ID = "id";

  public UserExitMetaDataDto fetchMetaData(String name, String appName, String serviceName)
      throws CommonServiceException {
    Optional<UserExitMetaData> userExitMetaDataOptional =
        userExitDomain.findUEMetaDataByNameAppNameAndServiceName(name, appName, serviceName);

    if (userExitMetaDataOptional.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put("UserExitName", FieldError.builder().rejectedValue(name).build());
      errorMap.put("AppName", FieldError.builder().rejectedValue(appName).build());
      errorMap.put("ServiceName", FieldError.builder().rejectedValue(serviceName).build());
      throw new CommonServiceException(
          "User Exit Meta data not found", HttpStatus.NOT_FOUND, 0x1775, errorMap);
    }
    return INSTANCE.toUserExitMetaDataDto(userExitMetaDataOptional.get());
  }

  public UserExitConfigDataDto fetchConfigData(
      String userExitName, String appName, String orgId, String serviceName)
      throws CommonServiceException {
    Optional<UserExitConfigData> userExitConfigDataOptional =
        userExitDomain.findUEConfigDataByNameAppNameOrgIdAndServiceName(
            userExitName, appName, orgId, serviceName);

    if (userExitConfigDataOptional.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put("UserExitName", FieldError.builder().rejectedValue(userExitName).build());
      errorMap.put("AppName", FieldError.builder().rejectedValue(appName).build());
      errorMap.put("orgId", FieldError.builder().rejectedValue(orgId).build());
      errorMap.put("ServiceName", FieldError.builder().rejectedValue(serviceName).build());
      throw new CommonServiceException(
          "User Exit config data not found", HttpStatus.NOT_FOUND, 0x1776, errorMap);
    }
    return INSTANCE.toUserExitConfigDataDto(userExitConfigDataOptional.get());
  }

  public MetaDataResponse addMetaData(CreateMetaDataRequest createMetaDataRequest)
      throws CommonServiceException {
    logger.debug("-- inside addMetadata service --");
    var metaData = INSTANCE.toUserExitMetaData(createMetaDataRequest);
    var res = userExitDomain.addUEMetaData(metaData);
    return INSTANCE.toUserExitMetaDataResponse(res);
  }

  public ConfigDataResponse addConfigData(CreateConfigDataRequest createConfigDataRequest)
      throws CommonServiceException {
    logger.debug("-- inside addConfigData service --");
    var configData = INSTANCE.toUserExitConfigData(createConfigDataRequest);
    var res = userExitDomain.addUEConfigData(configData);
    return INSTANCE.toUserExitConfigDataResponse(res);
  }

  public MetaDataResponse updateMetaData(
      String name, String appName, String serviceName, UpdateMetaDataRequest updateMetaDataRequest)
      throws CommonServiceException {
    logger.debug("-- inside updateMetadata service --");
    Optional<UserExitMetaData> existingMetaData =
        userExitDomain.findUEMetaDataByNameAppNameAndServiceName(name, appName, serviceName);
    if (existingMetaData.isEmpty()) {
      logger.error("Meta data not found");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(name).build());
      throw new CommonServiceException(
          "User Exit meta data not found", HttpStatus.NOT_FOUND, 0x1776, errorMap);
    }
    INSTANCE.updateMetaDataEntity(updateMetaDataRequest, existingMetaData.get());
    return INSTANCE.toUserExitMetaDataResponse(
        userExitDomain.addUEMetaData(existingMetaData.get()));
  }

  public ConfigDataResponse updateConfigData(
      String userExitName,
      String appName,
      String orgId,
      String serviceName,
      UpdateConfigDataRequest updateConfigDataRequest)
      throws CommonServiceException {
    logger.debug("-- inside updateConfigData service --");
    Optional<UserExitConfigData> existingConfigData =
        userExitDomain.findUEConfigDataByNameAppNameOrgIdAndServiceName(
            userExitName, appName, orgId, serviceName);
    if (existingConfigData.isEmpty()) {
      logger.error("Config data not found");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(userExitName).build());
      throw new CommonServiceException(
          "User Exit config data not found", HttpStatus.NOT_FOUND, 0x1776, errorMap);
    }
    INSTANCE.updateConfigDataEntity(updateConfigDataRequest, existingConfigData.get());
    return INSTANCE.toUserExitConfigDataResponse(
        userExitDomain.addUEConfigData(existingConfigData.get()));
  }
}
