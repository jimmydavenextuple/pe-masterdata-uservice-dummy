package com.nextuple.transit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.nextuple")
@EnableJpaRepositories("com.nextuple.transit.repository")
@EnableFeignClients(basePackages = {"com.nextuple"})
public class MasterDataTransitApplication {

  public static void main(String[] args) {
    SpringApplication.run(MasterDataTransitApplication.class, args);
  }
}
