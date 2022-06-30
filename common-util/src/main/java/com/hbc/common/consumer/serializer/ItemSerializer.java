package com.hbc.item.consumer.serializer;

import com.nextuple.item.Item;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemSerializer<T extends Item> extends AvroSerializer<T> {}
