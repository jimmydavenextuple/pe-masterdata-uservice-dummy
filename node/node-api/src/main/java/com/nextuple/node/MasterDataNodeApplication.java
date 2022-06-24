package com.nextuple.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.nextuple")
public class MasterDataNodeApplication {
  public static void main(String[] args) {
    SpringApplication.run(MasterDataNodeApplication.class, args);
  }
}
