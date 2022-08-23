package com.hbc.core.producer;

import com.hbc.core.event.LocalCacheUpdateEvent;
import com.hbc.core.event.LocalCacheUpdateMessage;
import com.hbc.core.exception.LocalCacheUpdateEventException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(value = "entity-listener.kafka.enabled", havingValue = "true")
public class EntityEventProducer {

  @Autowired KafkaTemplate<String, LocalCacheUpdateEvent> kafkaTemplate;

  @Value("${spring.kafka.producer.topics.master_data_update_event}")
  private String masterDataTopic;

  public void publishEntityEvent(LocalCacheUpdateMessage localCacheUpdateMessage)
      throws LocalCacheUpdateEventException {

    var localCacheUpdateEvent = new LocalCacheUpdateEvent();
    localCacheUpdateEvent.setLocalCacheUpdateMessage(localCacheUpdateMessage);

    try {
      kafkaTemplate
          .send(
              MessageBuilder.withPayload(localCacheUpdateEvent)
                  .setHeader(KafkaHeaders.TOPIC, masterDataTopic)
                  .build())
          .addCallback(
              e ->
                  log.debug(
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
