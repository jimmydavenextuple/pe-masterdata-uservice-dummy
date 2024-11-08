package com.nextuple.item.config; /*
                                   * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
                                   *
                                   * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
                                   * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
                                   */

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.nextuple.item.TestUtil;
import com.nextuple.masterdata.config.KafkaConsumerConfigs;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.test.util.ReflectionTestUtils;

class KafkaConsumerConfigsTest {

  @InjectMocks KafkaConsumerConfigs consumerConfigs;
  @InjectMocks TestUtil testUtil;

  @Mock KafkaProperties kafkaProperties;

  @Mock KafkaOperations<Object, Object> kafkaOperations;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(consumerConfigs, "kafkaProperties", kafkaProperties);
    ReflectionTestUtils.setField(consumerConfigs, "listenerConcurrency", 1);
    //        ReflectionTestUtils.setField(consumerConfigs, "retryDuration", 0);
    ReflectionTestUtils.setField(consumerConfigs, "maxRetryCount", 3);
    ReflectionTestUtils.setField(
        consumerConfigs,
        "keyDeserializer",
        "org.springframework.kafka.support.serializer.ErrorHandlingDeserializer");
    ReflectionTestUtils.setField(
        consumerConfigs,
        "valueDeserializer",
        "org.springframework.kafka.support.serializer.ErrorHandlingDeserializer");
    ReflectionTestUtils.setField(
        consumerConfigs, "bootstrapServers", "temp-5-kafka.integration.awshbc.io:9094");
    ReflectionTestUtils.setField(consumerConfigs, "enableAutoCommit", true);
    ReflectionTestUtils.setField(consumerConfigs, "autoOffsetReset", "earliest");
    //        ReflectionTestUtils.setField(consumerConfigs, "keyDelegate",
    // "io.confluent.kafka.serializers.KafkaAvroDeserializer");
    //        ReflectionTestUtils.setField(consumerConfigs, "valueDelegate",
    // "io.confluent.kafka.serializers.KafkaAvroDeserializer");
    //        ReflectionTestUtils.setField(consumerConfigs, "trustedPackages", "*");
    //        ReflectionTestUtils.setField(consumerConfigs, "schemaRegistry",
    // "http://schema-registry-temp-5.integration.awshbc.io:8081");
    //        ReflectionTestUtils.setField(consumerConfigs, "specificAvroReader", true);
    //        ReflectionTestUtils.setField(consumerConfigs, "partitionAssignmentStrategy",
    // "org.apache.kafka.clients.consumer.CooperativeStickyAssignor");
    ReflectionTestUtils.setField(
        consumerConfigs,
        "interceptorClasses",
        "com.nextuple.common.interceptor.KafkaProducerContextInterceptor");
  }

  @Test
  void jsonDeserializerPropertiesTest() {
    when(kafkaProperties.buildConsumerProperties()).thenReturn(testUtil.getJsonProps());
    Assertions.assertNotNull(consumerConfigs.jsonDeserializerProperties());
  }

  @Test
  void jsonContainerFactoryTest() {
    when(kafkaProperties.buildConsumerProperties()).thenReturn(testUtil.getJsonProps());
    assertDoesNotThrow(
        () ->
            consumerConfigs.jsonKafkaContainerListenerFactory(
                new DefaultKafkaConsumerFactory<>(new HashMap<>()), null));
  }

  //    @Test
  //    void avroContainerFactoryTest(){
  //        when(kafkaProperties.buildConsumerProperties()).thenReturn(testUtil.getAvroProps());
  //        assertDoesNotThrow(() -> consumerConfigs.kafkaContainerListenerFactory(null,
  // kafkaOperations));
  //    }
}
