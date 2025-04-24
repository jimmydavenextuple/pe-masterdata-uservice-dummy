/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.configuration.inbound.ConfigMetadataRequest;
import com.nextuple.configuration.inbound.ConfigMetadataUpdateRequest;
import com.nextuple.configuration.inbound.TenantConfigdataBaseRequest;
import com.nextuple.configuration.inbound.TenantConfigdataRequest;
import com.nextuple.configuration.outbound.ConfigMetadataResponse;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import com.nextuple.configuration.persistence.domain.ConfigMetadataDomainDto;
import com.nextuple.configuration.persistence.domain.TenantConfigdataDomainDto;

public class TestUtil {
  public static final String ORG_ID = "ABC";
  public static final String ORG_ID_2 = "XYZ";
  public static final String CONFIG_KEY = "custom-configkey";
  public static final String DEFAULT_CONFIG_VALUE = "SDND,EXPRESS";
  public static final String DEFAULT_CONFIG_VALUE_2 = "CustomEvent:false";
  public static final Long CONFIG_METADATA_ID = 1L;
  public static final Long TENANT_CONFIGDATA_ID = 1L;
  public static final String APP_NAME = "PE";

  public static final String CONFIG_VALUE = "SDND,EXPRESS,STANDARD";
  public static final String CONFIG_VALUE_2 = "SDND";
  public static final JsonNode CUSTOM_ATTRIBUTES =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");

  public ConfigMetadataDomainDto getConfigMetadataDomainDto() {
    ConfigMetadataDomainDto configMetadataDomainDto = new ConfigMetadataDomainDto();
    configMetadataDomainDto.setId(CONFIG_METADATA_ID);
    configMetadataDomainDto.setConfigKey(CONFIG_KEY);
    configMetadataDomainDto.setAppName(APP_NAME);
    configMetadataDomainDto.setDefaultConfigValue(DEFAULT_CONFIG_VALUE);
    configMetadataDomainDto.setCustomAttributes(CUSTOM_ATTRIBUTES);
    return configMetadataDomainDto;
  }

  public ConfigMetadataDomainDto getUpdatedConfigMetadataDomainDto() {
    ConfigMetadataDomainDto configMetadataDomainDto = new ConfigMetadataDomainDto();
    configMetadataDomainDto.setId(CONFIG_METADATA_ID);
    configMetadataDomainDto.setConfigKey(CONFIG_KEY);
    configMetadataDomainDto.setAppName(APP_NAME);
    configMetadataDomainDto.setDefaultConfigValue(DEFAULT_CONFIG_VALUE_2);
    configMetadataDomainDto.setCustomAttributes(CUSTOM_ATTRIBUTES);
    return configMetadataDomainDto;
  }

  public ConfigMetadataRequest getConfigMetadataRequest() {
    return ConfigMetadataRequest.builder()
        .configKey(CONFIG_KEY)
        .appName(APP_NAME)
        .defaultConfigValue(DEFAULT_CONFIG_VALUE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public ConfigMetadataResponse getConfigMetadataResponse() {
    return ConfigMetadataResponse.builder()
        .id(CONFIG_METADATA_ID)
        .configKey(CONFIG_KEY)
        .appName(APP_NAME)
        .defaultConfigValue(DEFAULT_CONFIG_VALUE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public ConfigMetadataUpdateRequest getConfigMetadataUpdateRequest() {
    return ConfigMetadataUpdateRequest.builder()
        .defaultConfigValue(DEFAULT_CONFIG_VALUE_2)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public TenantConfigdataDomainDto getTenantConfigdataDomainDto() {
    TenantConfigdataDomainDto tenantConfigdataDomainDto = new TenantConfigdataDomainDto();
    tenantConfigdataDomainDto.setId(TENANT_CONFIGDATA_ID);
    tenantConfigdataDomainDto.setOrgId(ORG_ID);
    tenantConfigdataDomainDto.setConfigKey(CONFIG_KEY);
    tenantConfigdataDomainDto.setConfigValue(CONFIG_VALUE);
    tenantConfigdataDomainDto.setCustomAttributes(CUSTOM_ATTRIBUTES);
    return tenantConfigdataDomainDto;
  }

  public TenantConfigdataDomainDto getUpdatedTenantConfigdataDomainDto() {
    TenantConfigdataDomainDto tenantConfigdataDomainDto = new TenantConfigdataDomainDto();
    tenantConfigdataDomainDto.setId(TENANT_CONFIGDATA_ID);
    tenantConfigdataDomainDto.setOrgId(ORG_ID);
    tenantConfigdataDomainDto.setConfigKey(CONFIG_KEY);
    tenantConfigdataDomainDto.setConfigValue(CONFIG_VALUE_2);
    tenantConfigdataDomainDto.setCustomAttributes(CUSTOM_ATTRIBUTES);
    return tenantConfigdataDomainDto;
  }

  public TenantConfigdataRequest getTenantConfigdataRequest() {
    return TenantConfigdataRequest.builder()
        .orgId(ORG_ID)
        .configKey(CONFIG_KEY)
        .configValue(CONFIG_VALUE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public TenantConfigdataResponse getTenantConfigdataResponse() {
    return TenantConfigdataResponse.builder()
        .id(TENANT_CONFIGDATA_ID)
        .orgId(ORG_ID)
        .configKey(CONFIG_KEY)
        .configValue(CONFIG_VALUE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public TenantConfigdataBaseRequest getTenantConfigdataUpdateRequest() {
    return TenantConfigdataBaseRequest.builder()
        .configValue(CONFIG_VALUE_2)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }
}
