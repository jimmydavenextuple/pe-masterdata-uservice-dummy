/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.userexit.spring.cache.util;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.userexit.domain.UserExitData;
import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import com.nextuple.common.userexit.domain.dto.UserExitMetaDataDto;
import com.nextuple.common.userexit.domain.enums.ExecutionFailureEnum;
import com.nextuple.common.userexit.domain.enums.UserExitTypeEnum;
import com.nextuple.pe.userexit.cache.domain.UEConfigDataCacheKey;
import com.nextuple.pe.userexit.cache.domain.UEConfigDataCacheValue;
import com.nextuple.pe.userexit.cache.domain.UEMetaDataCacheKey;
import com.nextuple.pe.userexit.cache.domain.UEMetaDataCacheValue;
import org.springframework.http.ResponseEntity;

public class TestUtil {
  public UserExitMetaDataDto getUEMetaData() {
    UserExitMetaDataDto userExitMetaData = new UserExitMetaDataDto();
    userExitMetaData.setId(1L);
    userExitMetaData.setName("ExampleExit");
    userExitMetaData.setAppName("PE");
    userExitMetaData.setServiceName("SourcingService");
    userExitMetaData.setExecutionFailureType(ExecutionFailureEnum.SOFT);
    userExitMetaData.setType(UserExitTypeEnum.REGULAR);
    return userExitMetaData;
  }

  public BaseResponse<UserExitConfigDataDto> getUEConfigDataBaseResponse() {
    return BaseResponse.builder()
        .message("Config data found")
        .success(true)
        .payload(getUEConfigData())
        .build();
  }

  public BaseResponse<UserExitMetaDataDto> getUEMetaDataBaseResponse() {
    return BaseResponse.builder()
        .message("Meta data found")
        .success(true)
        .payload(getUEMetaData())
        .build();
  }

  public UserExitConfigDataDto getUEConfigData() {
    UserExitConfigDataDto userExitConfigData = new UserExitConfigDataDto();
    userExitConfigData.setId(1L);
    userExitConfigData.setUserExitName("ExampleExit");
    userExitConfigData.setAppName("PE");
    userExitConfigData.setServiceName("SourcingService");
    userExitConfigData.setOrgId("NEXTUPLE");
    userExitConfigData.setUrl("https://www.nextuple.com");
    return userExitConfigData;
  }

  public UserExitData getUserExitData() {
    UserExitData userExitData = new UserExitData();
    userExitData.setUserExitMetaData(getUEMetaData());
    userExitData.setUserExitConfigData(getUEConfigData());
    return userExitData;
  }

  public ResponseEntity<BaseResponse<String>> getHttpResponse() {

    return ResponseEntity.ok(
        BaseResponse.builder().message("Success").payload("HttpResponse").build());
  }

  public UEConfigDataCacheValue getUEConfigDataCacheValue() {
    return UEConfigDataCacheValue.builder().userExitConfigDataDto(getUEConfigData()).build();
  }

  public UEMetaDataCacheValue getUEMetaDataCacheValue() {
    return UEMetaDataCacheValue.builder().userExitMetaDataDto(getUEMetaData()).build();
  }

  public UEConfigDataCacheKey getUEConfigDataCacheKey() {
    return UEConfigDataCacheKey.builder()
        .orgId("NEXTUPLE")
        .appName("PE")
        .serviceName("SourcingService")
        .userExitName("ExampleExit")
        .build();
  }

  public UEMetaDataCacheKey getUEMetaDataCacheKey() {
    return UEMetaDataCacheKey.builder()
        .serviceName("SourcingService")
        .appName("PE")
        .name("ExampleExit")
        .build();
  }

  //  public BaseResponse<UserExitConfigDataDto> getNamedOptimizationStrategyResponse() {
  //    BaseResponse<NamedOptimizationStrategyResponse> response = new BaseResponse<>();
  //    NamedOptimizationStrategyResponse namedOptimizationStrategyResponse =
  //        NamedOptimizationStrategyResponse.builder()
  //            .orgId("NEXTUPLE")
  //            .groupId("group1")
  //            .id(1L)
  //            .optimizationStrategyName("strategy_name")
  //            .optimizationStrategyDetails("strategy_details")
  //            .build();
  //    response.setPayload(namedOptimizationStrategyResponse);
  //    return response;
  //  }

}
