package com.nextuple.common.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.nextuple")
@EnableJpaRepositories("com.nextuple.common.configuration.repository")
public class CommonConfigurationApplication {
  public static void main(String[] args) {
    SpringApplication.run(CommonConfigurationApplication.class, args);
  }
}
