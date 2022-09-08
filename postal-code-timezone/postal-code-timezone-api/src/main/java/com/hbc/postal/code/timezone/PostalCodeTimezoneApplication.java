package com.hbc.postal.code.timezone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.hbc")
@EnableJpaRepositories("com.hbc.postal.code.timezone.domain.repository")
public class PostalCodeTimezoneApplication {

  public static void main(String[] args) {
    SpringApplication.run(PostalCodeTimezoneApplication.class, args);
  }
}
