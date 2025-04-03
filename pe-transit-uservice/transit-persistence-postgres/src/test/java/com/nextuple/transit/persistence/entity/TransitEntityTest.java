/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.transit.persistence.entity.key.TransitKey;
import org.junit.jupiter.api.Test;

class TransitEntityTest {

  @Test
  void transitEntityTest() {
    TransitEntity transitEntity = new TransitEntity();
    assertNull(transitEntity.getOrgId());
    assertNull(transitEntity.getSourceGeozone());
    assertNull(transitEntity.getDestinationGeozone());
    assertNull(transitEntity.getCarrierServiceId());
    assertNull(transitEntity.getTransitDays());
    assertNull(transitEntity.getBufferDays());
    assertNull(transitEntity.getBufferStartDate());
    assertNull(transitEntity.getBufferEndDate());
    assertNull(transitEntity.getCustomAttributes());
  }

  @Test
  void transitKeyNullDataTest() {
    TransitKey transitKey = new TransitKey();
    assertNull(transitKey.getOrgId());
    assertNull(transitKey.getSourceGeozone());
    assertNull(transitKey.getDestinationGeozone());
  }

  @Test
  void transitKeyTest() {
    TransitKey transitKey = new TransitKey();
    transitKey.setOrgId("orgId");
    transitKey.setSourceGeozone("sourceGeoZone");
    transitKey.setDestinationGeozone("destinationGeoZone");
    assertNotNull(transitKey.getOrgId());
    assertNotNull(transitKey.getSourceGeozone());
    assertNotNull(transitKey.getDestinationGeozone());
  }
}
