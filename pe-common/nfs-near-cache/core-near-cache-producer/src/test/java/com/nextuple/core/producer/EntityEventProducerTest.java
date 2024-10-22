package com.nextuple.core.producer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.core.event.LocalCacheUpdateEvent;
import com.nextuple.core.event.LocalCacheUpdateMessage;
import com.nextuple.core.exception.LocalCacheUpdateEventException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EntityEventProducerTest {

  @InjectMocks private EntityEventProducer entityEventProducer;

  @Mock private KafkaTemplate<String, LocalCacheUpdateEvent> kafkaTemplate;

  @Test
  @DisplayName("No message is published when it is disabled")
  void publishEntityDisabledEvent() throws LocalCacheUpdateEventException {
    ReflectionTestUtils.setField(entityEventProducer, "isPublishEnabled", false);
    CompletableFuture<SendResult<String, LocalCacheUpdateEvent>> future = new CompletableFuture<>();

    Map<String, Object> message = new HashMap<>();
    message.put("nodeId", "NODE-ID-01");

    LocalCacheUpdateMessage localCacheUpdateMessage = new LocalCacheUpdateMessage();
    localCacheUpdateMessage.setMessage(message);
    localCacheUpdateMessage.setEntityName("Node");

    entityEventProducer.publishEntityEvent(localCacheUpdateMessage);
    verify(kafkaTemplate, times(0)).send((Message<?>) any());
  }

  @Test
  void publishEntityEvent() throws LocalCacheUpdateEventException {
    ReflectionTestUtils.setField(entityEventProducer, "isPublishEnabled", true);
    CompletableFuture<SendResult<String, LocalCacheUpdateEvent>> future = new CompletableFuture<>();
    when(kafkaTemplate.send((Message<?>) any())).thenReturn(future);

    Map<String, Object> message = new HashMap<>();
    message.put("nodeId", "NODE-ID-01");

    LocalCacheUpdateMessage localCacheUpdateMessage = new LocalCacheUpdateMessage();
    localCacheUpdateMessage.setMessage(message);
    localCacheUpdateMessage.setEntityName("Node");

    entityEventProducer.publishEntityEvent(localCacheUpdateMessage);
    verify(kafkaTemplate, times(1)).send((Message<?>) any());
    ArgumentCaptor<Message<?>> messageCaptor = ArgumentCaptor.forClass(Message.class);
    verify(kafkaTemplate, times(1)).send(messageCaptor.capture());
    LocalCacheUpdateEvent payload = (LocalCacheUpdateEvent) messageCaptor.getValue().getPayload();

    Assertions.assertEquals(message, payload.getLocalCacheUpdateMessage().getMessage());
    Assertions.assertEquals("Node", payload.getLocalCacheUpdateMessage().getEntityName());
  }

  @Test
  void publishEntityEventExceptionTest() {
    ReflectionTestUtils.setField(entityEventProducer, "isPublishEnabled", true);
    when(kafkaTemplate.send((Message<?>) any())).thenThrow(new RuntimeException("error"));

    Map<String, Object> message = new HashMap<>();
    message.put("nodeId", "NODE_ID_01");

    LocalCacheUpdateMessage localCacheUpdateMessage = new LocalCacheUpdateMessage();
    localCacheUpdateMessage.setMessage(message);
    localCacheUpdateMessage.setEntityName("Node");

    Exception exception =
        Assertions.assertThrows(
            LocalCacheUpdateEventException.class,
            () -> entityEventProducer.publishEntityEvent(localCacheUpdateMessage));

    Assertions.assertEquals(
        "Error occurred while publishing LocalCacheUpdate Event", exception.getMessage());
    verify(kafkaTemplate, times(1)).send(((Message<?>) any()));
  }
}
