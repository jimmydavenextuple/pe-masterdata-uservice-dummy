/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.transit.persistence.TestUtil;
import com.nextuple.transit.persistence.domain.key.TransitDomainKey;
import org.junit.jupiter.api.Test;

class TransitDomainKeyTest {

  @Test
  void transitIdNullTest() {
    TransitDomainKey transitId = new TransitDomainKey();
    assertNull(transitId.getCarrierServiceId());
    assertNull(transitId.getDestinationGeozone());
    assertNull(transitId.getOrgId());
    assertNull(transitId.getSourceGeozone());
  }

  @Test
  void transitIdTest() {
    TransitDomainKey transitId =
        new TransitDomainKey(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);
    assertNotNull(transitId.getCarrierServiceId());
    assertNotNull(transitId.getDestinationGeozone());
    assertNotNull(transitId.getOrgId());
    assertNotNull(transitId.getSourceGeozone());
  }
}
