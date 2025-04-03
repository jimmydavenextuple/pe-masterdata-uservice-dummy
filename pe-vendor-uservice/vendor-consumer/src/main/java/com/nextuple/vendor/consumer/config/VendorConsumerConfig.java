package com.nextuple.vendor.consumer.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;

@Configuration
@EnableKafka
public class VendorConsumerConfig {

  @Value("${master-data.vendor.dlt-topic:#{null}}")
  private String vendorDltTopic;

  @Bean(name = "vendorDeserializerConsumer")
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactoryJSON(
      KafkaOperations<String, Object> kafkaOperations,
      ConsumerFactory<String, Object> vendorConsumerFactory) {

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(vendorConsumerFactory);
    factory.setCommonErrorHandler(kafkaErrorHandler(kafkaOperations));
    return factory;
  }

  @Bean
  public CommonErrorHandler kafkaErrorHandler(KafkaOperations<String, Object> kafkaOperations) {
    DeadLetterPublishingRecoverer recoverer =
        new DeadLetterPublishingRecoverer(
            kafkaOperations,
            (cr, e) -> {
              String resolvedTopicName =
                  vendorDltTopic != null ? vendorDltTopic : cr.topic() + ".DLT";
              return new TopicPartition(resolvedTopicName, cr.partition());
            });
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer);
    errorHandler.addNotRetryableExceptions(Exception.class);
    return errorHandler;
  }
}
