/*
 *
 *  * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *  *
 *  * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 *  * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 *
 */

package com.nextuple.common.userexit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.userexit.domain.UserExitData;
import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import com.nextuple.common.userexit.domain.dto.UserExitMetaDataDto;
import com.nextuple.common.userexit.domain.enums.UEImplTypeEnum;
import com.nextuple.common.userexit.domain.feign.UEDataFeign;
import com.nextuple.pe.userexit.cache.domain.UEConfigDataCacheKey;
import com.nextuple.pe.userexit.cache.service.UEConfigDataNearCacheService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Data
@ConditionalOnProperty(
    prefix = "userexit",
    name = "enabled",
    havingValue = "True",
    matchIfMissing = false)
public class ApiUEImpl<T, G> implements IUserExit<T, G> {
  @Autowired UEConfigDataNearCacheService ueConfigDataNearCacheService;
  @Autowired UEDataFeign ueDataFeign;
  @Autowired HttpUtil<T, T> httpUtilForPreUE;
  @Autowired HttpUtil<G, G> httpUtilForPostUE;
  @Autowired HttpUtil<T, G> httpUtil;
  @Autowired MeterRegistry meterRegistry;
  @Autowired ApplicationContext applicationContext;

  @Override
  public G invoke(
      T inputData,
      Map<String, Object> customAttributeMap,
      UserExitData userExitData,
      TypeReference<T> inputClazz,
      TypeReference<G> outputClazz)
      throws URISyntaxException, IOException, InterruptedException {
    UserExitMetaDataDto userExitMetaData = userExitData.getUserExitMetaData();
    UserExitConfigDataDto userExitConfigData = userExitData.getUserExitConfigData();

    var timer =
        Timer.builder("userexit." + userExitMetaData.getName())
            .tag("orgId", userExitConfigData.getOrgId())
            .publishPercentiles(0.99, 0.95)
            .publishPercentileHistogram()
            .register(meterRegistry);
    long startTime = System.currentTimeMillis();
    if (UEImplTypeEnum.CLASS_BASED.equals(userExitConfigData.getUeImplType())) {
      return getClassBasedResponse(inputData, customAttributeMap, userExitConfigData);
    }
    if (Objects.nonNull(userExitMetaData.getPreUEName())) {
      inputData = handlePreUE(userExitData, inputData, customAttributeMap, inputClazz);
    }

    G response = httpUtil.makePOSTCall(userExitData, inputData, customAttributeMap, outputClazz);

    if (Objects.nonNull(userExitMetaData.getPostUEName())) {
      response = handlePostUE(userExitData, response, customAttributeMap, outputClazz);
    }
    timer.record(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
    return response;
  }

  private UserExitData fetchConfigData(UserExitData userExitData, String ueName) {
    UserExitMetaDataDto userExitMetaData = userExitData.getUserExitMetaData();
    UserExitConfigDataDto userExitConfigData = userExitData.getUserExitConfigData();

    UEConfigDataCacheKey cacheKey =
        UEConfigDataCacheKey.builder()
            .orgId(userExitConfigData.getOrgId())
            .appName(userExitConfigData.getAppName())
            .serviceName(userExitConfigData.getServiceName())
            .userExitName(ueName)
            .build();

    UserExitConfigDataDto configData =
        ueConfigDataNearCacheService.get(cacheKey).getUserExitConfigDataDto();
    UserExitData userExitDataForUE = new UserExitData();
    userExitDataForUE.setUserExitMetaData(userExitMetaData);
    userExitDataForUE.setUserExitConfigData(configData);
    return userExitDataForUE;
  }

  private G handlePostUE(
      UserExitData userExitData,
      G inputData,
      Map<String, Object> customAttributeMap,
      TypeReference<G> clazz)
      throws URISyntaxException, IOException, InterruptedException {
    UserExitMetaDataDto userExitMetaData = userExitData.getUserExitMetaData();
    UserExitData userExitDataForPostUE =
        fetchConfigData(userExitData, userExitMetaData.getPostUEName());
    if (userExitDataForPostUE.getUserExitConfigData() != null) {
      return httpUtilForPostUE.makePOSTCall(
          userExitDataForPostUE, inputData, customAttributeMap, clazz);
    }
    return null;
  }

  private T handlePreUE(
      UserExitData userExitData,
      T inputData,
      Map<String, Object> customAttributeMap,
      TypeReference<T> clazz)
      throws URISyntaxException, IOException, InterruptedException {
    UserExitMetaDataDto userExitMetaData = userExitData.getUserExitMetaData();
    UserExitData userExitDataForPreUE =
        fetchConfigData(userExitData, userExitMetaData.getPreUEName());
    if (userExitDataForPreUE.getUserExitConfigData() != null) {
      return httpUtilForPreUE.makePOSTCall(
          userExitDataForPreUE, inputData, customAttributeMap, clazz);
    }
    return null;
  }

  private G getClassBasedResponse(
      T inputData,
      Map<String, Object> customAttributeMap,
      UserExitConfigDataDto userExitConfigData) {
    var timer =
        Timer.builder("userexit." + userExitConfigData.getUserExitName() + ".classbased")
            .tag("orgId", userExitConfigData.getOrgId())
            .publishPercentiles(0.99, 0.95)
            .publishPercentileHistogram()
            .register(meterRegistry);
    long startTime = System.currentTimeMillis();
    String fullyQualifiedClassName = userExitConfigData.getUrl();
    String[] packages = fullyQualifiedClassName.split("\\.");
    String beanName = packages[packages.length - 1];
    char firstChar = Character.toLowerCase(beanName.charAt(0));
    String restString = beanName.substring(1);
    beanName = firstChar + restString;
    IClassBasedExit<T, G> classBasedExit =
        applicationContext.getBean(beanName, IClassBasedExit.class);
    G g = classBasedExit.fetchResponse(inputData, customAttributeMap);
    timer.record(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
    return g;
  }
}
