package com.hbc.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.hbc")
@EnableJpaRepositories("com.hbc.node.repository")
public class MasterDataNodeApplication {
  public static void main(String[] args) {
    SpringApplication.run(MasterDataNodeApplication.class, args);
  }

  @Bean
  public feign.Logger.Level feignLoggerLevel() {
    return feign.Logger.Level.FULL;
  }
}
