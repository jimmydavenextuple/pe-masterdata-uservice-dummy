package com.hbc.csvdownload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.hbc")
@EnableFeignClients("com.hbc")
public class CsvUtilityApplication {
  public static void main(String[] args) {
    SpringApplication.run(CsvUtilityApplication.class, args);
  }
}
