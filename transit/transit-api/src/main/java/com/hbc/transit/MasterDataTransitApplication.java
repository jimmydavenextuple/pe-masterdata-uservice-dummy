package com.hbc.transit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.hbc")
public class MasterDataTransitApplication {

  public static void main(String[] args) {
    SpringApplication.run(MasterDataTransitApplication.class, args);
  }
}
