package com.nextuple.common.interceptor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class KafkaConsumerContestInterceptorTest {

  @DisplayName("Should return empty Consumer Records since map is empty")
  @Test
  void onConsumeTest() {
    KafkaConsumerContextInterceptor kafkaConsumerContextInterceptor =
        new KafkaConsumerContextInterceptor();
    Map<TopicPartition, List<ConsumerRecord<String, String>>> map = new HashMap<>();
    ConsumerRecords<String, String> records = new ConsumerRecords<>(map);
    ConsumerRecords actual = kafkaConsumerContextInterceptor.onConsume(records);
    assertTrue(actual.isEmpty());
  }

  @DisplayName("Should not throw any Exceptions ")
  @Test
  void onCommitTest() {
    KafkaConsumerContextInterceptor kafkaConsumerContextInterceptor =
        new KafkaConsumerContextInterceptor();
    assertDoesNotThrow(
        () -> {
          kafkaConsumerContextInterceptor.onCommit(null);
        });
  }

  @DisplayName("Should return Consumer Records as provided.")
  @Test
  void onConsumeNotNullTest() {
    KafkaConsumerContextInterceptor kafkaConsumerContextInterceptor =
        new KafkaConsumerContextInterceptor();

    Map<TopicPartition, List<ConsumerRecord<String, String>>> map = new HashMap<>();
    map.put(new TopicPartition("test", 45), new ArrayList<>());
    Map<String, ?> configs = new HashMap<>();
    ConsumerRecords<String, String> records = new ConsumerRecords<>(map);

    ConsumerRecords actual = kafkaConsumerContextInterceptor.onConsume(records);
    assertDoesNotThrow(
        () -> {
          kafkaConsumerContextInterceptor.configure(configs);
        });
    assertDoesNotThrow(
        () -> {
          kafkaConsumerContextInterceptor.close();
        });
    assertEquals(records, actual);
  }
}
