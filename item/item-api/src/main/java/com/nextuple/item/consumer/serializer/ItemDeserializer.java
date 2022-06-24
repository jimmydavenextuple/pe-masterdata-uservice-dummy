package com.nextuple.item.consumer.serializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;

@Slf4j
public class ItemDeserializer<T extends SpecificRecordBase> extends AvroDeserializer<T> {

  public ItemDeserializer(Class<T> targetType) {
    super(targetType);
  }
}
