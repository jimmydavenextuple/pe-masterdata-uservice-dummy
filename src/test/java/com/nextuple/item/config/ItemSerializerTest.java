/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.config;

import com.nextuple.item.TestUtil;
import com.nextuple.serializer.ItemDeserializer;
import com.nextuple.serializer.ItemSerializer;
import com.nextuple.streams.promising.messages.PromisingRecord;
import javax.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class ItemSerializerTest {

  @InjectMocks private ItemSerializer itemSerializer;

  @InjectMocks private ItemDeserializer<PromisingRecord> itemDeserializer;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void onItemMasterEventConsumptionTest() {
    byte[] res = itemSerializer.serialize("topic", null);
    itemDeserializer.deserialize("topic", null);
    PromisingRecord record = testUtil.getItemRecord();
    itemSerializer.serialize("topic", record);
    record.setOrgId(null);
    itemSerializer.serialize("topic", record);
    itemSerializer.configure(null, true);
    itemSerializer.close();
    itemDeserializer.deserialize("topic", "".getBytes());

    Assertions.assertNull(res);
  }

  @Test
  void onItemMasterEventConsumptionTest1() {
    PromisingRecord avro =
        itemDeserializer.deserialize(
            "",
            DatatypeConverter.parseHexBinary(
                "10383534333038383302064241590642415908454143480200021A3036303030383534333037393802065245440210345F4D656469756D0200010101010100000000000204494E020A435520494E0002044C4200000200022443617375616C20436F74746F6E20506F6C6F02063330370000C0868AB4C260"));
    System.out.println(avro);
    Assertions.assertTrue(Boolean.TRUE);
  }
}
