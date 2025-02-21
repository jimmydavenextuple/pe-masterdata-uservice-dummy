package com.nextuple.pe;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.nextuple.core.event.LocalCacheUpdateEvent;
import com.nextuple.jobs.framework.common.domain.pojo.RecordDto;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

class KafkaProducerConfigsTest {
  @InjectMocks KafkaProducerConfigs kafkaProducerConfigs;
  @Mock KafkaProperties kafkaProperties;
  @Mock MeterRegistry meterRegistry;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(kafkaProducerConfigs, "bootstrapServers", "localhost:9092");
  }

  @Test
  void itemKafkaTemplateTest() {
    Map<String, Object> itemSerializerProperties =
        Map.of(
            "key.serializer", "org.apache.kafka.common.serialization.StringSerializer",
            "value.serializer", "org.apache.kafka.common.serialization.JsonSerializer");
    when(kafkaProperties.buildProducerProperties()).thenReturn(itemSerializerProperties);
    KafkaTemplate<String, Object> kafkaTemplate = kafkaProducerConfigs.itemKafkaTemplate();
    assertNotNull(kafkaTemplate);
  }

  @Test
  void jobEventKafkaTemplateTest() {
    Map<String, Object> itemSerializerProperties =
        Map.of(
            "key.serializer", "org.apache.kafka.common.serialization.StringSerializer",
            "value.serializer", "org.apache.kafka.common.serialization.JsonSerializer",
            "acks", "1",
            "interceptor.classes", "interceptor.class");

    when(kafkaProperties.buildProducerProperties()).thenReturn(itemSerializerProperties);
    KafkaTemplate<Object, Object> kafkaTemplate = kafkaProducerConfigs.jobEventKafkaTemplate();
    assertNotNull(kafkaTemplate);
  }

  @Test
  void jobServiceKafkaTemplateTest() {
    Map<String, Object> itemSerializerProperties =
        Map.of(
            "key.serializer", "org.apache.kafka.common.serialization.StringSerializer",
            "value.serializer", "org.apache.kafka.common.serialization.JsonSerializer",
            "acks", "1",
            "interceptor.classes", "interceptor.class");
    when(kafkaProperties.buildProducerProperties()).thenReturn(itemSerializerProperties);
    KafkaTemplate<String, RecordDto> kafkaTemplate = kafkaProducerConfigs.jobServiceKafkaTemplate();
    assertNotNull(kafkaTemplate);
  }

  @Test
  void cacheUpdateKafkaTemplateTest() {
    Map<String, Object> itemSerializerProperties =
        Map.of(
            "key.serializer", "org.apache.kafka.common.serialization.StringSerializer",
            "value.serializer", "org.apache.kafka.common.serialization.JsonSerializer",
            "acks", "1",
            "interceptor.classes", "interceptor.class");
    when(kafkaProperties.buildProducerProperties()).thenReturn(itemSerializerProperties);
    KafkaTemplate<String, LocalCacheUpdateEvent> kafkaTemplate =
        kafkaProducerConfigs.cacheUpdateKafkaTemplate();
    assertNotNull(kafkaTemplate);
  }
}
