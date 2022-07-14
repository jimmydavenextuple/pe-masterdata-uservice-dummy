package com.hbc.item.config;

import com.hbc.item.ItemRecord;
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
    byte[] res = itemSerializer.serialize("topic", null);
    itemDeserializer.deserialize("topic", null);
    ItemRecord record = testUtil.getItemRecord();
    itemSerializer.serialize("topic", record);
    record.setOrgId(null);
    itemSerializer.serialize("topic", record);
    itemSerializer.configure(null, true);
    itemSerializer.close();
    itemDeserializer.deserialize("topic", "".getBytes());

    Assertions.assertNull(res);
  }
}
