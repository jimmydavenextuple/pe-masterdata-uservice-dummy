package com.nextuple.item.controller;

import com.nextuple.item.domain.events.ItemMasterEvent;
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

  @PostMapping("producer/{topicName}/messages")
  public ResponseEntity<String> produceKafkaMessage(
      @PathVariable String topicName, @RequestBody ItemMasterEvent message) {
    try {
      kafkaTemplate.send(topicName, message);
      return ResponseEntity.ok("Success");
    } catch (Exception e) {
      logger.error("Failed to add item");
      throw e;
    }
  }
}
