package com.hbc.jobs.dashboard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan("com.hbc")
@EnableFeignClients(basePackages = {"com.hbc"})
public class JobsDashboardServiceApplication {

  public static void main(String[] args) {
    log.debug("Starting service with args...");
    SpringApplication.run(JobsDashboardServiceApplication.class);
    log.debug("Service started successfully!");
  }
}
