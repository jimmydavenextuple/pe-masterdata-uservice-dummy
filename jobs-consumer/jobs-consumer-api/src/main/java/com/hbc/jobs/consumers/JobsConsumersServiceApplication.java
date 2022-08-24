package com.hbc.jobs.consumers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan("com.hbc")
@EnableFeignClients(basePackages = {"com.hbc"})
public class JobsConsumersServiceApplication {

  public static void main(String[] args) {
    log.debug("Starting service with args...");
    SpringApplication.run(JobsConsumersServiceApplication.class);
    log.debug("Service started successfully!");
  }
}
