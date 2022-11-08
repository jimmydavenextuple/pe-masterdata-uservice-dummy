package com.nextuple.fsa.node;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.nextuple")
@EnableFeignClients(basePackages = {"com.nextuple"})
@Slf4j
public class NodeFSASyncApplication {
  public static void main(String[] args) {

    log.debug("Starting service with args...");
    SpringApplication.run(NodeFSASyncApplication.class, args);
    log.debug("Service started successfully!");
  }
}
