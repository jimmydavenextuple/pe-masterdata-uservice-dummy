/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.consumer;

import com.nextuple.item.domain.events.ItemMasterEvent;
import com.nextuple.item.domain.mapper.ItemMapper;
import com.nextuple.item.persistence.exception.ItemDomainException;
import com.nextuple.item.service.ItemService;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@KafkaListener(
    topics = "#{'${spring.kafka.consumer-item.topics.item_master.names}'.split(',')}",
    groupId = "${spring.kafka.consumer-item.topics.item_master.group-id}",
    autoStartup = "#{'${spring.kafka.consumer-item.type}' == 'avro'}",
    containerFactory = "ItemDeserializerConsumer")
public class ItemMasterConsumer {

  private static final Logger logger = LoggerFactory.getLogger(ItemMasterConsumer.class);
  public static final ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);
  private final ItemService itemService;

  @KafkaHandler
  public void onItemMasterEventConsumption(
      @Payload ItemMasterEvent itemMasterEvent, @Headers KafkaMessageHeaders headers)
      throws ItemDomainException {
    logger.debug("Inside onItemMasterDataConsumption(), event: {} ", itemMasterEvent);
    try {
      itemService.createItem(INSTANCE.convertItemMasterEventToItemCreationRequest(itemMasterEvent));
    } catch (ConstraintViolationException cve) {
      logger.error(cve.getMessage());
    } catch (Exception e) {
      logger.error("Error while consuming the item feed message");
      throw e;
    }
  }
}
