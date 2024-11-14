/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.persistence.exception;

import com.nextuple.carrier.persistence.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarrierServiceDomainExceptionTest {

  @Test
  @DisplayName("Testing CarrierServiceDomainException")
  void constructTest() {
    CarrierServiceDomainException carrierServiceDomainException =
        new CarrierServiceDomainException(
            "test", TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals("test", carrierServiceDomainException.getMessage());
    Assertions.assertEquals(TestUtil.ORG_ID, carrierServiceDomainException.getOrgId());
    Assertions.assertEquals(TestUtil.CARRIER_ID, carrierServiceDomainException.getCarrierId());
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, carrierServiceDomainException.getCarrierServiceId());
  }
}
