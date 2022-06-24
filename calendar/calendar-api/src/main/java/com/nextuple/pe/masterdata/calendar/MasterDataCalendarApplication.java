package com.nextuple.pe.masterdata.calendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.nextuple")
public class MasterDataCalendarApplication {

  public static void main(String[] args) {
    SpringApplication.run(MasterDataCalendarApplication.class, args);
  }
}
