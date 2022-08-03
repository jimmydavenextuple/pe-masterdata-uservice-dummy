package com.hbc.csvdownload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.hbc")
public class CsvUtilityApplication {
  public static void main(String[] args) {
    SpringApplication.run(CsvUtilityApplication.class, args);
  }
}
