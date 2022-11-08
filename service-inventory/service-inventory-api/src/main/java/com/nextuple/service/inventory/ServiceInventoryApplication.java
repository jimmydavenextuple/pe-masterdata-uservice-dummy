package com.nextuple.service.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.nextuple")
@EnableJpaRepositories("com.nextuple.service.inventory")
public class ServiceInventoryApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServiceInventoryApplication.class, args);
  }
}
