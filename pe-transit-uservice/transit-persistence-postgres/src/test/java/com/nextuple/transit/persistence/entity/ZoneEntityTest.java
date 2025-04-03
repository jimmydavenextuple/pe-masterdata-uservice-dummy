/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.transit.persistence.entity.key.ZoneKey;
import org.junit.jupiter.api.Test;

class ZoneEntityTest {

  @Test
  void zoneEntityTest() {
    ZoneEntity zoneEntity = new ZoneEntity();
    assertNull(zoneEntity.getOrgId());
    assertNull(zoneEntity.getSourceGeozone());
    assertNull(zoneEntity.getDestinationGeozone());
    assertNull(zoneEntity.getCarrierServiceId());
    assertNull(zoneEntity.getZone());
    assertNull(zoneEntity.getCustomAttributes());
  }

  @Test
  void zoneKeyNullDataTest() {
    ZoneKey zoneKey = new ZoneKey();
    assertNull(zoneKey.getOrgId());
    assertNull(zoneKey.getSourceGeozone());
    assertNull(zoneKey.getDestinationGeozone());
  }

  @Test
  void zoneKeyTest() {
    ZoneKey zoneKey = new ZoneKey();
    zoneKey.setOrgId("orgId");
    zoneKey.setCarrierServiceId("carrierServiceId");
    zoneKey.setDestinationGeozone("destinationGeoZone");
    zoneKey.setSourceGeozone("sourceGeoZone");
    assertNotNull(zoneKey.getOrgId());
    assertNotNull(zoneKey.getSourceGeozone());
    assertNotNull(zoneKey.getDestinationGeozone());
  }
}
