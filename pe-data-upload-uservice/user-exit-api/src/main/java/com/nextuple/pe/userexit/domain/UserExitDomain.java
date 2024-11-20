package com.nextuple.pe.userexit.domain;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.userexit.domain.entity.UserExitConfigData;
import com.nextuple.common.userexit.domain.entity.UserExitMetaData;
import com.nextuple.pe.userexit.repository.UserExitConfigDataRepository;
import com.nextuple.pe.userexit.repository.UserExitMetaDataRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserExitDomain {
  private final UserExitMetaDataRepository userExitMetaDataRepository;
  private final UserExitConfigDataRepository userExitConfigDataRepository;

  private static final Logger logger = LoggerFactory.getLogger(UserExitDomain.class);

  public Optional<UserExitMetaData> findUEMetaDataByNameAppNameAndServiceName(
      String name, String appName, String serviceName) throws CommonServiceException {
    try {
      return userExitMetaDataRepository.findByNameAndAppNameAndServiceName(
          name, appName, serviceName);
    } catch (Exception e) {
      throw new CommonServiceException(
          "Error while fetching user exit meta data",
          HttpStatus.INTERNAL_SERVER_ERROR,
          0x1775,
          Map.of(
              "UserExitName",
              FieldError.builder().rejectedValue(name).build(),
              "AppName",
              FieldError.builder().rejectedValue(appName).build(),
              "ServiceName",
              FieldError.builder().rejectedValue(serviceName).build()));
    }
  }

  public Optional<UserExitConfigData> findUEConfigDataByNameAppNameOrgIdAndServiceName(
      String userExitName, String appName, String orgId, String serviceName)
      throws CommonServiceException {
    try {
      return userExitConfigDataRepository.findByUserExitNameAndAppNameAndOrgIdAndServiceName(
          userExitName, appName, orgId, serviceName);
    } catch (Exception e) {
      throw new CommonServiceException(
          "Error while fetching user exit meta data",
          HttpStatus.INTERNAL_SERVER_ERROR,
          0x1775,
          Map.of(
              "UserExitName",
              FieldError.builder().rejectedValue(userExitName).build(),
              "AppName",
              FieldError.builder().rejectedValue(appName).build(),
              "OrgId",
              FieldError.builder().rejectedValue(orgId).build(),
              "ServiceName",
              FieldError.builder().rejectedValue(serviceName).build()));
    }
  }

  public UserExitMetaData addUEMetaData(UserExitMetaData metaData) throws CommonServiceException {
    logger.debug("-- inside userExit domain --");
    try {
      return userExitMetaDataRepository.save(metaData);
    } catch (Exception e) {
      throw new CommonServiceException(
          "Unable to add meta data",
          e,
          HttpStatus.INTERNAL_SERVER_ERROR,
          0x1776,
          Map.of("MetaData", FieldError.builder().rejectedValue(metaData).build()));
    }
  }

  public UserExitConfigData addUEConfigData(UserExitConfigData configData)
      throws CommonServiceException {
    logger.debug("-- inside userExit domain --");
    try {
      return userExitConfigDataRepository.save(configData);
    } catch (Exception e) {
      throw new CommonServiceException(
          "Unable to add config data",
          e,
          HttpStatus.INTERNAL_SERVER_ERROR,
          0x1776,
          Map.of("ConfigData", FieldError.builder().rejectedValue(configData).build()));
    }
  }
}
