package com.nextuple.core.producer;

import com.nextuple.core.event.LocalCacheUpdateEvent;
import com.nextuple.core.event.LocalCacheUpdateMessage;
import com.nextuple.core.exception.LocalCacheUpdateEventException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EntityEventProducer {

  @Autowired KafkaTemplate<String, LocalCacheUpdateEvent> kafkaTemplate;

  public void publishEntityEvent(LocalCacheUpdateMessage localCacheUpdateMessage)
      throws LocalCacheUpdateEventException {

    LocalCacheUpdateEvent localCacheUpdateEvent = new LocalCacheUpdateEvent();
    localCacheUpdateEvent.setLocalCacheUpdateMessage(localCacheUpdateMessage);

    try {
      kafkaTemplate
          .send(
              MessageBuilder.withPayload(localCacheUpdateEvent)
                  .setHeader(KafkaHeaders.TOPIC, "node_event")
                  .build())
          .addCallback(
              e ->
                  log.info(
                      "Published LocalCacheUpdate Event into kafka with message: {}, topic is {}, partition is {}",
                      localCacheUpdateEvent.getLocalCacheUpdateMessage(),
                      e.getRecordMetadata().topic(),
                      e.getRecordMetadata().partition()),
              e ->
                  log.error(
                      "Error in publishing LocalCacheUpdate Event into kafka with message: {}",
                      localCacheUpdateEvent.getLocalCacheUpdateMessage(),
                      e));
    } catch (Exception e) {
      log.error("Error occurred while publishing LocalCacheUpdate Event", e);
      throw new LocalCacheUpdateEventException(
          "Error occurred while publishing LocalCacheUpdate Event",
          localCacheUpdateMessage.getEntityName());
    }
  }
}
