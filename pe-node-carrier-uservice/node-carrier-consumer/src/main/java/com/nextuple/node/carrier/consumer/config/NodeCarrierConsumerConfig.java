/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 *
 */

package com.nextuple.node.carrier.consumer.config;

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
public class NodeCarrierConsumerConfig {

  @Value("${master-data.node-carrier.dlt-topic:#{null}}")
  private String nodeCarrierDltTopic;

  @Value("${master-data.processing-lead-time.dlt-topic:#{null}}")
  private String processingLeadTimeDltTopic;

  @Value("${master-data.node-service-option-buffer.dlt-topic:#{null}}")
  private String nodeServiceOptionBufferDltTopic;

  @Bean(name = "nodeCarrierDeserializerConsumer")
  public ConcurrentKafkaListenerContainerFactory<String, Object> nodeCarrierDeserializerConsumer(
      KafkaOperations<String, Object> kafkaOperations,
      ConsumerFactory<String, Object> nodeCarrierConsumer) {

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(nodeCarrierConsumer);
    factory.setCommonErrorHandler(kafkaErrorHandler(kafkaOperations, nodeCarrierDltTopic));
    return factory;
  }

  @Bean(name = "processingLeadTimeDeserializerConsumer")
  public ConcurrentKafkaListenerContainerFactory<String, Object>
      processingLeadTimeDeserializerConsumer(
          KafkaOperations<String, Object> kafkaOperations,
          ConsumerFactory<String, Object> processingLeadTimeConsumer) {

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(processingLeadTimeConsumer);
    factory.setCommonErrorHandler(kafkaErrorHandler(kafkaOperations, processingLeadTimeDltTopic));
    return factory;
  }

  @Bean(name = "nodeServiceOptionBufferDeserializerConsumer")
  public ConcurrentKafkaListenerContainerFactory<String, Object>
      nodeServiceOptionBufferDeserializerConsumer(
          KafkaOperations<String, Object> kafkaOperations,
          ConsumerFactory<String, Object> nodeServiceOptionBufferConsumer) {

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(nodeServiceOptionBufferConsumer);
    factory.setCommonErrorHandler(
        kafkaErrorHandler(kafkaOperations, nodeServiceOptionBufferDltTopic));
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
