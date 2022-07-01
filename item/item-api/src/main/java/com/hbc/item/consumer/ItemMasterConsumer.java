package com.hbc.item.consumer;

import com.hbc.item.ItemRecord;
import com.hbc.item.domain.events.ItemMasterEvent;
import com.hbc.item.domain.mapper.ItemMapper;
import com.hbc.item.service.ItemService;
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
    topics = "${spring.kafka.consumer.topics.item_master.name}",
    groupId = "${spring.kafka.consumer.topics.item_master.group-id}")
public class ItemMasterConsumer {

  private static final Logger logger = LoggerFactory.getLogger(ItemMasterConsumer.class);
  public static final ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);
  private final ItemService itemService;

  @KafkaHandler
  public void onItemRecordConsumption(
      @Payload ItemRecord itemRecord, @Headers KafkaMessageHeaders headers) {
    logger.debug("Inside onItemMasterDataConsumption(), event: {} ", itemRecord);
    try {
      itemService.createItem(INSTANCE.convertItemToItemCreationRequest(itemRecord));
    } catch (Exception e) {
      logger.error(
          "Error while consuming the message: {}, stacktrace: {}", itemRecord, e.getStackTrace());
    }
  }

  @KafkaHandler
  public void onItemMasterEventConsumption(
      @Payload ItemMasterEvent itemMasterEvent, @Headers KafkaMessageHeaders headers) {
    logger.debug("Inside onItemMasterDataConsumption(), event: {} ", itemMasterEvent);
    try {
      itemService.createItem(INSTANCE.convertItemMasterEventToItemCreationRequest(itemMasterEvent));
    } catch (Exception e) {
      logger.error(
          "Error while consuming the message: {}, stacktrace: {}",
          itemMasterEvent,
          e.getStackTrace());
    }
  }
}
