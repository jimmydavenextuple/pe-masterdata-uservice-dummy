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
 * Controller for handling Kafka message production related to item master events.
 *
 * <p>This controller provides an API to produce Kafka messages to a specified topic. The message
 * payload contains an item master event, which is sent to the topic provided in the URL path. The
 * controller ensures that the message is successfully sent to Kafka or throws an exception if an
 * error occurs.
 *
 * <p>The controller is tagged with "Kafka Mock APIs" for easy categorization in API documentation.
 *
 * <p>Note: This controller is designed for mock Kafka interactions in a development or testing
 * environment, simulating the production of item-related events to Kafka topics.
 */
@RestController
@Tag(name = "Kafka Mock APIs")
@RequiredArgsConstructor
public class ItemKafkaMockController {

  private static final Logger logger = LoggerFactory.getLogger(ItemKafkaMockController.class);

  @Qualifier("ItemSerializerProducer")
  private final KafkaTemplate<String, Object> kafkaTemplate;

  /**
   * Produces a Kafka message to the specified topic with the provided item master event data.
   *
   * <p>This endpoint processes a request to send an item master event message to a Kafka topic. The
   * provided event data is sent to the topic specified in the URL path. If the message is
   * successfully sent to Kafka, a success response is returned. If an error occurs, an exception is
   * thrown.
   *
   * @param topicName the name of the Kafka topic to which the message will be sent.
   * @param itemMasterEvent the item master event data to be sent as the Kafka message payload.
   * @return a {@link ResponseEntity} containing a success message if the message is produced
   *     successfully.
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
