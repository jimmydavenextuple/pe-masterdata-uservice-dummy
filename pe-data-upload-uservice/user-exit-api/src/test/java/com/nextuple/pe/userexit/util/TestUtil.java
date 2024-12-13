/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.userexit.util;

import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import com.nextuple.common.userexit.domain.dto.UserExitMetaDataDto;
import com.nextuple.common.userexit.domain.entity.UserExitConfigData;
import com.nextuple.common.userexit.domain.entity.UserExitMetaData;
import com.nextuple.common.userexit.domain.enums.ExecutionFailureEnum;
import com.nextuple.common.userexit.domain.enums.UEImplTypeEnum;
import com.nextuple.common.userexit.domain.enums.UserExitTypeEnum;
import com.nextuple.pe.userexit.domain.inbound.CreateConfigDataRequest;
import com.nextuple.pe.userexit.domain.inbound.CreateMetaDataRequest;
import com.nextuple.pe.userexit.domain.inbound.UpdateConfigDataRequest;
import com.nextuple.pe.userexit.domain.inbound.UpdateMetaDataRequest;
import com.nextuple.pe.userexit.domain.outbound.ConfigDataResponse;
import com.nextuple.pe.userexit.domain.outbound.MetaDataResponse;

public class TestUtil {
  public static final String NAME = "GetSourcingRules";
  public static final String APP_NAME = "PE";
  public static final String SERVICE_NAME = "SourcingService";
  public static final String DESCRIPTION = "Changing data priority";
  public static final ExecutionFailureEnum EXECUTION_FAILURE_TYPE = ExecutionFailureEnum.SOFT;
  public static final UserExitTypeEnum TYPE = UserExitTypeEnum.REGULAR;
  public static final String PRE_UE_NAME = "PRE_NXT";
  public static final String POST_UE_NAME = "POST_NXT";
  public static final String URL = "com.nextuple.promise.item.ItemMockProvider";
  public static final UEImplTypeEnum UE_IMPL_TYPE = UEImplTypeEnum.CLASS_BASED;
  public static final String ATTRIBUTE_JSON_PATH =
      "serviceOption:$.serviceOption,orgId:$.orgId,uom:$.orderLines[0].item.unitOfMeasure,lineId:$.orderLines[0].lineId,sessionId:$.sessionId,pageName:$.pageName";
  public static final String ORG_ID = "SIGNET";
  public static final String USER_EXIT_NAME = "GetItemDetails";

  public UserExitMetaData getUserExitMetaData() {
    UserExitMetaData metaData = new UserExitMetaData();
    metaData.setName("GetSourcing");
    metaData.setAppName("PE");
    metaData.setServiceName("Sourcing");
    metaData.setDescription("Changing data priority");
    metaData.setExecutionFailureType(ExecutionFailureEnum.SOFT);
    metaData.setType(UserExitTypeEnum.REGULAR);
    return metaData;
  }

  public UserExitConfigData getUserExitConfigData() {
    UserExitConfigData configData = new UserExitConfigData();
    configData.setUserExitName("GetSourcing");
    configData.setAppName("PE");
    configData.setServiceName("Sourcing");
    configData.setUeImplType(UEImplTypeEnum.REST);
    configData.setPropagateError(true);
    return configData;
  }

  public CreateMetaDataRequest createMetaDataRequest() {
    return CreateMetaDataRequest.builder()
        .name("GetSourcing")
        .serviceName("Sourcing")
        .appName("PE")
        .build();
  }

  public CreateConfigDataRequest createConfigDataRequest() {
    return CreateConfigDataRequest.builder()
        .userExitName("GetSourcing")
        .appName("PE")
        .orgId("SIGNET")
        .serviceName("Sourcing")
        .build();
  }

  public MetaDataResponse metaDataResponse() {
    return MetaDataResponse.builder()
        .name("GetSourcing")
        .serviceName("Sourcing")
        .appName("PE")
        .build();
  }

  public ConfigDataResponse configDataResponse() {
    return ConfigDataResponse.builder()
        .userExitName("GetSourcing")
        .orgId("SIGNET")
        .serviceName("Sourcing")
        .appName("PE")
        .build();
  }

  public MetaDataResponse getUpdatedMetaDataResponse() {
    return MetaDataResponse.builder()
        .id(1L)
        .name("GetSourcing")
        .appName("PE")
        .serviceName("Sourcing")
        .build();
  }

  public UpdateMetaDataRequest getUpdateMetaDataRequest(
      String description,
      ExecutionFailureEnum executionFailureType,
      UserExitTypeEnum type,
      String preUEName,
      String postUEName) {
    return UpdateMetaDataRequest.builder()
        .description(description)
        .executionFailureType(executionFailureType)
        .type(type)
        .preUEName(preUEName)
        .postUEName(postUEName)
        .build();
  }

  public UpdateConfigDataRequest getUpdateConfigDataRequest(
      String url, UEImplTypeEnum ueImplTypeEnum, String attributeJsonPath) {
    return UpdateConfigDataRequest.builder()
        .url(url)
        .ueImplType(ueImplTypeEnum)
        .attributeJsonPath(attributeJsonPath)
        .propagateError(true)
        .build();
  }

  public ConfigDataResponse getUpdatedConfigDataResponse() {
    return ConfigDataResponse.builder()
        .id(1L)
        .userExitName("GetSourcing")
        .appName("PE")
        .orgId("SIGNET")
        .serviceName("Sourcing")
        .build();
  }

  public UserExitMetaDataDto getUserExitMetaDataDto() {
    UserExitMetaDataDto metaDataDto = new UserExitMetaDataDto();
    metaDataDto.setName("GetSourcing");
    metaDataDto.setAppName("PE");
    metaDataDto.setServiceName("Sourcing");
    metaDataDto.setDescription("Changing data priority");
    metaDataDto.setExecutionFailureType(ExecutionFailureEnum.SOFT);
    metaDataDto.setType(UserExitTypeEnum.REGULAR);
    return metaDataDto;
  }

  public UserExitConfigDataDto getUserExitConfigDataDto() {
    UserExitConfigDataDto configDataDto = new UserExitConfigDataDto();
    configDataDto.setUserExitName("GetSourcing");
    configDataDto.setAppName("PE");
    configDataDto.setServiceName("Sourcing");
    configDataDto.setUeImplType(UEImplTypeEnum.REST);
    configDataDto.setPropagateError(true);
    return configDataDto;
  }
}
