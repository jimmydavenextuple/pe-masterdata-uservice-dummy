package com.hbc.service.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.hbc")
@EnableJpaRepositories("com.hbc.service.inventory")
public class ServiceInventoryApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServiceInventoryApplication.class, args);
  }
}
