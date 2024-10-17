/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.config;

import com.nextuple.jobs.framework.common.domain.pojo.StorageConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

  @Value("${dataupload.signed-url-expiry-minutes}")
  private Integer signedUrlExpiryMinutes;

  @Value("${dataupload.bucket-name}")
  private String containerName;

  @Value("${dataupload.type}")
  private String storageType;

  @Bean
  public StorageConfigProperties azureStorageProperties() {
    StorageConfigProperties storageConfigProperties = new StorageConfigProperties();
    storageConfigProperties.setStorageType(storageType);
    storageConfigProperties.setSignedUrlExpiryMinutes(signedUrlExpiryMinutes);
    storageConfigProperties.setContainerName(containerName);
    return storageConfigProperties;
  }
}
