package com.hbc.item.controller;

import com.hbc.item.ItemRecord;
import com.hbc.item.domain.events.ItemMasterEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaMockController {

  private static final Logger logger = LoggerFactory.getLogger(KafkaMockController.class);
  private final KafkaTemplate<String, Object> kafkaTemplate;

  @PostMapping("producer/avro/{topicName}/messages")
  public ResponseEntity<String> produceAvroKafkaMessage(
      @PathVariable String topicName, @RequestBody ItemRecord itemRecord) {
    try {
      kafkaTemplate.send(topicName, itemRecord);
      return ResponseEntity.ok("Success");
    } catch (Exception e) {
      logger.error("Failed to produce avro item master message");
      throw e;
    }
  }

  @PostMapping("producer/{topicName}/messages")
  public ResponseEntity<String> produceKafkaMessage(
      @PathVariable String topicName, @RequestBody ItemMasterEvent itemMasterEvent) {
    try {
      kafkaTemplate.send(topicName, itemMasterEvent);
      return ResponseEntity.ok("Success");
    } catch (Exception e) {
      logger.error("Failed to produce item master message");
      throw e;
    }
  }
}
