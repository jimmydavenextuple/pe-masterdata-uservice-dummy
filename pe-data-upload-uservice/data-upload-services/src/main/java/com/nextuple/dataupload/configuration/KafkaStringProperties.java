package com.nextuple.dataupload.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spring.kafka.consumer-string")
@Data
public class KafkaStringProperties {
  private String keyDeserializer;
  private String valueDeserializer;
  private String keyDelegateClass;
  private String trustedPackages;
  private String interceptorClasses;
  private String enableAutoCommit;
  private String autoOffsetReset;
}
