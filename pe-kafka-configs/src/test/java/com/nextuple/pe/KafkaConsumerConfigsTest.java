package com.nextuple.pe;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nextuple.dataupload.configuration.KafkaStringProperties;
import com.nextuple.pe.utils.TestUtil;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.test.util.ReflectionTestUtils;

class KafkaConsumerConfigsTest {
  @InjectMocks KafkaConsumerConfigs consumerConfigs;
  @InjectMocks TestUtil testUtil;
  @Mock KafkaProperties kafkaProperties;
  @Mock KafkaErrorHandlerProperties kafkaErrorHandlerProperties;
  @Mock KafkaStringProperties kafkaStringProperties;
  @Mock MeterRegistry meterRegistry;
  @Mock KafkaOperations<Object, Object> kafkaOperations;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(consumerConfigs, "kafkaProperties", kafkaProperties);
    ReflectionTestUtils.setField(consumerConfigs, "listenerConcurrency", 1);
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
    assertDoesNotThrow(() -> consumerConfigs.jsonKafkaContainerListenerFactory(null, null));
  }

  @Test
  void jsonContainerFactoryJSONTest() {
    KafkaOperations<String, Object> objectKafkaOperations = mock(KafkaOperations.class);
    when(kafkaProperties.buildConsumerProperties()).thenReturn(testUtil.getJsonProps());
    when(kafkaErrorHandlerProperties.getNonRetryableExceptions())
        .thenReturn(List.of("jakarta.validation.ConstraintViolationException"));
    assertDoesNotThrow(
        () -> consumerConfigs.kafkaListenerContainerFactoryJSON(objectKafkaOperations, null));
  }

  @Test
  void jsonContainerFactoryJSONInvalidClassTest() {
    KafkaOperations<String, Object> objectKafkaOperations = mock(KafkaOperations.class);
    when(kafkaProperties.buildConsumerProperties()).thenReturn(testUtil.getJsonProps());
    when(kafkaErrorHandlerProperties.getNonRetryableExceptions())
        .thenReturn(List.of("jakarta.validation.ConstraintViolationExceptionx"));
    assertThrows(
        IllegalArgumentException.class,
        () -> consumerConfigs.kafkaListenerContainerFactoryJSON(objectKafkaOperations, null));
  }

  @Test
  void jsonContainerFactoryJSONWithNonNullDefaultTypeTest() {
    KafkaOperations<String, Object> objectKafkaOperations = mock(KafkaOperations.class);
    Map<String, Object> jsonProps = testUtil.getJsonProps();
    Map<String, Object> properties = (Map<String, Object>) jsonProps.get("properties");
    properties.put("spring-json-value-default-type", "json");
    when(kafkaProperties.buildConsumerProperties()).thenReturn(jsonProps);
    when(kafkaErrorHandlerProperties.getNonRetryableExceptions())
        .thenReturn(Collections.emptyList());
    assertDoesNotThrow(
        () -> consumerConfigs.kafkaListenerContainerFactoryJSON(objectKafkaOperations, null));
  }

  @Test
  void kafkaErrorHandlerRetryTest() {
    ConsumerRecord<String, Object> record =
        new ConsumerRecord<>("testTopic", 0, 0L, "key", "value");
    Exception exception = new RuntimeException("Test exception");
    Assertions.assertDoesNotThrow(() -> consumerConfigs.retryListener(record, exception, 1));
  }

  @Test
  void stringContainerFactoryJSONTest() {
    KafkaOperations<Object, Object> objectKafkaOperations = mock(KafkaOperations.class);
    when(kafkaStringProperties.getKeyDeserializer())
        .thenReturn("org.springframework.kafka.support.serializer.ErrorHandlingDeserializer");
    when(kafkaStringProperties.getValueDeserializer())
        .thenReturn("org.springframework.kafka.support.serializer.ErrorHandlingDeserializer");
    when(kafkaStringProperties.getInterceptorClasses())
        .thenReturn("com.nextuple.common.interceptor.KafkaProducerContextInterceptor");
    when(kafkaStringProperties.getKeyDelegateClass())
        .thenReturn("com.nextuple.common.interceptor.KafkaProducerContextInterceptor");
    when(kafkaStringProperties.getTrustedPackages()).thenReturn("*");
    when(kafkaStringProperties.getEnableAutoCommit()).thenReturn("true");
    when(kafkaStringProperties.getAutoOffsetReset()).thenReturn("0");

    when(kafkaProperties.getProperties()).thenReturn(testUtil.getKafkaProperties());
    when(kafkaProperties.getBootstrapServers()).thenReturn(List.of("localhost:9092"));

    when(kafkaProperties.buildConsumerProperties()).thenReturn(testUtil.getJsonProps());
    assertDoesNotThrow(
        () -> consumerConfigs.kafkaContainerListenerFactory(null, objectKafkaOperations));
  }

  @Test
  void dltTopicResolverTest() {
    ConsumerRecord<String, Object> record =
        new ConsumerRecord<>("testTopic", 0, 0L, "key", "value");
    Exception exception = new RuntimeException("Test exception");
    BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> biFunction =
        KafkaConsumerConfigs.getConsumerRecordExceptionTopicPartitionBiFunction("test");
    TopicPartition topicPartition = biFunction.apply(record, exception);
    Assertions.assertEquals("test", topicPartition.topic());
  }

  @Test
  void dltTopicResolverDLTNotSpecifiedTest() {
    ConsumerRecord<String, Object> record =
        new ConsumerRecord<>("testTopic", 0, 0L, "key", "value");
    Exception exception = new RuntimeException("Test exception");
    BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> biFunction =
        KafkaConsumerConfigs.getConsumerRecordExceptionTopicPartitionBiFunction(null);
    TopicPartition topicPartition = biFunction.apply(record, exception);
    Assertions.assertEquals("testTopic.dlt", topicPartition.topic());
  }
}
