package com.hbc.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.hbc")
public class MasterDataItemApplication {
  public static void main(String[] args) {
    SpringApplication.run(MasterDataItemApplication.class, args);
  }
}
