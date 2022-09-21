package com.hbc.node.carrier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.hbc")
@EnableJpaRepositories("com.hbc.node.carrier.repository")
@EnableFeignClients(basePackages = {"com.hbc"})
public class MasterDataNodeCarrierApplication {
  public static void main(String[] args) {
    SpringApplication.run(MasterDataNodeCarrierApplication.class, args);
  }
}
