package com.hbc.item.consumer.serializer;

import com.hbc.item.ItemRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemSerializer<T extends ItemRecord> extends AvroSerializer<T> {}
