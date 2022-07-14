package com.hbc.common.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CurrentThreadContextTest {

  @DisplayName("Should all be null because of the cleaning the log context")
  @Test
  void getLogContextTest() {
    CurrentThreadContext.cleanLogContext();

    LogContext actual = CurrentThreadContext.getLogContext();

    assertNull(actual.getApplicationName(), "Application name");
    assertNull(actual.getApplicationVersion(), "Application version");
    assertNull(actual.getCorrelationId(), "Correlation Id");
    assertNull(actual.getKafkaEventDate(), "Kafka Event Date");
    assertNull(actual.getKafkaEventType(), "Kafka Event Type");
    assertNull(actual.getKafkaHeaders(), "Kafka Headers");
    assertNull(actual.getKafkaEventName(), "Kakfa Event Name");
    assertNull(actual.getKafkaMessageId(), "Kafka Message Id");
    assertNull(actual.getKafkaTopic(), "Kafka Topic");
    assertNull(actual.getRequestEndpoint(), "Request Endpoint");
    assertNull(actual.getRequestHeaders(), "Request Headers");
    assertNull(actual.getRequestParameters(), "Request Parameters");
    assertNull(actual.getRequestMethod(), "Request Method");
    assertNull(actual.getResponseCode(), "Response Code");
    assertNull(actual.getServiceCorrelationId(), "Service Correlation Id");
    assertNull(actual.getTraceId(), "Trace Id");
  }

  @DisplayName("Should set all the parameters based on function methods")
  @Test
  void setLogContextTest() {
    String apiKey = "apiKey";
    String applicationName = "applicationName";
    String applicationVersion = "applicationVersion";
    String correlationId = "correlationId";
    String kafkaEventDate = "kafkaEventDate";
    String kafkaEventType = "kafkaEventType";
    String kafkaEventName = "kafkaEventName";
    String kafkaMessageId = "kafkaMessageId";
    String kafkaTopic = "kafkaTopic";
    String requestMethod = "requestMethod";
    String requestEndpoint = "requestEndpoint";
    String responseCode = "responseCode";
    String serviceCorrelationId = "serviceCorrelationId";
    String tenantDnsName = "tenantDnsName";
    String tenantId = "tenantId";
    String traceId = "traceId";
    String userLocale = "userLocale";
    String username = "username";
    String userRole = "userRole";

    LogContext expected = LogContext.builder().build();
    expected.setApplicationName(applicationName);
    expected.setApplicationVersion(applicationVersion);
    expected.setCorrelationId(correlationId);
    expected.setKafkaEventDate(kafkaEventDate);
    expected.setKafkaEventType(kafkaEventType);
    expected.setKafkaEventName(kafkaEventName);
    expected.setKafkaMessageID(kafkaMessageId);
    expected.setKafkaTopic(kafkaTopic);
    expected.setRequestMethod(requestMethod);
    expected.setRequestEndpoint(requestEndpoint);
    expected.setResponseCode(responseCode);
    expected.setServiceCorrelationId(serviceCorrelationId);
    expected.setTraceId(traceId);

    CurrentThreadContext.setLogContext(expected);
    LogContext actual = CurrentThreadContext.getLogContext();

    assertEquals(expected.getApplicationName(), actual.getApplicationName(), "Application Name");
    assertEquals(
        expected.getApplicationVersion(), actual.getApplicationVersion(), "Application Version");
    assertEquals(expected.getCorrelationId(), actual.getCorrelationId(), "Correlation Id");
    assertEquals(expected.getKafkaEventDate(), actual.getKafkaEventDate(), "Kafka Event Date");
    assertEquals(expected.getKafkaEventType(), actual.getKafkaEventType(), "Kafka Event Type");
    assertEquals(expected.getKafkaHeaders(), actual.getKafkaHeaders(), "Kafka Headers");
    assertEquals(expected.getKafkaEventName(), actual.getKafkaEventName(), "Kafka Event Name");
    assertEquals(expected.getKafkaMessageId(), actual.getKafkaMessageId(), "Kafka Message Id");
    assertEquals(expected.getKafkaTopic(), actual.getKafkaTopic(), "Kafka Topic");
    assertEquals(expected.getRequestEndpoint(), actual.getRequestEndpoint(), "Request Endpoint");
    assertEquals(expected.getRequestHeaders(), actual.getRequestHeaders(), "Request Headers");
    assertEquals(
        expected.getRequestParameters(), actual.getRequestParameters(), "Request Parameters");
    assertEquals(expected.getRequestMethod(), actual.getRequestMethod(), "Request Method");
    assertEquals(expected.getResponseCode(), actual.getResponseCode(), "Response Code");
    assertEquals(
        expected.getServiceCorrelationId(),
        actual.getServiceCorrelationId(),
        "Service Correlation Id");
    assertEquals(expected.getTraceId(), actual.getTraceId(), "Trace Id");
  }

  @DisplayName("Should return null due to the clean Log Context")
  @Test
  void cleanLogContextTest() {
    CurrentThreadContext.cleanLogContext();
    LogContext actual = CurrentThreadContext.getLogContext();

    assertNull(actual.getApplicationName(), "Application name");
    assertNull(actual.getApplicationVersion(), "Application version");
    assertNull(actual.getCorrelationId(), "Correlation Id");
    assertNull(actual.getKafkaEventDate(), "Kafka Event Date");
    assertNull(actual.getKafkaEventType(), "Kafka Event Type");
    assertNull(actual.getKafkaHeaders(), "Kafka Headers");
    assertNull(actual.getKafkaEventName(), "Kakfa Event Name");
    assertNull(actual.getKafkaMessageId(), "Kafka Message Id");
    assertNull(actual.getKafkaTopic(), "Kafka Topic");
    assertNull(actual.getRequestEndpoint(), "Request Endpoint");
    assertNull(actual.getRequestHeaders(), "Request Headers");
    assertNull(actual.getRequestParameters(), "Request Parameters");
    assertNull(actual.getRequestMethod(), "Request Method");
    assertNull(actual.getResponseCode(), "Response Code");
    assertNull(actual.getServiceCorrelationId(), "Service Correlation Id");
    assertNull(actual.getTraceId(), "Trace Id");
  }
}
