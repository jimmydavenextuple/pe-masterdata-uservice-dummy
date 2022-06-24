package com.nextuple.promise.sourcing.rule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.nextuple")
public class PromiseSourcingRuleApplication {

  public static void main(String[] args) {
    SpringApplication.run(PromiseSourcingRuleApplication.class, args);
  }
}
