package com.hbc.item.consumer.serializer;

import com.nextuple.item.Item;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemDeserializer<T extends Item> extends AvroDeserializer<T> {

  public ItemDeserializer(Class<T> targetType) {
    super(targetType);
  }
}
