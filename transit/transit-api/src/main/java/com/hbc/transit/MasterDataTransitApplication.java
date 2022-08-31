package com.hbc.transit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.hbc")
@EnableJpaRepositories("com.hbc.transit.repository")
public class MasterDataTransitApplication {

  public static void main(String[] args) {
    SpringApplication.run(MasterDataTransitApplication.class, args);
  }
}
