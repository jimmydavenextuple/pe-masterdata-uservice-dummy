/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.config;

import static com.nextuple.common.constants.CommonConstants.CONFIG_KEY;
import static com.nextuple.common.constants.CommonConstants.ORG_ID;
import static com.nextuple.common.constants.ConfigKeyConstants.NODE_TYPES_CONFIG_KEY;
import static com.nextuple.common.constants.ConfigKeyConstants.SERVICE_OPTIONS_CONFIG_KEY;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.configuration.feign.ConfigurationFeign;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Configuration
public class NodeTenantBasedDBConfig {

  @Autowired ConfigurationFeign configurationFeign;

  public Set<String> getNodeTypes(String orgId) throws CommonServiceException {
    try {
      TenantConfigdataResponse response =
          configurationFeign
              .getTenantConfigdataByOrgIdAndConfigKey(orgId, NODE_TYPES_CONFIG_KEY)
              .getPayload();
      String[] values = response.getConfigValue().split("\\s*,\\s*");
      return new HashSet<>(Arrays.asList(values));
    } catch (Exception e) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          CONFIG_KEY, FieldError.builder().rejectedValue(SERVICE_OPTIONS_CONFIG_KEY).build());
      throw new CommonServiceException(
          "No configuration data found for node types", HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }
}
