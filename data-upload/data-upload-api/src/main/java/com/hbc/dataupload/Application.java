package com.hbc.dataupload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.hbc")
@EnableFeignClients(basePackages = {"com.hbc"})
@Slf4j
public class Application {

    public static void main(String[] args) {

        log.debug("Starting service with args...");
        SpringApplication.run(Application.class, args);
        log.debug("Service started successfully!");
    }
}
