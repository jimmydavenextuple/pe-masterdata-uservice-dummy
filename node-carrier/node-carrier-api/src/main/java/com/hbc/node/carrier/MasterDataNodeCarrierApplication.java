package com.hbc.node.carrier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.hbc")
public class MasterDataNodeCarrierApplication {
  public static void main(String[] args) {
    SpringApplication.run(MasterDataNodeCarrierApplication.class, args);
  }
}
