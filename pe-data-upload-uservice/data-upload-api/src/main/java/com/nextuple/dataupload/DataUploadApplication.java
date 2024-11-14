/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload;

import com.nextuple.pe.webhook.config.WebhookConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.nextuple")
@EnableFeignClients(basePackages = {"com.nextuple"})
@EnableConfigurationProperties({WebhookConfiguration.class})
@EnableJpaRepositories(
    basePackages = {
      "com.nextuple.jobs.consumers.domain.repository",
      "com.nextuple.jobs.dashboard.repository",
      "com.nextuple.pe.userexit.repository"
    })
@EntityScan(
    basePackages = {
      "com.nextuple.jobs.consumers.domain.entity",
      "com.nextuple.jobs.dashboard.domain.entity",
      "com.nextuple.common"
    })
@EnableScheduling
@Slf4j
public class DataUploadApplication {

  public static void main(String[] args) {

    log.debug("Starting service with args...");
    SpringApplication.run(DataUploadApplication.class);
    log.debug("Service started successfully!");
  }
}
