package com.nextuple.pe.webhook.producer.impl;

import com.nextuple.pe.webhook.producer.KafkaProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
// Added this
@AllArgsConstructor
public class KafkaProducerImpl implements KafkaProducer {
  @Qualifier("ItemSerializerProducer")
  @Autowired
  public KafkaTemplate<String, Object> messageFeed;

  @Override
  public void publishFeedToKafka(Object message, String key, String topic) {
    log.debug("publishing message {} to topic {}", message, topic);
    messageFeed.send(topic, key, message);
  }
}
