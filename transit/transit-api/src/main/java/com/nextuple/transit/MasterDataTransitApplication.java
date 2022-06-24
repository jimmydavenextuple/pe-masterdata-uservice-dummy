package com.nextuple.transit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.nextuple")
public class MasterDataTransitApplication {

  public static void main(String[] args) {
    SpringApplication.run(MasterDataTransitApplication.class, args);
  }
}
