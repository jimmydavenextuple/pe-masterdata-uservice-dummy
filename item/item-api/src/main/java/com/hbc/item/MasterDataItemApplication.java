package com.hbc.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.hbc")
@EnableJpaRepositories("com.hbc.item.repository")
public class MasterDataItemApplication {
  public static void main(String[] args) {
    SpringApplication.run(MasterDataItemApplication.class, args);
  }
}
