package com.hbc.nodecarrier.data.upload.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.hbc")
@EnableFeignClients(basePackages = {"com.hbc"})
@Slf4j
public class NodeCarrierDataUploadUtilityApplication {

  public static void main(String[] args) {

    log.debug("Starting service with args...");
    SpringApplication.run(NodeCarrierDataUploadUtilityApplication.class, args);
    log.debug("Service started successfully!");
  }
}
