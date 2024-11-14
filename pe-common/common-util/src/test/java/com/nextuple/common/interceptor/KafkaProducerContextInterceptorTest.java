package com.nextuple.common.interceptor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.nextuple.common.context.CurrentThreadContext;
import java.util.ArrayList;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KafkaProducerContextInterceptorTest {

  @DisplayName("Should only set user locale and have a length of 1 in headers")
  @Test
  void onSendNullTest() {
    CurrentThreadContext.getLogContext().setTenantId(null);
    CurrentThreadContext.getLogContext().setCorrelationId(null);
    CurrentThreadContext.getLogContext().setUsername(null);
    CurrentThreadContext.getLogContext().setUserRole(null);

    KafkaProducerContextInterceptor kafkaProducerContextInterceptor =
        new KafkaProducerContextInterceptor();
    String topic = "topic";
    Integer partition = 1;
    Long timestamp = 1L;
    String key = "key";
    ProducerRecord<String, Object> producerRecord =
        new ProducerRecord<>(topic, partition, timestamp, key, "event", new ArrayList<>());
    ProducerRecord actual = kafkaProducerContextInterceptor.onSend(producerRecord);
    Header[] headers = actual.headers().toArray();
    assertEquals(1, headers.length);
  }

  //  @DisplayName(
  //      "Should set `tenantId`, `correlationId`, `username`, `userlocale`, and `role` from method
  // arguments and header is size 5")
  //  @Test
  //  void onSendTest() {
  //    String tenantId = "tenantId";
  //    String correlationId = "correlationId";
  //    String username = "username";
  //    String role = "role";
  //
  //    CurrentThreadContext.getLogContext().setTenantId(tenantId);
  //    CurrentThreadContext.getLogContext().setCorrelationId(correlationId);
  //    CurrentThreadContext.getLogContext().setUsername(username);
  //    CurrentThreadContext.getLogContext().setUserRole(role);
  //
  //    KafkaProducerContextInterceptor kafkaProducerContextInterceptor =
  //        new KafkaProducerContextInterceptor();
  //    String topic = "topic";
  //    Integer partition = 1;
  //    Long timestamp = 1L;
  //    String key = "key";
  //    BaseEvent value = new BaseEvent();
  //    value.setEventModule("event_module");
  //    value.setOriginModule("origin_module");
  //    value.setEventName("event_name");
  //    value.setEventTimestamp(new Date());
  //    ProducerRecord<String, Object> producerRecord =
  //        new ProducerRecord<>(topic, partition, timestamp, key, value, new ArrayList<>());
  //    ProducerRecord actual = kafkaProducerContextInterceptor.onSend(producerRecord);
  //    Header[] headers = actual.headers().toArray();
  //    assertEquals(7, headers.length);
  //  }

  @DisplayName("Should not through exception while setting record-metadata.")
  @Test
  void onAcknowledgementTest() {
    RecordMetadata recordMetadata = mock(RecordMetadata.class);
    Exception exception = mock(Exception.class);

    KafkaProducerContextInterceptor kafkaProducerContextInterceptor =
        new KafkaProducerContextInterceptor();

    assertDoesNotThrow(
        () -> {
          kafkaProducerContextInterceptor.onAcknowledgement(recordMetadata, exception);
        });
  }

  @DisplayName("Should not through exception while null exception is provided.")
  @Test
  void onAcknowledgementNullExceptionTest() {
    RecordMetadata recordMetadata = mock(RecordMetadata.class);
    Exception exception = null;

    KafkaProducerContextInterceptor kafkaProducerContextInterceptor =
        new KafkaProducerContextInterceptor();

    assertDoesNotThrow(
        () -> {
          kafkaProducerContextInterceptor.onAcknowledgement(recordMetadata, exception);
        });
  }

  @DisplayName("Should not through exception while null metadata and null exception is provided.")
  @Test
  void onAcknowledgementErrorTest() {
    RecordMetadata recordMetadata = null;
    Exception exception = null;

    KafkaProducerContextInterceptor kafkaProducerContextInterceptor =
        new KafkaProducerContextInterceptor();

    assertDoesNotThrow(
        () -> {
          kafkaProducerContextInterceptor.onAcknowledgement(recordMetadata, exception);
          kafkaProducerContextInterceptor.close();
          kafkaProducerContextInterceptor.configure(null);
        });
  }

  @Test
  void onSendMessageOfTypeString() {

    CurrentThreadContext.getLogContext().setTenantId("nextuple");
    CurrentThreadContext.getLogContext().setCorrelationId("cid");
    CurrentThreadContext.getLogContext().setUsername("uid");
    CurrentThreadContext.getLogContext().setUserRole("role");
    CurrentThreadContext.getLogContext().setUserLocale("user-locale");

    KafkaProducerContextInterceptor kafkaProducerContextInterceptor =
        new KafkaProducerContextInterceptor();
    ProducerRecord<String, Object> producerRecord =
        new ProducerRecord<>("stage_topic", 1, 1L, "stage_key", "event", new ArrayList<>());
    ProducerRecord actual = kafkaProducerContextInterceptor.onSend(producerRecord);
    Header[] headers = actual.headers().toArray();
    assertEquals(5, headers.length);
  }
}
