package com.hbc.dataupload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.hbc")
@EnableFeignClients(basePackages = {"com.hbc"})
@EnableJpaRepositories("com.hbc.jobs.consumers.domain.repository")
@EntityScan("com.hbc.jobs.consumers.domain.entity")
@Slf4j
public class DataUploadApplication {

  public static void main(String[] args) {

    log.debug("Starting service with args...");
    SpringApplication.run(DataUploadApplication.class, args);
    log.debug("Service started successfully!");
  }
}
