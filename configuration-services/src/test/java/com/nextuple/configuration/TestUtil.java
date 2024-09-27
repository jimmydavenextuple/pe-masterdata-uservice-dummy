/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.configuration;

import com.nextuple.configuration.domain.entity.ConfigMetadataEntity;
import com.nextuple.configuration.domain.entity.TenantConfigdataEntity;
import com.nextuple.configuration.inbound.ConfigMetadataRequest;
import com.nextuple.configuration.inbound.ConfigMetadataUpdateRequest;
import com.nextuple.configuration.inbound.TenantConfigdataRequest;
import com.nextuple.configuration.inbound.TenantConfigdataUpdateRequest;
import com.nextuple.configuration.outbound.ConfigMetadataResponse;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;

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

  public ConfigMetadataEntity getConfigMetadataEntity() {
    ConfigMetadataEntity configMetadataEntity = new ConfigMetadataEntity();
    configMetadataEntity.setId(CONFIG_METADATA_ID);
    configMetadataEntity.setConfigKey(CONFIG_KEY);
    configMetadataEntity.setAppName(APP_NAME);
    configMetadataEntity.setDefaultConfigValue(DEFAULT_CONFIG_VALUE);
    return configMetadataEntity;
  }

  public ConfigMetadataEntity getUpdatedConfigMetadataEntity() {
    ConfigMetadataEntity configMetadataEntity = new ConfigMetadataEntity();
    configMetadataEntity.setId(CONFIG_METADATA_ID);
    configMetadataEntity.setConfigKey(CONFIG_KEY);
    configMetadataEntity.setAppName(APP_NAME);
    configMetadataEntity.setDefaultConfigValue(DEFAULT_CONFIG_VALUE_2);
    return configMetadataEntity;
  }

  public ConfigMetadataRequest getConfigMetadataRequest() {
    return ConfigMetadataRequest.builder()
        .configKey(CONFIG_KEY)
        .appName(APP_NAME)
        .defaultConfigValue(DEFAULT_CONFIG_VALUE)
        .build();
  }

  public ConfigMetadataResponse getConfigMetadataResponse() {
    return ConfigMetadataResponse.builder()
        .id(CONFIG_METADATA_ID)
        .configKey(CONFIG_KEY)
        .appName(APP_NAME)
        .defaultConfigValue(DEFAULT_CONFIG_VALUE)
        .build();
  }

  public ConfigMetadataUpdateRequest getConfigMetadataUpdateRequest() {
    return ConfigMetadataUpdateRequest.builder().defaultConfigValue(DEFAULT_CONFIG_VALUE_2).build();
  }

  public TenantConfigdataEntity getTenantConfigdataEntity() {
    TenantConfigdataEntity tenantConfigdataEntity = new TenantConfigdataEntity();
    tenantConfigdataEntity.setId(TENANT_CONFIGDATA_ID);
    tenantConfigdataEntity.setOrgId(ORG_ID);
    tenantConfigdataEntity.setConfigKey(CONFIG_KEY);
    tenantConfigdataEntity.setConfigValue(CONFIG_VALUE);
    return tenantConfigdataEntity;
  }

  public TenantConfigdataEntity getUpdatedConfigdataEntity() {
    TenantConfigdataEntity tenantConfigdataEntity = new TenantConfigdataEntity();
    tenantConfigdataEntity.setId(TENANT_CONFIGDATA_ID);
    tenantConfigdataEntity.setOrgId(ORG_ID);
    tenantConfigdataEntity.setConfigKey(CONFIG_KEY);
    tenantConfigdataEntity.setConfigValue(CONFIG_VALUE_2);
    return tenantConfigdataEntity;
  }

  public TenantConfigdataRequest getTenantConfigdataRequest() {
    return TenantConfigdataRequest.builder()
        .orgId(ORG_ID)
        .configKey(CONFIG_KEY)
        .configValue(CONFIG_VALUE)
        .build();
  }

  public TenantConfigdataResponse getTenantConfigdataResponse() {
    return TenantConfigdataResponse.builder()
        .id(TENANT_CONFIGDATA_ID)
        .orgId(ORG_ID)
        .configKey(CONFIG_KEY)
        .configValue(CONFIG_VALUE)
        .build();
  }

  public TenantConfigdataUpdateRequest getTenantConfigdataUpdateRequest() {
    return TenantConfigdataUpdateRequest.builder().configValue(CONFIG_VALUE_2).build();
  }
}
