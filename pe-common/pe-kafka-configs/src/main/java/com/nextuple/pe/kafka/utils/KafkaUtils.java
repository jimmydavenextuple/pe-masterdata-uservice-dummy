/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.kafka.utils;

import com.nextuple.pe.KafkaErrorHandlerProperties;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.backoff.FixedBackOff;

@Component
@RequiredArgsConstructor
public class KafkaUtils {
  @Value(value = "${spring.kafka.consumer-retry-count}")
  private long maxRetryCount;

  @Value(value = "${spring.kafka.consumer-retry-interval:0}")
  private long retryInterval;

  private static final Logger logger = LoggerFactory.getLogger(KafkaUtils.class);
  private final KafkaErrorHandlerProperties kafkaErrorHandlerProperties;

  /**
   * Configures and returns a Kafka error handler for managing message consumption failures. The
   * error handler supports retry logic with a fixed backoff strategy and integrates a Dead Letter
   * Topic (DLT) for handling unrecoverable errors. Additionally, it allows configuration of
   * non-retryable exception classes.
   *
   * <p>The error handler ensures that failed messages are retried a configurable number of times
   * before being published to the DLT. Non-retryable exceptions can be specified to bypass retries
   * for specific error types. Retry attempts are logged for tracking purposes.
   *
   * @param kafkaOperations The {@link KafkaOperations} instance used to publish failed messages to
   *     the Dead Letter Topic (DLT).
   * @param dltTopic The name of the Dead Letter Topic. If {@code null}, a topic with the suffix
   *     {@code .dlt} will be used based on the original topic name.
   * @return A configured {@link CommonErrorHandler} instance with retry and DLT mechanisms.
   *     <p>Features:
   *     <ul>
   *       <li>Retry logic with a fixed backoff strategy.
   *       <li>Dead Letter Topic integration for unrecoverable errors.
   *       <li>Customizable non-retryable exception classes.
   *       <li>Retry attempt logging for monitoring.
   *     </ul>
   *     <p>Example Usage:
   *     <pre>
   *     KafkaOperations&lt;String, Object&gt; kafkaOperations = ...;
   *     String dltTopic = "my-dlt-topic";
   *     CommonErrorHandler errorHandler = kafkaErrorHandler(kafkaOperations, dltTopic);
   *         </pre>
   *
   * @see FixedBackOff
   * @see DeadLetterPublishingRecoverer
   * @see DefaultErrorHandler
   */
  public CommonErrorHandler kafkaErrorHandler(
      KafkaOperations<String, Object> kafkaOperations, String dltTopic) {
    FixedBackOff fixedBackOff = new FixedBackOff(retryInterval, maxRetryCount);
    DeadLetterPublishingRecoverer recoverer =
        new DeadLetterPublishingRecoverer(
            kafkaOperations, getConsumerRecordExceptionTopicPartitionBiFunction(dltTopic));
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, fixedBackOff);
    errorHandler.setRetryListeners(this::retryListener);
    List<Class<? extends Exception>> nonRetryableExceptionClasses = new ArrayList<>();
    if (!CollectionUtils.isEmpty(kafkaErrorHandlerProperties.getNonRetryableExceptions())) {
      nonRetryableExceptionClasses =
          kafkaErrorHandlerProperties.getNonRetryableExceptions().stream()
              .map(this::getExceptionClass)
              .collect(Collectors.toList());
    }
    nonRetryableExceptionClasses.forEach(errorHandler::addNotRetryableExceptions);
    return errorHandler;
  }

  @NotNull
  public static BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition>
      getConsumerRecordExceptionTopicPartitionBiFunction(String dltTopic) {
    return (cr, e) -> {
      String resolvedTopicName = dltTopic != null ? dltTopic : cr.topic() + ".dlt";
      return new TopicPartition(resolvedTopicName, cr.partition());
    };
  }

  public void retryListener(
      ConsumerRecord<?, ?> consumerRecord, Exception ex, int deliveryAttempt) {
    logger.error(
        "Retry attempt {} for record {} failed due to the message: {}",
        deliveryAttempt,
        consumerRecord,
        ex.getMessage());
  }

  private Class<? extends Exception> getExceptionClass(String className) {
    try {
      return (Class<? extends Exception>) Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Invalid exception class name: " + className, e);
    }
  }
}
