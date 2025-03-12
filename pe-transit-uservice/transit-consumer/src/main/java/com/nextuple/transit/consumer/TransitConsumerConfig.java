/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 *
 */

package com.nextuple.transit.consumer;

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
public class TransitConsumerConfig {

  @Value("${master-data.transit.dlt-topic:#{null}}")
  private String transitFeedDltTopic;

  @Value("${master-data.transfer-schedule.dlt-topic:#{null}}")
  private String transferScheduleDltTopic;

  @Value("${master-data.transit-buffer.dlt-topic:#{null}}")
  private String transitBufferFeedDltTopic;

  @Bean(name = "transitFeedDeserializerConsumer")
  public ConcurrentKafkaListenerContainerFactory<String, Object> transitFeedDeserializerConsumer(
      KafkaOperations<String, Object> kafkaOperations,
      ConsumerFactory<String, Object> nodeCarrierConsumer) {

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(nodeCarrierConsumer);
    factory.setCommonErrorHandler(kafkaErrorHandler(kafkaOperations, transitFeedDltTopic));
    return factory;
  }

  @Bean(name = "transitBufferFeedDeserializerConsumer")
  public ConcurrentKafkaListenerContainerFactory<String, Object>
      transitBufferFeedDeserializerConsumer(
          KafkaOperations<String, Object> kafkaOperations,
          ConsumerFactory<String, Object> processingLeadTimeConsumer) {

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(processingLeadTimeConsumer);
    factory.setCommonErrorHandler(kafkaErrorHandler(kafkaOperations, transitBufferFeedDltTopic));
    return factory;
  }

  @Bean(name = "transferScheduleDeserializerConsumer")
  public ConcurrentKafkaListenerContainerFactory<String, Object>
      transferScheduleDeserializerConsumer(
          KafkaOperations<String, Object> kafkaOperations,
          ConsumerFactory<String, Object> transferScheduleConsumer) {

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(transferScheduleConsumer);
    factory.setCommonErrorHandler(kafkaErrorHandler(kafkaOperations, transferScheduleDltTopic));
    return factory;
  }

  public CommonErrorHandler kafkaErrorHandler(
      KafkaOperations<String, Object> kafkaOperations, String dltTopic) {
    DeadLetterPublishingRecoverer recoverer =
        new DeadLetterPublishingRecoverer(
            kafkaOperations,
            (cr, e) -> {
              String resolvedTopicName = dltTopic != null ? dltTopic : cr.topic() + ".DLT";
              return new TopicPartition(resolvedTopicName, cr.partition());
            });
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer);
    errorHandler.addNotRetryableExceptions(Exception.class);
    return errorHandler;
  }
}
