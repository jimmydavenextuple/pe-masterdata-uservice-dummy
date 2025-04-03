/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.transit.persistence.entity.key.TransitBufferV2Key;
import org.junit.jupiter.api.Test;

class TransitBufferV2EntityTest {
  @Test
  void transitBufferV2EntityTest() {
    TransitBufferV2Entity transitBufferV2EntityTest = new TransitBufferV2Entity();
    assertNull(transitBufferV2EntityTest.getId());
    assertNull(transitBufferV2EntityTest.getOrgId());
    assertNull(transitBufferV2EntityTest.getCarrierServiceId());
    assertNull(transitBufferV2EntityTest.getSourceGeozone());
    assertNull(transitBufferV2EntityTest.getDestinationGeozone());
    assertNull(transitBufferV2EntityTest.getBufferDays());
    assertNull(transitBufferV2EntityTest.getBufferStartDate());
    assertNull(transitBufferV2EntityTest.getBufferEndDate());
    assertNull(transitBufferV2EntityTest.getTransitBufferConfigRequestId());
    assertNull(transitBufferV2EntityTest.getCustomAttributes());
  }

  @Test
  void transitBufferV2KeyNullDataTest() {
    TransitBufferV2Key transitBufferV2Key = new TransitBufferV2Key();
    assertNull(transitBufferV2Key.getId());
  }

  @Test
  void transitBufferV2KeyTest() {
    TransitBufferV2Key transitBufferV2Key = new TransitBufferV2Key();
    transitBufferV2Key.setId(123L);
    assertNotNull(transitBufferV2Key.getId());
  }
}
