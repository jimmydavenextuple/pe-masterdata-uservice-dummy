package com.nextuple.pe.userexit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.nextuple")
@EnableJpaRepositories("com.nextuple.pe.userexit.repository")
@EntityScan("com.nextuple.common")
@Slf4j
public class UserExitApplication {
  public static void main(String[] args) {

    log.debug("Starting service with args...");
    SpringApplication.run(UserExitApplication.class);
    log.debug("Service started successfully!");
  }
}
