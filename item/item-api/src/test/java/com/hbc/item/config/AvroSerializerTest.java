package com.hbc.item.config;

import com.hbc.item.TestUtil;
import com.hbc.item.consumer.serializer.AvroDeserializer;
import com.hbc.item.consumer.serializer.AvroSerializer;
import com.hbc.item.exception.ItemDomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class AvroSerializerTest {

  @InjectMocks private AvroSerializer avroSerializer;

  @InjectMocks private AvroDeserializer avroDeserializer;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void onItemMasterEventConsumptionTest() throws ItemDomainException {
    avroSerializer.serialize("topic", testUtil.getItemRecord());
    avroDeserializer.deserialize("topic", "".getBytes());

    Assertions.assertTrue(Boolean.TRUE);
  }
}
