package com.nextuple.item.consumer.serializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;

@Slf4j
public class ItemSerializer<T extends SpecificRecordBase> extends AvroSerializer<T> {}
