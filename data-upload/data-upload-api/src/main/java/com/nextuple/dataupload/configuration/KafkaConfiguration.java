package com.nextuple.dataupload.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;

@EnableKafka
@Configuration
public class KafkaConfiguration {

  @Bean
  public CommonErrorHandler errorHandler(KafkaOperations<Object, Object> kafkaOperations) {
    return new DefaultErrorHandler(new DeadLetterPublishingRecoverer(kafkaOperations));
  }
}
