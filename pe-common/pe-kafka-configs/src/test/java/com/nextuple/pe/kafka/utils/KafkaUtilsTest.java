package com.nextuple.pe.kafka.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.BiFunction;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class KafkaUtilsTest {

  @InjectMocks KafkaUtils kafkaUtils;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void kafkaErrorHandlerRetryTest() {
    ConsumerRecord<String, Object> record =
        new ConsumerRecord<>("testTopic", 0, 0L, "key", "value");
    Exception exception = new RuntimeException("Test exception");
    Assertions.assertDoesNotThrow(() -> kafkaUtils.retryListener(record, exception, 1));
  }

  @Test
  void dltTopicResolverTest() {
    ConsumerRecord<String, Object> record =
        new ConsumerRecord<>("testTopic", 0, 0L, "key", "value");
    Exception exception = new RuntimeException("Test exception");
    BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> biFunction =
        KafkaUtils.getConsumerRecordExceptionTopicPartitionBiFunction("test");
    TopicPartition topicPartition = biFunction.apply(record, exception);
    Assertions.assertEquals("test", topicPartition.topic());
  }

  @Test
  void dltTopicResolverDLTNotSpecifiedTest() {
    ConsumerRecord<String, Object> record =
        new ConsumerRecord<>("testTopic", 0, 0L, "key", "value");
    Exception exception = new RuntimeException("Test exception");
    BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> biFunction =
        KafkaUtils.getConsumerRecordExceptionTopicPartitionBiFunction(null);
    TopicPartition topicPartition = biFunction.apply(record, exception);
    Assertions.assertEquals("testTopic.dlt", topicPartition.topic());
  }
}
