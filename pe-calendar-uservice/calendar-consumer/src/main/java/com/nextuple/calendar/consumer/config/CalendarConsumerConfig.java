/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 *
 */

package com.nextuple.calendar.consumer.config;

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
public class CalendarConsumerConfig {

  @Value("${master-data.calendar.dlt-topic}")
  private String calendarDltTopic;

  @Value("${master-data.carrier-service-calendar.dlt-topic}")
  private String carrierServiceCalendarDltTopic;

  @Value("${master-data.calendar.dlt-topic}")
  private String nodeCalendarDltTopic;

  @Value("${master-data.pickup-calendar.dlt-topic}")
  private String pickupCalendarDltTopic;

  @Bean(name = "carrierServiceCalendarDeserializerConsumer")
  public ConcurrentKafkaListenerContainerFactory<String, Object> carrierServiceCalendar(
      KafkaOperations<String, Object> kafkaOperations,
      ConsumerFactory<String, Object> carrierServiceCalendarConsumer) {

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(carrierServiceCalendarConsumer);
    factory.setCommonErrorHandler(
        kafkaErrorHandler(kafkaOperations, carrierServiceCalendarDltTopic));
    return factory;
  }

  @Bean(name = "nodeCalendarDeserializerConsumer")
  public ConcurrentKafkaListenerContainerFactory<String, Object> nodeCalendarDeserializerConsumer(
      KafkaOperations<String, Object> kafkaOperations,
      ConsumerFactory<String, Object> nodeCalendarConsumerFactory) {

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(nodeCalendarConsumerFactory);
    factory.setCommonErrorHandler(kafkaErrorHandler(kafkaOperations, nodeCalendarDltTopic));
    return factory;
  }

  @Bean(name = "calendarDeserializerConsumer")
  public ConcurrentKafkaListenerContainerFactory<String, Object> calendarDeserializerConsumer(
      KafkaOperations<String, Object> kafkaOperations,
      ConsumerFactory<String, Object> calendarConsumerFactory) {

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(calendarConsumerFactory);
    factory.setCommonErrorHandler(kafkaErrorHandler(kafkaOperations, calendarDltTopic));
    return factory;
  }

  @Bean(name = "pickupCalendarDeserializerConsumer")
  public ConcurrentKafkaListenerContainerFactory<String, Object> pickupCalendarDeserializerConsumer(
      KafkaOperations<String, Object> kafkaOperations,
      ConsumerFactory<String, Object> pickupCalendarConsumerFactory) {

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(pickupCalendarConsumerFactory);
    factory.setCommonErrorHandler(kafkaErrorHandler(kafkaOperations, pickupCalendarDltTopic));
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
