/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.common.config;

import static com.nextuple.common.constants.CommonConstants.CONFIG_KEY;
import static com.nextuple.common.constants.CommonConstants.ORG_ID;
import static com.nextuple.common.constants.ConfigKeyConstants.CUSTOM_ATTRIBUTES_CONFIG_KEY;
import static com.nextuple.common.constants.ConfigKeyConstants.LINES_CUSTOM_ATTRIBUTES_CONFIG_KEY;
import static com.nextuple.common.constants.ConfigKeyConstants.SERVICE_OPTIONS_CONFIG_KEY;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.configuration.feign.ConfigurationFeign;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Configuration
public class TenantDatabaseConfig {
  @Autowired ConfigurationFeign configurationFeign;
  public static final Gson gson = new Gson();

  public static Gson getGsonObject() {
    return gson;
  }

  public String[] getCurrentTenantServiceOptionsUnmodified() throws CommonServiceException {
    String tenantId = CurrentThreadContext.getLogContext().getTenantId();
    String serviceOptions = fetchServiceOptions(tenantId);
    return Arrays.stream(serviceOptions.split(",")).toArray(String[]::new);
  }

  public String[] getCurrentTenantServiceOptions() throws CommonServiceException {
    String tenantId = CurrentThreadContext.getLogContext().getTenantId();
    String serviceOptions = fetchServiceOptions(tenantId);
    return Arrays.stream(serviceOptions.split(","))
        .map(option -> option.toLowerCase() + "Eligible")
        .toArray(String[]::new);
  }

  public String[] getCurrentTenantServiceOptions(String tenantId) throws CommonServiceException {
    String serviceOptions = fetchServiceOptions(tenantId);
    return Arrays.stream(serviceOptions.split(","))
        .map(option -> option.toLowerCase() + "Eligible")
        .toArray(String[]::new);
  }

  public Map<String, String> getCurrentTenantCustomAttributes(String tenantId)
      throws CommonServiceException {
    Type type = new TypeToken<Map<String, String>>() {}.getType();
    String customAttributes =
        fetchHeaderAndLineLevelCustomAttributes(tenantId, CUSTOM_ATTRIBUTES_CONFIG_KEY);
    return getGsonObject().fromJson(customAttributes, type);
  }

  public Map<String, String> getCurrentTenantLinesCustomAttributes(String tenantId)
      throws CommonServiceException {
    Type type = new TypeToken<Map<String, String>>() {}.getType();
    String lineCustomAttributes =
        fetchHeaderAndLineLevelCustomAttributes(tenantId, LINES_CUSTOM_ATTRIBUTES_CONFIG_KEY);
    return getGsonObject().fromJson(lineCustomAttributes, type);
  }

  public String fetchServiceOptions(String orgId) throws CommonServiceException {
    try {
      TenantConfigdataResponse response =
          configurationFeign
              .getTenantConfigdataByOrgIdAndConfigKey(orgId, SERVICE_OPTIONS_CONFIG_KEY)
              .getPayload();
      return response.getConfigValue();
    } catch (Exception e) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          CONFIG_KEY, FieldError.builder().rejectedValue(SERVICE_OPTIONS_CONFIG_KEY).build());
      throw new CommonServiceException(
          "No configuration data found for service options",
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
  }

  private String fetchHeaderAndLineLevelCustomAttributes(String orgId, String configKey)
      throws CommonServiceException {
    try {
      TenantConfigdataResponse response =
          configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(orgId, configKey).getPayload();
      return response.getConfigValue();
    } catch (Exception e) {
      if (e.getMessage().contains("Tenant configuration data not found")) {
        return "{}";
      }
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CONFIG_KEY, FieldError.builder().rejectedValue(configKey).build());
      throw new CommonServiceException(
          "Error while fetching " + configKey, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }
}
