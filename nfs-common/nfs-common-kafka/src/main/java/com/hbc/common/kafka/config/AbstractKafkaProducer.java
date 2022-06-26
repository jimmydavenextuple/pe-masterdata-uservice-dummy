package com.hbc.common.kafka.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class AbstractKafkaProducer<K, V> {

  @Autowired private KafkaTemplate<K, V> kafkaTemplate;

  @Getter @Setter protected String topicName;

  public void sendMessage(K key, V msg) {
    kafkaTemplate.send(topicName, msg);
  }
}
