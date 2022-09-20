package com.hbc.promise.sourcing.rule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.hbc")
@EnableJpaRepositories("com.hbc.promise.sourcing.rule")
@EnableFeignClients(basePackages = {"com.hbc"})
public class PromiseSourcingRuleApplication {

  public static void main(String[] args) {
    SpringApplication.run(PromiseSourcingRuleApplication.class, args);
  }
}
