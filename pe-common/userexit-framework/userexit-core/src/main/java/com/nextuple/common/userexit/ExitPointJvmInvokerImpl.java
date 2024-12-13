package com.nextuple.common.userexit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jayway.jsonpath.DocumentContext;
import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.HardExecutionFailureException;
import com.nextuple.common.exception.ServiceUnavailableException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.userexit.domain.UserExitData;
import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import com.nextuple.common.userexit.domain.dto.UserExitMetaDataDto;
import com.nextuple.common.userexit.domain.enums.ExecutionFailureEnum;
import com.nextuple.common.userexit.domain.enums.UEImplTypeEnum;
import com.nextuple.common.userexit.domain.feign.UEDataFeign;
import com.nextuple.pe.userexit.cache.domain.UEConfigDataCacheKey;
import com.nextuple.pe.userexit.cache.domain.UEMetaDataCacheKey;
import com.nextuple.pe.userexit.cache.service.UEConfigDataNearCacheService;
import com.nextuple.pe.userexit.cache.service.UEMetaDataNearCacheService;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Data
@ConditionalOnProperty(
    prefix = "userexit",
    name = "enabled",
    havingValue = "True",
    matchIfMissing = false)
public class ExitPointJvmInvokerImpl<T, G> implements ExitPointInvoker<T, G> {
  public static final String USER_EXITNAME = ", userExitname: ";
  public static final String APP_NAME = ", appName: ";
  public static final String SERVICE_NAME = ", serviceName: ";
  @Autowired UEDataFeign ueDataFeign;
  @Autowired UEMetaDataNearCacheService ueMetaDataNearCacheService;
  @Autowired UEConfigDataNearCacheService ueConfigDataNearCacheService;
  @Autowired UserExitFactory<T, G> userExitFactory;

  private static final Logger logger = LoggerFactory.getLogger(ExitPointJvmInvokerImpl.class);

  @Override
  public UserExitData fetchUEData(
      String orgId, String userExitName, String appName, String serviceName)
      throws CommonServiceException {
    try {
      if (logger.isDebugEnabled()) {
        logger.debug(
            "Fetching user exit data for orgId: "
                + orgId
                + USER_EXITNAME
                + userExitName
                + APP_NAME
                + appName
                + SERVICE_NAME
                + serviceName
                + "Tenant id = "
                + CurrentThreadContext.getLogContext().getTenantId());
      }
      UserExitData userExitData = new UserExitData();
      UEMetaDataCacheKey ueMetaDataCacheKey =
          UEMetaDataCacheKey.builder()
              .appName(appName)
              .serviceName(serviceName)
              .name(userExitName)
              .build();

      UserExitMetaDataDto metaData =
          ueMetaDataNearCacheService.get(ueMetaDataCacheKey).getUserExitMetaDataDto();
      userExitData.setUserExitMetaData(metaData);
      if (Objects.nonNull(metaData)) {
        UserExitConfigDataDto configData;
        configData = getUserExitConfigData(orgId, userExitName, appName, serviceName);
        userExitData.setUserExitConfigData(configData);
      }
      return userExitData;
    } catch (Exception e) {
      Map<String, FieldError> fieldInfo = new HashMap<>();
      fieldInfo.put("orgId", FieldError.builder().rejectedValue(orgId).build());
      fieldInfo.put("userExitName", FieldError.builder().rejectedValue(userExitName).build());
      fieldInfo.put("appName", FieldError.builder().rejectedValue(appName).build());
      fieldInfo.put("serviceName", FieldError.builder().rejectedValue(serviceName).build());
      logger.error(
          "Error while fetching userExit data for orgId: "
              + orgId
              + USER_EXITNAME
              + userExitName
              + APP_NAME
              + appName
              + SERVICE_NAME
              + serviceName
              + "\n. Error is: "
              + e);
      throw new CommonServiceException(
          "Error while fetching user exit data.",
          e,
          HttpStatus.INTERNAL_SERVER_ERROR,
          0x1778,
          fieldInfo);
    }
  }

  private UserExitConfigDataDto getUserExitConfigData(
      String orgId, String userExitName, String appName, String serviceName) {
    UserExitConfigDataDto configData;
    try {
      UEConfigDataCacheKey cacheKey =
          UEConfigDataCacheKey.builder()
              .orgId(orgId)
              .appName(appName)
              .serviceName(serviceName)
              .userExitName(userExitName)
              .build();

      configData = ueConfigDataNearCacheService.get(cacheKey).getUserExitConfigDataDto();
    } catch (Exception e) {
      logger.error(
          "Error while fetching user Exit config data for orgId: "
              + orgId
              + USER_EXITNAME
              + userExitName
              + APP_NAME
              + appName
              + SERVICE_NAME
              + serviceName);
      configData = null;
    }
    return configData;
  }

  @Override
  public G invoke(
      UserExitData userExitData,
      T inputData,
      DocumentContext documentContext,
      TypeReference<T> inputClazz,
      TypeReference<G> outputClazz)
      throws URISyntaxException,
          IOException,
          InterruptedException,
          ClassNotFoundException,
          InvocationTargetException,
          NoSuchMethodException,
          InstantiationException,
          IllegalAccessException {

    UserExitMetaDataDto userExitMetaData = userExitData.getUserExitMetaData();
    UserExitConfigDataDto userExitConfigData = userExitData.getUserExitConfigData();

    Map<String, String> attributeJsonPathMap = getAttributeJsonPathMap(userExitConfigData);
    Map<String, Object> customAttributeMap = new HashMap<>();
    for (Entry<String, String> entry : attributeJsonPathMap.entrySet()) {
      Object obj = documentContext.read(entry.getValue());
      customAttributeMap.put(entry.getKey(), obj);
    }
    IUserExit<T, G> userExit = userExitFactory.getUE(userExitMetaData.getType());
    try {
      return userExit.invoke(inputData, customAttributeMap, userExitData, inputClazz, outputClazz);
    } catch (SocketException e) {
      if (ExecutionFailureEnum.HARD.equals(userExitMetaData.getExecutionFailureType())
          && UEImplTypeEnum.REST.equals(userExitConfigData.getUeImplType()))
        throw new ServiceUnavailableException();
      throw e;
    } catch (CommonServiceException e) {
      if (ExecutionFailureEnum.HARD.equals(userExitMetaData.getExecutionFailureType())
          && UEImplTypeEnum.REST.equals(userExitConfigData.getUeImplType()))
        throw new HardExecutionFailureException(e.getMessage());
      throw new IOException(e.getMessage());
    }
  }

  private static Map<String, String> getAttributeJsonPathMap(
      UserExitConfigDataDto userExitConfigData) {
    Map<String, String> attributeJsonPathMap = new HashMap<>();
    String attributeJsonPathString = userExitConfigData.getAttributeJsonPath();
    if (!StringUtils.hasLength(attributeJsonPathString)) return attributeJsonPathMap;
    String[] attributeJsonPathPairs = attributeJsonPathString.split(",");
    for (String attributeJsonPathPair : attributeJsonPathPairs) {
      List<String> attributeJsonPathList = Arrays.asList(attributeJsonPathPair.split(":"));
      if (attributeJsonPathList.size() < 2) continue;
      String key = attributeJsonPathList.get(0).trim();
      String value = attributeJsonPathList.get(1).trim();
      attributeJsonPathMap.put(key, value);
    }
    return attributeJsonPathMap;
  }
}
