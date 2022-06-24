package com.nextuple.postal.code.timezone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.nextuple")
public class PostalCodeTimezoneApplication {

  public static void main(String[] args) {
    SpringApplication.run(PostalCodeTimezoneApplication.class, args);
  }
}
