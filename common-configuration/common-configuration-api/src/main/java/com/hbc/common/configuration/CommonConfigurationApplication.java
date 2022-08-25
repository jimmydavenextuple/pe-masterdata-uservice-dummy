package com.hbc.common.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.hbc")
public class CommonConfigurationApplication {
  public static void main(String[] args) {
    SpringApplication.run(CommonConfigurationApplication.class, args);
  }
}
