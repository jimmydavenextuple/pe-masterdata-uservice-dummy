package com.hbc.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.hbc")
@EnableFeignClients(basePackages = "com.hbc")
public class MasterDataNodeApplication {
  public static void main(String[] args) {
    SpringApplication.run(MasterDataNodeApplication.class, args);
  }

  @Bean
  public feign.Logger.Level feignLoggerLevel() {
    return feign.Logger.Level.FULL;
  }
}
