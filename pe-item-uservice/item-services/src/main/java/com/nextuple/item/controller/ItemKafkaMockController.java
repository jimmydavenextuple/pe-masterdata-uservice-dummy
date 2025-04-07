/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.controller;

import com.nextuple.item.controller.docs.ProduceKafkaMessageDoc;
import com.nextuple.item.domain.constants.ItemConstants;
import com.nextuple.item.domain.events.ItemMasterEvent;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mock controller for testing Kafka message production.
 *
 * <p>This controller is for testing purposes only and should not be used in production. It provides
 * endpoints to simulate Kafka message production for item master events.
 *
 * @hidden
 */
@RestController
@Tag(name = "Kafka Mock APIs")
@RequiredArgsConstructor
public class ItemKafkaMockController {

  private static final Logger logger = LoggerFactory.getLogger(ItemKafkaMockController.class);

  @Qualifier("ItemSerializerProducer")
  private final KafkaTemplate<String, Object> kafkaTemplate;

  /**
   * Produces a mock Kafka message for testing purposes.
   *
   * @param topicName The name of the Kafka topic
   * @param itemMasterEvent The item master event to be sent
   * @return A {@link ResponseEntity} with success/failure message
   * @hidden
   */
  @ProduceKafkaMessageDoc
  @PostMapping("producer/{topicName}/messages")
  public ResponseEntity<String> produceKafkaMessage(
      @Parameter(description = ItemConstants.TOPIC_NAME, example = ItemConstants.TOPIC_NAME_EXAMPLE)
          @PathVariable
          String topicName,
      @RequestBody ItemMasterEvent itemMasterEvent) {
    try {
      kafkaTemplate.send(topicName, itemMasterEvent);
      return ResponseEntity.ok("Success");
    } catch (Exception e) {
      logger.error("Failed to produce item master message");
      throw e;
    }
  }
}
