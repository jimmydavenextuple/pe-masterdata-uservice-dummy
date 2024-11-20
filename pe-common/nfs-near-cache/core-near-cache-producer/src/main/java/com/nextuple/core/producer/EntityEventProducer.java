package com.nextuple.core.producer;

import com.nextuple.core.event.LocalCacheUpdateEvent;
import com.nextuple.core.event.LocalCacheUpdateMessage;
import com.nextuple.core.exception.LocalCacheUpdateEventException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
  @Autowired
  @Qualifier("CacheUpdateProducer")
  KafkaTemplate<String, LocalCacheUpdateEvent> kafkaTemplate;

  @Value("${spring.kafka.producer.topics.master_data_update_event:null}")
  private String masterDataTopic;

  @Value("${kafka-topic-flags.spring.kafka.producer.topics.master_data_update_event:true}")
  private boolean isPublishEnabled;

  public void publishEntityEvent(LocalCacheUpdateMessage localCacheUpdateMessage)
      throws LocalCacheUpdateEventException {
    if (Boolean.FALSE.equals(isPublishEnabled())) return;
    var localCacheUpdateEvent = new LocalCacheUpdateEvent();
    localCacheUpdateEvent.setLocalCacheUpdateMessage(localCacheUpdateMessage);

    try {
      kafkaTemplate
          .send(
              MessageBuilder.withPayload(localCacheUpdateEvent)
                  .setHeader(KafkaHeaders.TOPIC, masterDataTopic)
                  .build())
          .whenComplete(
              (sr, ex) -> {
                if (ex != null) {
                  log.error(
                      "Error in publishing LocalCacheUpdate Event into kafka with message: {}",
                      localCacheUpdateEvent.getLocalCacheUpdateMessage(),
                      ex);

                } else {
                  log.debug(
                      "Published LocalCacheUpdate Event into kafka with message: {}, topic is {}, partition is {}",
                      localCacheUpdateEvent.getLocalCacheUpdateMessage(),
                      sr.getRecordMetadata().topic(),
                      sr.getRecordMetadata().partition());
                }
              });

    } catch (Exception e) {
      log.error("Error occurred while publishing LocalCacheUpdate Event", e);
      throw new LocalCacheUpdateEventException(
          "Error occurred while publishing LocalCacheUpdate Event",
          localCacheUpdateMessage.getEntityName());
    }
  }

  private boolean isPublishEnabled() {
    if (Boolean.TRUE.equals(isPublishEnabled)) return true;
    log.warn("Not publishing master data near cache update messages since it is disabled.");
    return false;
  }
}
