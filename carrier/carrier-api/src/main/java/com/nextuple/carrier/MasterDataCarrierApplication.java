package com.nextuple.carrier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.nextuple")
public class MasterDataCarrierApplication {
  public static void main(String[] args) {
    SpringApplication.run(MasterDataCarrierApplication.class, args);
  }
}
