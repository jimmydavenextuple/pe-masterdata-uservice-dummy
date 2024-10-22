package com.nextuple.common.aop;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.nextuple.common.constants.CommonConstants;
import com.nextuple.common.context.CurrentThreadContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;

@ExtendWith(MockitoExtension.class)
public class KafkaHandlerAspectTest {

  @Mock JoinPoint joinPoint;
  @Mock KafkaMessageHeaders kafkaMessageHeaders;

  @DisplayName("Should initialize Log Context from from kafkaMessageHeaders")
  @Test
  void initializeContextTest() {
    String username = "username";
    String userRole = "userRole";
    String userLocale = "userLocale";
    String tenantId = "tenantId";
    String apiKey = "apiKey";
    String host = "host";
    String receivedTopic = "receivedTopic";
    String eventType = "eventType";
    String eventName = "eventName";
    long eventDate = 1L;
    UUID id = new UUID(123, 123);

    Map<String, Object> headers = new HashMap<>();
    headers.put(CommonConstants.HEADER_USER, username);
    headers.put(CommonConstants.HEADER_ROLE, userRole);
    headers.put(CommonConstants.HEADER_USER_LOCALE, userLocale);
    headers.put(CommonConstants.HEADER_TENANT_ID, tenantId);
    headers.put(host, host);
    Set<Map.Entry<String, Object>> set = headers.entrySet();

    when(kafkaMessageHeaders.entrySet()).thenReturn(set);
    when(kafkaMessageHeaders.containsKey(KafkaHeaders.RECEIVED_TOPIC)).thenReturn(true);
    when(kafkaMessageHeaders.get(KafkaHeaders.RECEIVED_TOPIC)).thenReturn(receivedTopic);
    when(kafkaMessageHeaders.containsKey(CommonConstants.HEADER_TENANT_ID)).thenReturn(true);
    when(kafkaMessageHeaders.get(CommonConstants.HEADER_TENANT_ID)).thenReturn(tenantId);
    when(kafkaMessageHeaders.containsKey(CommonConstants.HEADER_API_KEY)).thenReturn(true);
    when(kafkaMessageHeaders.get(CommonConstants.HEADER_API_KEY)).thenReturn(apiKey);
    when(kafkaMessageHeaders.containsKey(CommonConstants.HEADER_EVENT_NAME)).thenReturn(true);
    when(kafkaMessageHeaders.get(CommonConstants.HEADER_EVENT_NAME)).thenReturn(eventName);
    when(kafkaMessageHeaders.containsKey(CommonConstants.HEADER_EVENT_TYPE)).thenReturn(true);
    when(kafkaMessageHeaders.get(CommonConstants.HEADER_EVENT_TYPE)).thenReturn(eventType);
    when(kafkaMessageHeaders.containsKey(CommonConstants.HEADER_EVENT_DATE)).thenReturn(true);
    when(kafkaMessageHeaders.get(CommonConstants.HEADER_EVENT_DATE)).thenReturn(eventDate);
    when(kafkaMessageHeaders.getId()).thenReturn(id);

    when(joinPoint.getArgs()).thenReturn(new KafkaMessageHeaders[] {kafkaMessageHeaders});

    KafkaHandlerAspect kafkaHandlerAspect = new KafkaHandlerAspect();
    kafkaHandlerAspect.initializeContext(joinPoint);

    assertEquals(username, CurrentThreadContext.getLogContext().getUsername(), "username");
    assertEquals(userRole, CurrentThreadContext.getLogContext().getUserRole(), "user role");
    assertEquals(userLocale, CurrentThreadContext.getLogContext().getUserLocale(), "user Locale");
    assertEquals(tenantId, CurrentThreadContext.getLogContext().getTenantId(), "Tenant Id");
    assertEquals(apiKey, CurrentThreadContext.getLogContext().getApiKey(), "Api Key");
    assertEquals(
        eventName, CurrentThreadContext.getLogContext().getKafkaEventName(), "Kafka Event Name");
    assertEquals(
        eventType, CurrentThreadContext.getLogContext().getKafkaEventType(), "Kafka Event Type");
    assertEquals(
        eventDate, CurrentThreadContext.getLogContext().getKafkaEventDate(), "Kafka Event Date");
    assertEquals(
        id.toString(),
        CurrentThreadContext.getLogContext().getKafkaMessageId(),
        "Kafka Message Id");
  }

  @DisplayName("Should initialize Log Context from from kafkaMessageHeaders")
  @Test
  void initializeContextNoKafka() {
    when(joinPoint.getArgs()).thenReturn(new KafkaMessageHeaders[1]);

    KafkaHandlerAspect kafkaHandlerAspect = new KafkaHandlerAspect();
    kafkaHandlerAspect.initializeContext(joinPoint);
    assertEquals(
        "unknown", CurrentThreadContext.getLogContext().getKafkaMessageId(), "KafkaMessageId");
    assertEquals("unknown", CurrentThreadContext.getLogContext().getKafkaTopic(), "Kafka Topic");
  }

  @DisplayName("Should clean logcontext and clear MDC")
  @Test
  void closeContext() {
    KafkaHandlerAspect kafkaHandlerAspect = new KafkaHandlerAspect();
    assertDoesNotThrow(
        () -> {
          kafkaHandlerAspect.closeContext(joinPoint);
        },
        "Should not throw exception");
  }
}
