/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nextuple.transit.cache.domain.ZoneCacheValue;
import com.nextuple.transit.domain.outbound.ZoneResponse;
import com.nextuple.transit.spring.cache.util.TestUtil;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ZoneCacheValueTest {
  @InjectMocks private TestUtil testUtil;

  @Test
  void testGetZoneMap() {
    ZoneCacheValue zoneCacheValue =
        ZoneCacheValue.builder()
            .zoneResponseList(testUtil.getZoneCacheValue().getZoneResponseList())
            .build();

    Map<String, ZoneResponse> zoneMap = zoneCacheValue.getZoneMap(TestUtil.ORG_ID);

    Assertions.assertNotNull(zoneMap);
    Assertions.assertFalse(zoneMap.isEmpty());
  }

  @Test
  void testGetZonesKey() {
    ZoneCacheValue zoneCacheValue = ZoneCacheValue.builder().build();

    String zonesKey =
        zoneCacheValue.getZonesKey(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertNotNull(zonesKey);
    assertEquals("org-1-source-geozone-1-destination-geozone-1-carrier-service-id-1", zonesKey);
  }
}
