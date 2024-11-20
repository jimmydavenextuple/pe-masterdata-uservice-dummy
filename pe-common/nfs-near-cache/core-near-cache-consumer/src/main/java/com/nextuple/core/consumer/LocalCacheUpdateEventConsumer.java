package com.nextuple.core.consumer;

import com.nextuple.core.event.LocalCacheUpdateEvent;
import java.lang.reflect.InvocationTargetException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "entity-listener.kafka.enabled", havingValue = "true")
@KafkaListener(
    topics = "${spring.kafka.consumer.topics.master_data_update_event.name}",
    groupId = "${spring.kafka.consumer.topics.master_data_update_event.group-id}",
    autoStartup = "${kafka-topic-flags.spring.kafka.consumer.topics.master_data_update_event:true}")
@Slf4j
public class LocalCacheUpdateEventConsumer {
  private final LocalCacheUpdateService localCacheUpdateService;

  @KafkaHandler
  public void onLocalCacheUpdateEvent(
      @Payload LocalCacheUpdateEvent localCacheUpdateEvent, @Headers KafkaMessageHeaders headers)
      throws IllegalAccessException,
          ClassNotFoundException,
          NoSuchFieldException,
          InvocationTargetException,
          NoSuchMethodException,
          InstantiationException {
    try {
      log.debug("Received master service event {}", localCacheUpdateEvent);
      localCacheUpdateService.handleLocalCacheUpdate(localCacheUpdateEvent);
    } catch (Exception e) {
      log.error("Error in processing the message received", e);
      throw e;
    }
  }
}
