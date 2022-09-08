package com.hbc.weightage.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.hbc")
@EnableJpaRepositories("com.hbc.weightage.configuration.domain.repository")
public class WeightageConfigurationApplication {

  public static void main(String[] args) {
    SpringApplication.run(WeightageConfigurationApplication.class, args);
  }
}
