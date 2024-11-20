package com.nextuple.pe.webhook.producer;

public interface KafkaProducer {

  public void publishFeedToKafka(Object message, String key, String topic);
}
