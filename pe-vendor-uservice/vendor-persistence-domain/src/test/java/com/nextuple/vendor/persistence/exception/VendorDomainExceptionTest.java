/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.persistence.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class VendorDomainExceptionTest {

  public static final String VENDOR_ID = "vendor-1";
  public static final String ORG_ID = "org-1";

  @Test
  @DisplayName("Testing VendorDomainException")
  void constructTest() {
    VendorDomainException vendorDomainException =
        new VendorDomainException("test", VENDOR_ID, ORG_ID);
    Assertions.assertEquals("test", vendorDomainException.getMessage());
    Assertions.assertEquals(VENDOR_ID, vendorDomainException.getVendorId());
    Assertions.assertEquals(ORG_ID, vendorDomainException.getOrgId());
  }
}
