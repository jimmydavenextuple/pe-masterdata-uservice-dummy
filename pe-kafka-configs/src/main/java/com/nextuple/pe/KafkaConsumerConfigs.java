/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe;

import com.nextuple.dataupload.configuration.KafkaStringProperties;
import java.util.HashMap;
import java.util.Map;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.SaslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.MicrometerConsumerListener;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConsumerConfigs {

  @Value(value = "${spring.kafka.listener.concurrency:1}")
  private Integer listenerConcurrency;

  @Value(value = "${spring.kafka.consumer-item.key-deserializer}")
  private String keyDeserializer;

  @Value(value = "${spring.kafka.consumer-item.value-deserializer}")
  private String valueDeserializer;

  @Value(value = "${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value(value = "${spring.kafka.consumer-retry-count}")
  private long maxRetryCount;

  @Value(value = "${spring.kafka.consumer-retry-interval:0L}")
  private long retryInterval;

  @Value(value = "${spring.kafka.consumer-item.enable-auto-commit}")
  private Boolean enableAutoCommit;

  @Value(value = "${spring.kafka.consumer-item.auto-offset-reset}")
  private String autoOffsetReset;

  @Value(value = "${spring.kafka.consumer-item.properties.interceptor.classes}")
  private String interceptorClasses;

  private final KafkaProperties kafkaProperties;

  private final KafkaStringProperties kafkaStringProperties;
  private final MeterRegistry meterRegistry;
  private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerConfigs.class);


  @Primary
  @Bean("jsonDeserializerProperties")
  @ConfigurationProperties(prefix = "spring.kafka.consumer")
  public Map<String, Object> jsonDeserializerProperties() {
    return this.kafkaProperties.buildConsumerProperties();
  }

  @Bean("itemDeserializerProperties")
  @ConfigurationProperties(prefix = "spring.kafka.consumer-item")
  public Map<String, Object> itemDeserializerProperties() {
    return this.kafkaProperties.buildConsumerProperties();
  }

  @Bean
  public ConsumerFactory<String, Object> itemConsumerFactory() {
    HashMap<String, Object> prop = new HashMap<>(itemDeserializerProperties());
    Map<String, Object> properties = (Map<String, Object>) prop.get("properties");
    prop.put(
        ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
        properties.get("spring-deserializer-value-delegate-class"));
    prop.put(JsonDeserializer.TRUSTED_PACKAGES, properties.get("spring-json-trusted-packages"));
    prop.put(JsonDeserializer.TYPE_MAPPINGS, properties.get("spring-json-type-mapping"));
    Object valueDefaultType = properties.get("spring-json-value-default-type");
    if (valueDefaultType != null) {
      prop.put(JsonDeserializer.VALUE_DEFAULT_TYPE, valueDefaultType);
    }
    DefaultKafkaConsumerFactory<String, Object> itemConsumerFactory =
            new DefaultKafkaConsumerFactory<>(prop);
    itemConsumerFactory.addListener(new MicrometerConsumerListener<>(meterRegistry));
    return itemConsumerFactory;
  }

  @Bean
  @Primary
  public ConsumerFactory<Object, Object> jsonConsumerFactory() {
    HashMap<String, Object> prop = new HashMap<>(jsonDeserializerProperties());
    DefaultKafkaConsumerFactory<Object, Object> jsonConsumerFactory =
            new DefaultKafkaConsumerFactory<>(prop);
    jsonConsumerFactory.addListener(new MicrometerConsumerListener<>(meterRegistry));
    return jsonConsumerFactory;
  }

  @Primary
  @Bean(name = "JsonDeserializerConsumer")
  public ConcurrentKafkaListenerContainerFactory<Object, Object> jsonKafkaContainerListenerFactory(
      ConsumerFactory<Object, Object> jsonconsumerFactory,
      KafkaOperations<Object, Object> kafkaOperations) {
    ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(jsonConsumerFactory());
    return factory;
  }

  @Bean(name = "ItemDeserializerConsumer")
  @ConditionalOnProperty(name = "spring.kafka.consumer-item.type", havingValue = "json")
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactoryJSON(
      KafkaOperations<String, Object> kafkaOperations,
      ConsumerFactory<String, Object> consumerFactory) {

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(itemConsumerFactory());
    HashMap<String, Object> prop = new HashMap<>(itemDeserializerProperties());
    String dltTopicName = (String) getNestedProperty(prop, "topics.item_master.dlt_name");
    factory.setCommonErrorHandler(kafkaErrorHandler(kafkaOperations, dltTopicName));
    return factory;
  }

  @Bean(name = "StringDeserializerConsumer")
  public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaContainerListenerFactory(
      ConsumerFactory<Object, Object> consumerFactory,
      KafkaOperations<Object, Object> kafkaOperations) {
    ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(stringConsumerFactory());
    factory.setCommonErrorHandler(errorHandler(kafkaOperations));
    return factory;
  }

  @Bean
  public CommonErrorHandler kafkaErrorHandler(KafkaOperations<String, Object> kafkaOperations, String dltTopic) {
    FixedBackOff fixedBackOff = new FixedBackOff(retryInterval, maxRetryCount);
    DeadLetterPublishingRecoverer recoverer =
            new DeadLetterPublishingRecoverer(
                    kafkaOperations,
                    (cr, e) -> {
                      String resolvedTopicName = dltTopic != null ? dltTopic : cr.topic() + ".dlt";
                      return new TopicPartition(resolvedTopicName, cr.partition());
                    });
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, fixedBackOff);
    errorHandler.setRetryListeners(
            (consumerRecord, ex, deliveryAttempt) ->
                    logger.error(
                            "Retry attempt {} for record {} failed due to the message: {}",
                            deliveryAttempt,
                            consumerRecord,
                            ex.getMessage()));
    return errorHandler;
  }

  @Bean
  public ConsumerFactory<Object, Object> stringConsumerFactory() {
    HashMap<String, Object> prop = new HashMap<>();
    prop.put(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaStringProperties.getKeyDeserializer());
    prop.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        kafkaStringProperties.getValueDeserializer());
    prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
    prop.put(
        ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, kafkaStringProperties.getInterceptorClasses());
    prop.put(
        ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS,
        kafkaStringProperties.getKeyDelegateClass());
    prop.put(
        ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
        kafkaStringProperties.getKeyDelegateClass());
    Map<String, String> securityProps = kafkaProperties.getProperties();
    prop.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProps.get("security.protocol"));
    prop.put(SaslConfigs.SASL_MECHANISM, securityProps.get("sasl.mechanism"));
    prop.put(SaslConfigs.SASL_JAAS_CONFIG, securityProps.get("sasl.jaas.config"));
    prop.put(JsonDeserializer.TRUSTED_PACKAGES, kafkaStringProperties.getTrustedPackages());
    prop.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaStringProperties.getEnableAutoCommit());
    prop.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaStringProperties.getAutoOffsetReset());
    DefaultKafkaConsumerFactory<Object, Object> stringConsumerFactory =
            new DefaultKafkaConsumerFactory<>(prop);
    stringConsumerFactory.addListener(new MicrometerConsumerListener<>(meterRegistry));
    return stringConsumerFactory;
  }

  @Bean
  public CommonErrorHandler errorHandler(KafkaOperations<Object, Object> kafkaOperations) {
    return new DefaultErrorHandler(new DeadLetterPublishingRecoverer(kafkaOperations,
            (cr, e) -> new TopicPartition(cr.topic() + ".dlt", cr.partition())));
  }

  /**
   * Gets a nested property from a map using a dot-separated path.
   *
   * @param map The map to search.
   * @param path The dot-separated path to traverse through the nested maps.
   * @return The final value associated with the path, or null if any key is not found.
   */
  private Object getNestedProperty(Map<String, Object> map, String path) {
    // Split the path into keys
    String[] keys = path.split("\\.");

    // Traverse the map using the keys
    Object current = map;
    for (String key : keys) {
      if (current instanceof Map) {
        current = ((Map<?, ?>) current).get(key);
        // If the key is not found, current will be null and the loop will end
      } else {
        return null; // Return null if current is not a Map
      }
    }

    // Return the final value which could be any type or null
    return current;
  }
}
