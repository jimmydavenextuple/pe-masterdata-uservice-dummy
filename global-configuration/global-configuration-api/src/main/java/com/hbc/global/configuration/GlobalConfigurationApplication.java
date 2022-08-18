package com.hbc.global.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.hbc")
public class GlobalConfigurationApplication {
  public static void main(String[] args) {
    SpringApplication.run(GlobalConfigurationApplication.class, args);
  }
}
