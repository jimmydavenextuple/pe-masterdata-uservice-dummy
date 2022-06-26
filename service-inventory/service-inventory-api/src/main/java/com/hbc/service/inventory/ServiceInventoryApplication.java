package com.hbc.service.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.hbc")
public class ServiceInventoryApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServiceInventoryApplication.class, args);
  }
}
