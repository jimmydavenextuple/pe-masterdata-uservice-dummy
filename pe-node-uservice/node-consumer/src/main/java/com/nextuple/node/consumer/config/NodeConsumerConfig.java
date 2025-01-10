package com.nextuple.node.consumer.config;

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
public class NodeConsumerConfig {

  @Value("${master-data.node.dlt-topic:null}")
  private String nodeDltTopic;

  @Bean(name = "nodeDeserializerConsumer")
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactoryJSON(
      KafkaOperations<String, Object> kafkaOperations,
      ConsumerFactory<String, Object> nodeConsumerFactory) {

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(nodeConsumerFactory);
    factory.setCommonErrorHandler(kafkaErrorHandler(kafkaOperations));
    return factory;
  }

  @Bean
  public CommonErrorHandler kafkaErrorHandler(KafkaOperations<String, Object> kafkaOperations) {
    DeadLetterPublishingRecoverer recoverer =
        new DeadLetterPublishingRecoverer(
            kafkaOperations,
            (cr, e) -> {
              String resolvedTopicName = nodeDltTopic != null ? nodeDltTopic : cr.topic() + ".DLT";
              return new TopicPartition(resolvedTopicName, cr.partition());
            });
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer);
    errorHandler.addNotRetryableExceptions(Exception.class);
    return errorHandler;
  }
}
