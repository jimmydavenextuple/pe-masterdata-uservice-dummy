package com.hbc.carrier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.hbc")
@EnableJpaRepositories("com.hbc.carrier.repository")
public class MasterDataCarrierApplication {
  public static void main(String[] args) {
    SpringApplication.run(MasterDataCarrierApplication.class, args);
  }
}
