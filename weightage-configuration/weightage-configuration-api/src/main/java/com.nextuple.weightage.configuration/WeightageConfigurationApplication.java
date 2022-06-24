package com.nextuple.weightage.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.nextuple")
public class WeightageConfigurationApplication {

  public static void main(String[] args) {
    SpringApplication.run(WeightageConfigurationApplication.class, args);
  }
}
