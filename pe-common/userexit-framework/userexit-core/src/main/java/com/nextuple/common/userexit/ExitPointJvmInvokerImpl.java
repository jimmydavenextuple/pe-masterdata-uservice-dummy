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
import com.nextuple.common.userexit.domain.dto.ErrorWrapper;
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

/**
 * Implementation of the {@link ExitPointInvoker} interface that uses a JVM-based approach for
 * invoking User Exit points. This service is conditionally enabled based on the application
 * property configuration.
 *
 * <p>The service is activated when the property {@code userexit.enabled} is set to {@code True}. If
 * the property is missing, the service remains disabled.
 *
 * @param <T> The input data type for the User Exit invocation.
 * @param <G> The output data type for the User Exit invocation.
 *     <p>Annotations used in this class:
 *     <ul>
 *       <li>{@code @Service} - Marks this class as a Spring-managed service.
 *       <li>{@code @NoArgsConstructor} - Generates a no-argument constructor.
 *       <li>{@code @AllArgsConstructor} - Generates a constructor with all arguments.
 *       <li>{@code @Data} - Lombok annotation to generate getters, setters, equals, hashCode, and
 *           toString methods.
 *       <li>{@code @ConditionalOnProperty} - Activates this service only when the specified
 *           property conditions are met.
 *     </ul>
 */
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

  /**
   * Fetches User Exit (UE) data for the specified organization, user exit name, application name,
   * and service name. This method retrieves metadata and configuration data for the given
   * parameters.
   *
   * @param orgId The unique identifier of the organization.
   * @param userExitName The name of the user exit.
   * @param appName The name of the application for which the user exit is being fetched.
   * @param serviceName The name of the service for which the user exit is being fetched.
   * @return {@link UserExitData} containing metadata and configuration data for the user exit.
   * @throws CommonServiceException If an error occurs during data retrieval or processing.
   *     <p>The method performs the following operations:
   *     <ul>
   *       <li>Fetches user exit metadata from a near-cache service using the provided keys.
   *       <li>Fetches configuration data if metadata exists.
   *       <li>Returns the assembled {@code UserExitData} object.
   *     </ul>
   *     <p>In case of errors, the method logs the error details, constructs a {@link
   *     CommonServiceException} with error context, and throws it with an appropriate HTTP status
   *     code.
   */
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

  /**
   * Invokes a User Exit (UE) implementation with the provided input data and configuration. This
   * method processes the input data, resolves custom attributes using JSON paths, and delegates the
   * invocation to the appropriate User Exit implementation.
   *
   * @param userExitData Contains metadata and configuration data for the user exit.
   * @param inputData The input data of type {@code T} to be processed by the user exit.
   * @param documentContext A JSON document context used to extract custom attributes based on paths
   *     defined in the user exit configuration.
   * @param inputClazz A {@link TypeReference} for the input data type.
   * @param outputClazz A {@link TypeReference} for the output data type.
   * @return An {@link ErrorWrapper} object of type {@code G} containing the result of the user exit
   *     invocation.
   * @throws URISyntaxException If there is an issue with constructing the URI for the user exit.
   * @throws IOException If there is an I/O error during processing.
   * @throws InterruptedException If the thread executing the user exit is interrupted.
   * @throws ClassNotFoundException If the specified User Exit implementation class is not found.
   * @throws InvocationTargetException If the underlying method of the User Exit implementation
   *     throws an exception.
   * @throws NoSuchMethodException If a required method in the User Exit implementation is not
   *     found.
   * @throws InstantiationException If the User Exit implementation cannot be instantiated.
   * @throws IllegalAccessException If access to the User Exit implementation or its methods is
   *     denied.
   *     <p>The method performs the following steps:
   *     <ul>
   *       <li>Extracts custom attributes by resolving JSON paths from the provided {@code
   *           DocumentContext}.
   *       <li>Fetches the appropriate User Exit implementation based on metadata.
   *       <li>Invokes the User Exit with input data and custom attributes.
   *       <li>Handles specific exceptions such as {@link SocketException} and {@link
   *           CommonServiceException} based on the execution failure type and implementation type
   *           defined in metadata.
   *     </ul>
   *     <p>In case of hard execution failures or unavailability, the method throws appropriate
   *     exceptions like {@link ServiceUnavailableException} or {@link
   *     HardExecutionFailureException}.
   */
  @Override
  public ErrorWrapper<G> invoke(
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
