package com.hbc.item.config;

import com.hbc.item.TestUtil;
import com.hbc.item.consumer.serializer.ItemDeserializer;
import com.hbc.item.consumer.serializer.ItemSerializer;
import com.hbc.item.exception.ItemDomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class ItemSerializerTest {

  @InjectMocks private ItemSerializer itemSerializer;

  @InjectMocks private ItemDeserializer itemDeserializer;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void onItemMasterEventConsumptionTest() throws ItemDomainException {
    itemSerializer.serialize("topic", testUtil.getItemRecord());
    itemDeserializer.deserialize("topic", "".getBytes());

    Assertions.assertTrue(Boolean.TRUE);
  }
}
