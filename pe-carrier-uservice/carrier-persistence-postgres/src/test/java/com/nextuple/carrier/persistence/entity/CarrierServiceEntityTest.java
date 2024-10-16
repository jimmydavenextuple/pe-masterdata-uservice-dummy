/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.carrier.persistence.TestUtil;
import com.nextuple.carrier.persistence.entity.key.CarrierServiceKey;
import org.junit.jupiter.api.Test;

class CarrierServiceEntityTest {
  @Test
  void carrierServiceEntityTest() {
    CarrierServiceEntity carrierServiceEntity = new CarrierServiceEntity();
    assertNull(carrierServiceEntity.getOrgId());
    assertNull(carrierServiceEntity.getCarrierId());
    assertNull(carrierServiceEntity.getCarrierServiceId());
    assertNull(carrierServiceEntity.getCarrierName());
    assertNull(carrierServiceEntity.getServiceName());
    assertNull(carrierServiceEntity.getServiceOptions());
  }

  @Test
  void carrierServiceKeyNullDataTest() {
    CarrierServiceKey carrierServiceKey = new CarrierServiceKey();
    assertNull(carrierServiceKey.getCarrierServiceId());
    assertNull(carrierServiceKey.getCarrierId());
    assertNull(carrierServiceKey.getOrgId());
  }

  @Test
  void carrierServiceKeyTest() {
    CarrierServiceKey carrierServiceKey =
        new CarrierServiceKey(TestUtil.ORG_ID, TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID);
    assertNotNull(carrierServiceKey.getCarrierServiceId());
    assertNotNull(carrierServiceKey.getCarrierId());
    assertNotNull(carrierServiceKey.getOrgId());
  }
}
