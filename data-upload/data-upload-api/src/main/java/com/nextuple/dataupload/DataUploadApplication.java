package com.nextuple.dataupload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.nextuple")
@EnableFeignClients(basePackages = {"com.nextuple"})
@EnableJpaRepositories(
    basePackages = {
      "com.nextuple.jobs.consumers.domain.repository",
      "com.nextuple.jobs.dashboard.repository"
    })
@EntityScan(
    basePackages = {
      "com.nextuple.jobs.consumers.domain.entity",
      "com.nextuple.jobs.dashboard.domain.entity"
    })
@EnableScheduling
@Slf4j
public class DataUploadApplication {

  public static void main(String[] args) {

    log.debug("Starting service with args...");
    SpringApplication.run(DataUploadApplication.class, args);
    log.debug("Service started successfully!");
  }
}
