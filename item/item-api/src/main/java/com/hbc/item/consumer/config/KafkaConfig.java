package com.hbc.item.consumer.config;

import com.hbc.item.ItemRecord;
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
import org.springframework.util.backoff.FixedBackOff;

@EnableKafka
@Configuration
public class KafkaConfig {

  @Value(value = "${spring.kafka.consumer-retry-count}")
  private long maxRetryCount;

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ItemRecord> kafkaListenerContainerFactory(
      KafkaOperations<String, Object> kafkaOperations,
      ConsumerFactory<String, Object> consumerFactory) {

    ConcurrentKafkaListenerContainerFactory<String, ItemRecord> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setCommonErrorHandler(kafkaErrorHandler(kafkaOperations));
    return factory;
  }

  @Bean
  public CommonErrorHandler kafkaErrorHandler(KafkaOperations<String, Object> kafkaOperations) {
    return new DefaultErrorHandler(
        new DeadLetterPublishingRecoverer(
            kafkaOperations, (cr, e) -> new TopicPartition(cr.topic() + ".err", cr.partition())),
        new FixedBackOff(0L, maxRetryCount));
  }
}
