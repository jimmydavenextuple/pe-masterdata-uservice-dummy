package com.hbc.pe.masterdata.calendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.hbc")
@EnableJpaRepositories("com.hbc.pe.masterdata.calendar.domain.repository")
public class MasterDataCalendarApplication {

  public static void main(String[] args) {
    SpringApplication.run(MasterDataCalendarApplication.class, args);
  }
}
