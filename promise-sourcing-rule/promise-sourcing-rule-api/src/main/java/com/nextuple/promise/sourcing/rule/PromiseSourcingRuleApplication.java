package com.nextuple.promise.sourcing.rule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.nextuple")
@EnableJpaRepositories("com.nextuple.promise.sourcing.rule")
@EnableFeignClients(basePackages = {"com.nextuple"})
public class PromiseSourcingRuleApplication {

  public static void main(String[] args) {
    SpringApplication.run(PromiseSourcingRuleApplication.class, args);
  }
}
