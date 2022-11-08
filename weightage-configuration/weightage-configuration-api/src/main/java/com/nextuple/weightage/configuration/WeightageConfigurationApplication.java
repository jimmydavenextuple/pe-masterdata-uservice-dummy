package com.nextuple.weightage.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.nextuple")
@EnableJpaRepositories("com.nextuple.weightage.configuration.domain.repository")
public class WeightageConfigurationApplication {

  public static void main(String[] args) {
    SpringApplication.run(WeightageConfigurationApplication.class, args);
  }
}
