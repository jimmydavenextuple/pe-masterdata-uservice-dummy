package com.hbc.item.consumer.serializer;

import com.hbc.item.ItemRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemDeserializer<T extends ItemRecord> extends AvroDeserializer<T> {

  public ItemDeserializer(Class<T> targetType) {
    super(targetType);
  }
}
