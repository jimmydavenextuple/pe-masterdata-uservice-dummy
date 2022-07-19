package com.hbc.item.consumer;

import com.hbc.item.domain.events.ItemMasterEvent;
import com.hbc.item.domain.mapper.ItemMapper;
import com.hbc.item.domain.mapper.ItemRecordMapper;
import com.hbc.item.exception.ItemDomainException;
import com.hbc.item.service.ItemService;
import com.hbc.streams.promising.messages.PromisingRecord;
import javax.validation.ConstraintViolationException;
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
  public static final ItemRecordMapper ItemINSTANCE = Mappers.getMapper(ItemRecordMapper.class);
  private final ItemService itemService;

  @KafkaHandler
  public void onItemRecordConsumption(
      @Payload PromisingRecord itemRecord, @Headers KafkaMessageHeaders headers)
      throws ItemDomainException {
    logger.debug("Inside onItemMasterDataConsumption(), event: {} ", itemRecord);
    try {
      itemService.createItem(ItemINSTANCE.convertItemToItemCreationRequest(itemRecord));
    } catch (ConstraintViolationException cve) {
      logger.error(cve.getMessage());
    } catch (Exception e) {
      logger.error("Error while consuming the avro item feed message");
      throw e;
    }
  }

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
