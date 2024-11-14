/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.configuration.persistence.util;

import com.nextuple.configuration.persistence.domain.ConfigMetadataDomainDto;
import com.nextuple.configuration.persistence.domain.TenantConfigdataDomainDto;
import com.nextuple.configuration.persistence.entity.ConfigMetadataEntity;
import com.nextuple.configuration.persistence.entity.TenantConfigdataEntity;

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

  public ConfigMetadataDomainDto getConfigMetadataPersistenceDomainDto() {
    ConfigMetadataDomainDto configMetadataDomainDto = new ConfigMetadataDomainDto();
    configMetadataDomainDto.setId(CONFIG_METADATA_ID);
    configMetadataDomainDto.setConfigKey(CONFIG_KEY);
    configMetadataDomainDto.setAppName(APP_NAME);
    configMetadataDomainDto.setDefaultConfigValue(DEFAULT_CONFIG_VALUE);
    return configMetadataDomainDto;
  }

  public ConfigMetadataEntity getConfigMetadataEntity() {
    ConfigMetadataEntity configMetadataEntity = new ConfigMetadataEntity();
    configMetadataEntity.setId(CONFIG_METADATA_ID);
    configMetadataEntity.setConfigKey(CONFIG_KEY);
    configMetadataEntity.setAppName(APP_NAME);
    configMetadataEntity.setDefaultConfigValue(DEFAULT_CONFIG_VALUE);
    return configMetadataEntity;
  }

  public TenantConfigdataDomainDto getTenantConfigdataPersistenceDto() {
    TenantConfigdataDomainDto tenantConfigdataDomainDto = new TenantConfigdataDomainDto();
    tenantConfigdataDomainDto.setId(TENANT_CONFIGDATA_ID);
    tenantConfigdataDomainDto.setOrgId(ORG_ID);
    tenantConfigdataDomainDto.setConfigKey(CONFIG_KEY);
    tenantConfigdataDomainDto.setConfigValue(CONFIG_VALUE);
    return tenantConfigdataDomainDto;
  }

  public TenantConfigdataEntity getTenantConfigdataEntity() {
    TenantConfigdataEntity tenantConfigdataEntity = new TenantConfigdataEntity();
    tenantConfigdataEntity.setId(TENANT_CONFIGDATA_ID);
    tenantConfigdataEntity.setOrgId(ORG_ID);
    tenantConfigdataEntity.setConfigKey(CONFIG_KEY);
    tenantConfigdataEntity.setConfigValue(CONFIG_VALUE);
    return tenantConfigdataEntity;
  }
}
